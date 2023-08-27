package stack;

import lombok.Data;

@Data
public class OverLoadInfo {
    String methodId;
    int level;
    public OverLoadInfo(String methodId, int level) {
        this.methodId = methodId;
        this.level = level;
    }
}
