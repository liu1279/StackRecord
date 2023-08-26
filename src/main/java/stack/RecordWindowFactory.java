package stack;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class RecordWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        RecordWindow toolWindowContent = new RecordWindow(project, toolWindow);
        Content content = ContentFactory.getInstance().createContent(toolWindowContent.getContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);

    }
}
