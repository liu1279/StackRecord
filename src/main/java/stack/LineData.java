package stack;

import com.intellij.debugger.engine.JavaStackFrame;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class LineData {
    private String lineDetail;
    private String method;
    private String methodId;
    private String nextMethodRef;
    private String fileName;
    private String fileNum;
    private String comment;
    private String fileMethod;
    private List<String> argTypeNames;
    private String allInfo;

    public LineData(JavaStackFrame javaStackFrame) {
        method = javaStackFrame.getDescriptor().getName();
        fileName = javaStackFrame.getSourcePosition().getFile().getName().split("\\.")[0];
        fileNum = String.valueOf(javaStackFrame.getSourcePosition().getLine() + 1);
        argTypeNames = javaStackFrame.getDescriptor().getMethod().argumentTypeNames();
        fileMethod = method + " " + fileName;
        methodId = method + " " + fileName + " " + argTypeNames.toString();
        nextMethodRef = fileName + ":" + fileNum;
        comment = "";
        lineDetail = method + " " + fileName + ":" + fileNum;
        allInfo = method + " " + fileName + ":" + fileNum + " " + argTypeNames.toString();

    }

    @Override
    public boolean equals(Object o) {
        return ((LineData) o).getMethodId().equals(this.allInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allInfo);
    }

    public String[] toArray() {
        String[] array = new String[4];
        array[0] = method;
        array[1] = fileName;
        array[2] = fileNum;
        array[3] = comment;
        return array;
    }

    public boolean isSameMethodAs(LineData thisBreakPoint) {
        return thisBreakPoint.methodId.equals(this.methodId);
    }
}
