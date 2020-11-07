package org.csu.os.view;

import javax.swing.*;
import java.awt.*;

public class EntryFrame extends JFrame {
    private int PCBCount = 6;

    public EntryFrame() {
        initPanel();
        initButton();

        pack();
        setTitle("调度算法模拟");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initPanel() {
        JLabel PCBCountLabel = new JLabel("设定道数:");
        PCBCountLabel.setPreferredSize(new Dimension(80, 30));
        JSpinner PCBCountSpinner = new JSpinner(new SpinnerNumberModel(6, 1, 2048, 1));
        PCBCountSpinner.setPreferredSize(new Dimension(120, 30));
        PCBCountSpinner.addChangeListener(event -> {
            PCBCount = (int) PCBCountSpinner.getValue();
        });

        JPanel PCBCountPanel = new JPanel();
        PCBCountPanel.add(PCBCountLabel);
        PCBCountPanel.add(PCBCountSpinner);

        add(PCBCountPanel, BorderLayout.CENTER);
    }

    private void initButton() {
        JButton confirmButton = new JButton("开始");
        JButton cancelButton = new JButton("取消");

        confirmButton.addActionListener(event -> {
            MainFrame mainFrame = new MainFrame(PCBCount);
            mainFrame.setVisible(true);
            dispose();
        });

        cancelButton.addActionListener(event -> {
            dispose();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
