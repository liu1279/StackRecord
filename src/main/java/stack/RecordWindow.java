package stack;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import org.apache.commons.lang3.StringUtils;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public class RecordWindow {
    private JTable stackTable;
    private JButton resetButton;
    private JTextField startMethods;
    private JTextField endMethods;
    private JComboBox printStyle;
    private JPanel contentPanel;
    private JButton showButton;

    private Project project;

    public static final String[] COLUMES = {"方法名", "文件名", "行号", "注释"};
    public static final DefaultTableModel DEFAULT_TABLE_MODEL = new DefaultTableModel(null, COLUMES);

    public static final ArrayList<LineData> BREAK_POINT_LINES = new ArrayList<>();

    public static boolean addUniquePoint(LineData thisBreakPoint) {
        for (LineData breakPointLine : BREAK_POINT_LINES) {
            if (breakPointLine.equals(thisBreakPoint)) {
                return false;
            }
        }

        String comment = JOptionPane.showInputDialog("添加注释");
        thisBreakPoint.setComment(comment);
        BREAK_POINT_LINES.add(thisBreakPoint);
        DEFAULT_TABLE_MODEL.addRow(thisBreakPoint.toArray());
        return true;
    }


    private void init() {
        stackTable.setModel(DEFAULT_TABLE_MODEL);
    }

    public RecordWindow(Project project, ToolWindow toolWindow) {
        init();
        this.project = project;
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BREAK_POINT_LINES.clear();
                StackRcorder.clear();
                DEFAULT_TABLE_MODEL.setDataVector(null, COLUMES);
            }
        });
        showButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String stackFigure = StackRcorder.GetStackFigure(startMethods.getText(), endMethods.getText(), printStyle.getSelectedIndex());
                if (StringUtils.isBlank(stackFigure)) {
                    Messages.showMessageDialog("没有栈信息", "警告", Messages.getInformationIcon());
                    return;
                }
                FigureDialog stackFigureDialog = new FigureDialog(project, stackFigure);
                stackFigureDialog.show();
            }
        });
    }

    public JComponent getContentPanel() {
        return contentPanel;
    }


}
