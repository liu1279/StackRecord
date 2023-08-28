package stack;

import com.intellij.debugger.engine.JavaStackFrame;
import com.intellij.debugger.impl.DebuggerContextImpl;
import com.intellij.debugger.impl.DebuggerContextListener;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.xdebugger.impl.frame.XDebuggerFramesList;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;


public class PauseEventListener implements DebuggerContextListener {
    public static XDebuggerFramesList framesList;

    public static boolean isAutoRecord = false;

    @Override
    public void changeEvent(@NotNull DebuggerContextImpl newContext, DebuggerSession.Event event) {
        if (isAutoRecord && event == DebuggerSession.Event.PAUSE) {
            if (framesList == null) {
                JOptionPane.showMessageDialog(null, "自动记录前，需要手动记录一次获取环境");
            } else {
                StackRcorder.storeLineData(framesList);
            }
        }
    }
}
