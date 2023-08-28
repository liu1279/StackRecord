package stack;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import org.apache.commons.lang3.StringUtils;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;


public class RecordWindow {
    private JTable stackTable;
    private JButton resetButton;
    private JTextField startMethods;
    private JTextField endMethods;
    private JComboBox printStyle;
    private JPanel contentPanel;
    private JButton showButton;
    private JComboBox isComment;
    private JComboBox recordPattern;

    private Project project;

    public static final String[] COLUMES = {"方法名", "文件名", "行号", "注释"};
    public static final DefaultTableModel DEFAULT_TABLE_MODEL = new DefaultTableModel(null, COLUMES);

    public static final ArrayList<LineData> BREAK_POINT_LINES = new ArrayList<>();

    public static boolean addUniquePoint(LineData thisBreakPoint) {
        for (LineData breakPointLine : BREAK_POINT_LINES) {
            if (breakPointLine.isSameMethodAs(thisBreakPoint)) {
                return false;
            }
        }
        if (StackRcorder.needComment) {
            String comment = JOptionPane.showInputDialog("添加注释");
            if (comment != null) {
                thisBreakPoint.setComment(comment);
            }
        }
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
        recordPattern.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (e.getItem().equals("自动记录")) {
                        PauseEventListener.isAutoRecord = true;
                    } else if (e.getItem().equals("手动记录")) {
                        PauseEventListener.isAutoRecord = false;
                    }
                }
            }
        });
        /*
            添加说明
            不添加说明
         */
        isComment.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (e.getItem().equals("添加说明")) {
                        StackRcorder.needComment = true;
                    } else if (e.getItem().equals("不添加说明")) {
                        StackRcorder.needComment = false;
                    }
                }
            }
        });
    }

    public JComponent getContentPanel() {
        return contentPanel;
    }


}
