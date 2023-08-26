package stack;

import com.intellij.debugger.engine.JavaStackFrame;
import lombok.Data;

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

    public LineData(JavaStackFrame javaStackFrame) {
        method = javaStackFrame.getDescriptor().getName();
        fileName = javaStackFrame.getSourcePosition().getFile().getName().split("\\.")[0];
        fileNum = String.valueOf(javaStackFrame.getSourcePosition().getLine() + 1);
        methodId = method + " " + fileName;
        nextMethodRef = fileName + ":" + fileNum;
        comment = "";
        lineDetail = method + " " + fileName + ":" + fileNum;
    }

    @Override
    public boolean equals(Object o) {
        return ((LineData) o).getMethodId().equals(this.methodId) && ((LineData) o).fileNum.equals(this.fileNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodId, fileNum);
    }

    public String[] toArray() {
        String[] array = new String[4];
        array[0] = method;
        array[1] = fileName;
        array[2] = fileNum;
        array[3] = comment;
        return array;
    }
}
