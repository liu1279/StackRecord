package stack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Until {
    public static final int spacePerLevel = 4;

    public static String getLevelBlanks(int level) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < spacePerLevel * level; i++) {
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    public static <T> void putDifferElementToMap(HashMap<String, List<T>> map, String key, T value) {
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<>(Arrays.asList(value)));
        } else if (!map.get(key).contains(value)) {
            map.get(key).add(value);
        }
    }

    public static void formPrintByStyle(LineData line, int level, StringBuilder printBuffer, boolean isPrint, int printedLevel,
                                        boolean isFirstLine, boolean isOverLoad, int printStyle) {
        String strLevel = (level > 9 ? "" : "0") + level;
        String me = line.getMethod();
        String file = line.getFileName();
        String noticeInfo = isPrint ? " 【已在level " + printedLevel + "打印】" : "";
        if (isOverLoad) {
            noticeInfo += "【重写】";
        }
        String stackPrefix = level == 0 ? "\n" : "";

        String levelBlanks = Until.getLevelBlanks(level);
        String comment = line.getComment().equals("") ? "" : " //" + line.getComment();
        if (printStyle == 0) {
            if (isFirstLine) {
                printBuffer.append(stackPrefix + levelBlanks + strLevel + "-> " + me + noticeInfo + comment + "\n");
            }
        } else if (printStyle == 1) {
            if (isFirstLine || level != 0) {
                printBuffer.append(stackPrefix + levelBlanks + strLevel + "-> " + line.getLineDetail() + noticeInfo + comment + "\n");
            }

        } else {
            System.out.println("请定义你的style！");
            return;
        }
    }
}
