package stack;

import com.intellij.debugger.engine.JavaStackFrame;
import com.intellij.debugger.impl.DebuggerContextImpl;
import com.intellij.debugger.impl.DebuggerContextListener;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.xdebugger.impl.frame.XDebuggerFramesList;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class PauseEventListener implements DebuggerContextListener {
    private XDebuggerFramesList framesList;

    @Override
    public void changeEvent(@NotNull DebuggerContextImpl newContext, DebuggerSession.Event event) {
        if (event == DebuggerSession.Event.PAUSE) {
            if (framesList == null) {
                framesList = DataManager.getInstance().getDataContext().getData(DataKey.create("FRAMES_LIST"));
            }
            if (framesList != null) {
                StackRcorder.storeLineData(framesList);
            }
        }

    }
}
