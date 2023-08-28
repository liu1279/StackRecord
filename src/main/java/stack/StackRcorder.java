package stack;

import com.intellij.debugger.engine.JavaStackFrame;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.xdebugger.impl.frame.XDebuggerFramesList;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class StackRcorder extends AnAction {
    // 记录方法对应的line
    public static final HashMap<String, List<LineData>> METHOD_LINES_MAP = new HashMap<>();
    // 记录方法对应的参数，用来处理重载
    public static final HashMap<String, OverLoadInfo> METHOD_MAP_ID = new HashMap<>();
    // 记录文件某行跳转到下一个方法, 重写导致可能调到多处
    public static final HashMap<String, List<String>> LINE_TO_METHODS_MAP = new HashMap<>();
    // 记录已经打印的方法，避免重复打印，key为方法名，value为level
    public static final HashMap<String, Integer> PRINTED_METHODS = new HashMap<>();
    // 记录所有的开始方法
    public static final List<LineData> ALL_START_METHODS = new ArrayList<>();
    public static int printStyle = 1;
    public static boolean needComment = true;

    @Override
    public void actionPerformed(AnActionEvent e) {
        XDebuggerFramesList framesList = e.getData(DataKey.create("FRAMES_LIST"));
        PauseEventListener.framesList = framesList;
        storeLineData(framesList);
    }

    public static void storeLineData(XDebuggerFramesList framesList) {
        List<JavaStackFrame> items = (List<JavaStackFrame>) framesList.getModel().getItems();
        if (items.isEmpty()) {
            return;
        }
        List<LineData> lineDataList = new ArrayList<>();
        for (JavaStackFrame item : items) {
            lineDataList.add(0, new LineData(item));
        }
        if (!ALL_START_METHODS.contains(lineDataList.get(0))) {
            ALL_START_METHODS.add(lineDataList.get(0));
        }

        LineData thisBreakPoint = lineDataList.get(lineDataList.size() - 1);

        if (!RecordWindow.addUniquePoint(thisBreakPoint)) {
            lineDataList.remove(lineDataList.size() - 1);
        }

        thisBreakPoint = lineDataList.get(lineDataList.size() - 1);
        for (int i = 0; i < lineDataList.size() - 1; i++) {
            LineData thisLineData = lineDataList.get(i);
            LineData nextLineData = lineDataList.get(i + 1);
            Until.putDifferElementToMap(METHOD_LINES_MAP, thisLineData.getMethodId(), thisLineData);
            Until.putDifferElementToMap(LINE_TO_METHODS_MAP, thisLineData.getNextMethodRef(), nextLineData.getMethodId());
        }

        Until.putDifferElementToMap(METHOD_LINES_MAP, thisBreakPoint.getMethodId(), thisBreakPoint);
    }

    public static String GetStackFigure(String customStartMethodString, String customEndMethodString, int printStyle) {
        StackRcorder.printStyle = printStyle;
        for (List<LineData> lines : METHOD_LINES_MAP.values()) {
            lines.sort(new Comparator<LineData>() {
                @Override
                public int compare(LineData o1, LineData o2) {
                    return Integer.parseInt(o1.getFileNum()) - Integer.parseInt(o2.getFileNum());
                }
            });
        }

        // 获取起始方法，用于过滤
        List<String> customStartMethods = Arrays.stream(customStartMethodString.split(",")).map(String::strip).toList();
        if (customStartMethods.isEmpty() || StringUtils.isBlank(customStartMethods.get(0))) {
            METHOD_LINES_MAP.put("", ALL_START_METHODS);
        } else {
            METHOD_LINES_MAP.put("", new ArrayList<>());
            for (String customStartMethod : customStartMethods) {
                for (String methodId : METHOD_LINES_MAP.keySet()) {
                    if (customStartMethod.equals(methodId.split(" ")[0])) {
                        METHOD_LINES_MAP.get("").addAll(METHOD_LINES_MAP.get(methodId));
                    }
                }
            }
            if (METHOD_LINES_MAP.get("").isEmpty()) {
                System.out.println("请输入正确的起始方法！");
                return null;
            }
        }
        StringBuilder printBuffer = new StringBuilder();
        PRINTED_METHODS.clear();
        METHOD_MAP_ID.clear();
        HashSet<String> thisStackPrined = new HashSet<>();
        formPrint("", 0, printBuffer, false, thisStackPrined);

        List<String> endMethods = Arrays.stream(customEndMethodString.split(",")).map(String::strip).toList();
        if (endMethods.isEmpty() || StringUtils.isBlank(endMethods.get(0))) {
            return printBuffer.toString();
        }
        String[] lines = printBuffer.toString().split("\n");

        StringBuilder newBuilder = new StringBuilder();
        Map<String, Integer> endNumDict = endMethods.stream().collect(Collectors.toMap(item -> item, item -> 0));
        for (int i = 0; i < lines.length; i++) {
            if (updateNum(endNumDict, lines, lines.length - 1 - i)) {
                newBuilder.insert(0, "\n");
                newBuilder.insert(0, lines[lines.length - 1 - i]);
            }
        }
        return newBuilder.toString();
    }

    private static boolean updateNum(Map<String, Integer> endNumDict, String[] lines, int linesIndex) {
        boolean isUpdate = false;
        String line = lines[linesIndex];
        if (StringUtils.isBlank(line)) {
            return isUpdate;
        }
        int directIndex = line.indexOf("->");
        int curNum = Integer.parseInt(line.substring(directIndex - 2, directIndex));
        for (Map.Entry<String, Integer> entry : endNumDict.entrySet()) {
            if (line.contains(entry.getKey()) || curNum + 1 == entry.getValue()) {
                endNumDict.put(entry.getKey(), curNum);
                isUpdate = true;
            }
            if (line.contains(entry.getKey())) {
                lines[linesIndex] = line.substring(0, directIndex + 3) + "【目标】" + line.substring(directIndex + 3);
            }
        }
        return isUpdate;
    }


    private static void formPrint(String method, int level, StringBuilder printBuffer,
                                  boolean isOverWrite, HashSet<String> thisStackPrined) {
        List<LineData> lines = METHOD_LINES_MAP.getOrDefault(method, null);
        if (lines == null || lines.isEmpty()) {
            return;
        }
        boolean isFirstLine = true;
        HashSet<String> thisMethodPrinted = new HashSet<>();
        for (LineData line : lines) {
            String nextMeRef = line.getNextMethodRef();
            boolean isPrint = PRINTED_METHODS.containsKey(nextMeRef);
            boolean isThisStackPrint = thisStackPrined.contains(nextMeRef);
            boolean isOverLoad = false;
            int overLevel = -1;
            if (METHOD_MAP_ID.containsKey(line.getFileMethod())) {
                String recordMethodId = METHOD_MAP_ID.get(line.getFileMethod()).getMethodId();
                if (!recordMethodId.equalsIgnoreCase(line.getMethodId())) {
                    isOverLoad = true;
                    overLevel = METHOD_MAP_ID.get(line.getFileMethod()).getLevel();
                }
            } else {
                METHOD_MAP_ID.put(line.getFileMethod(), new OverLoadInfo(line.getMethodId(), level));
            }
            int printedLevel = isPrint ? PRINTED_METHODS.get(nextMeRef) : -1;
            Until.formPrintByStyle(line, level, printBuffer, isPrint, printedLevel,
                    isFirstLine, isOverWrite, printStyle, isThisStackPrint, isOverLoad, overLevel);
            isFirstLine = false;
            if (!isThisStackPrint) {
                thisStackPrined.add(nextMeRef);
                thisMethodPrinted.add(nextMeRef);
            }
            if (!isPrint) {
                PRINTED_METHODS.put(nextMeRef, level);
                if (LINE_TO_METHODS_MAP.containsKey(nextMeRef)) {
                    for (String nextMethod : LINE_TO_METHODS_MAP.get(nextMeRef)) {
                        formPrint(nextMethod, level + 1, printBuffer, LINE_TO_METHODS_MAP.get(nextMeRef).size() > 1, thisStackPrined);
                    }
                }
            }
        }
        thisStackPrined.removeAll(thisMethodPrinted);
    }
    public static void clear() {
        StackRcorder.METHOD_LINES_MAP.clear();
        StackRcorder.LINE_TO_METHODS_MAP.clear();
        StackRcorder.ALL_START_METHODS.clear();
        StackRcorder.PRINTED_METHODS.clear();
        StackRcorder.METHOD_MAP_ID.clear();
    }
}

