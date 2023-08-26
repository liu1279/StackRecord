package stack;


import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class FigureDialog extends DialogWrapper {

    private JTextArea jTextArea;
    Project project;
    String stackFigure;

    protected FigureDialog(Project project, String figure) {
        super(true);
        this.project = project;
        this.stackFigure = figure;
        jTextArea = new JTextArea();
        jTextArea.setText(figure);
        jTextArea.setVisible(true);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel();
        panel.add(jTextArea);
        return panel;
    }

    @Override
    protected JComponent createSouthPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        JButton button = new JButton();
        button.setText("保存图片");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VirtualFile virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(),
                        project, project.getProjectFile());
                if (virtualFile != null) {
                    String path = virtualFile.getPath() + "/stackFigure.txt";
                    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path));) {
                        bufferedWriter.write(stackFigure, 0, stackFigure.length());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    FigureDialog.this.dispose();
                }
            }
        });
        panel.add(button);
        return panel;
    }
}
