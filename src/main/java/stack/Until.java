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
                                        boolean isFirstLine, boolean isOverWrite, int printStyle,
                                        boolean isThisStackPrint, boolean isOverLoad, int overLevel) {
        String strLevel = (level > 9 ? "" : "0") + level;
        String me = line.getMethod();
        String noticeInfo = "";
        if (isThisStackPrint) {
            noticeInfo += " [[ recursion -> " + printedLevel + " ]]";
        } else if (isPrint) {
            noticeInfo += " [[ repeat -> " + printedLevel + " ]]";
        }

        if (isOverLoad) {
            noticeInfo += " [[ overLoad -> " + overLevel + " ]]";
        }

        if (isOverWrite) {
            noticeInfo += " [[ overWrite ]]";
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
            System.out.println("please define your style！");
            return;
        }
    }
}
