package stack;

import com.intellij.debugger.impl.DebuggerManagerListener;
import com.intellij.debugger.impl.DebuggerSession;

public class DebuggerManageListener implements DebuggerManagerListener {

    @Override
    public void sessionCreated(DebuggerSession session) {
        session.getContextManager().addListener(new PauseEventListener());
    }
}
