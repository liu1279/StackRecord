package stack;

import com.intellij.debugger.impl.DebuggerContextImpl;
import com.intellij.debugger.impl.DebuggerContextListener;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.xdebugger.impl.frame.XDebuggerFramesList;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class PauseEventListener implements DebuggerContextListener {
    public static XDebuggerFramesList framesList;

    public static boolean isAutoRecord = false;

    @Override
    public void changeEvent(@NotNull DebuggerContextImpl newContext, DebuggerSession.Event event) {
        if (isAutoRecord && event == DebuggerSession.Event.PAUSE) {
            if (framesList == null) {
                JOptionPane.showMessageDialog(null,
                        "you should manually record one time before automatically record");
            } else {
                StackRcorder.storeLineData(framesList);
            }
        }
    }
}
