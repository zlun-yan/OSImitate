package org.csu.os.view;

import org.csu.os.view.component.ControllerPanel;

import javax.swing.*;
import java.awt.*;

public class SettingDialog extends JFrame {
    private JSpinner randomTimeMinSpinner;
    private JSpinner randomTimeMaxSpinner;
    private JSpinner randomPriorityMinSpinner;
    private JSpinner randomPriorityMaxSpinner;

    private static int randomTimeMin;
    private static int randomTimeMax;
    private static int randomPriorityMin;
    private static int randomPriorityMax;

    public SettingDialog() {
        init();
        initButton();

        pack();
        setTitle("设置");
        setLocationRelativeTo(null);
    }

    private void init() {
        randomTimeMin = ControllerPanel.getTimeMinn();
        randomTimeMax = ControllerPanel.getTimeMaxx();
        randomPriorityMin = ControllerPanel.getPriorityMinn();
        randomPriorityMax = ControllerPanel.getPriorityMaxx();

        randomTimeMinSpinner = new JSpinner(new SpinnerNumberModel(randomTimeMin, 1, 2048, 1));
        randomTimeMaxSpinner = new JSpinner(new SpinnerNumberModel(randomTimeMax, 1, 2048, 1));
        randomPriorityMinSpinner = new JSpinner(new SpinnerNumberModel(randomPriorityMin, 1, 2048, 1));
        randomPriorityMaxSpinner = new JSpinner(new SpinnerNumberModel(randomPriorityMax, 1, 2048, 1));

        randomTimeMinSpinner.addChangeListener(event -> {
            int value = (int) randomTimeMinSpinner.getValue();
            if (value > randomTimeMax) randomTimeMinSpinner.setValue(value - 1);
            else randomTimeMin = value;
        });
        randomTimeMaxSpinner.addChangeListener(event -> {
            int value = (int) randomTimeMaxSpinner.getValue();
            if (value < randomTimeMin) randomTimeMaxSpinner.setValue(value + 1);
            else randomTimeMax = value;
        });
        randomPriorityMinSpinner.addChangeListener(event -> {
            int value = (int) randomPriorityMinSpinner.getValue();
            if (value > randomPriorityMax) randomPriorityMinSpinner.setValue(value - 1);
            else randomPriorityMin = value;
        });
        randomPriorityMaxSpinner.addChangeListener(event -> {
            int value = (int) randomPriorityMaxSpinner.getValue();
            if (value < randomPriorityMin) randomPriorityMaxSpinner.setValue(value + 1);
            else randomPriorityMax = value;
        });

        JLabel randomTimeMinLabel = new JLabel("随机时间最小值: ");
        randomTimeMinLabel.setPreferredSize(new Dimension(120, 30));
        randomTimeMinSpinner.setPreferredSize(new Dimension(80, 30));
        JPanel randomTimeMinPanel = new JPanel();
        randomTimeMinPanel.add(randomTimeMinLabel);
        randomTimeMinPanel.add(randomTimeMinSpinner);

        JLabel randomTimeMaxLabel = new JLabel("随机时间最大值: ");
        randomTimeMaxLabel.setPreferredSize(new Dimension(120, 30));
        randomTimeMaxSpinner.setPreferredSize(new Dimension(80, 30));
        JPanel randomTimeMaxPanel = new JPanel();
        randomTimeMaxPanel.add(randomTimeMaxLabel);
        randomTimeMaxPanel.add(randomTimeMaxSpinner);

        JLabel randomPriorityMinLabel = new JLabel("随机优先级最小值: ");
        randomPriorityMinLabel.setPreferredSize(new Dimension(120, 30));
        randomPriorityMinSpinner.setPreferredSize(new Dimension(80, 30));
        JPanel randomPriorityMinPanel = new JPanel();
        randomPriorityMinPanel.add(randomPriorityMinLabel);
        randomPriorityMinPanel.add(randomPriorityMinSpinner);

        JLabel randomPriorityMaxLabel = new JLabel("随机优先级最大值: ");
        randomPriorityMaxLabel.setPreferredSize(new Dimension(120, 30));
        randomPriorityMaxSpinner.setPreferredSize(new Dimension(80, 30));
        JPanel randomPriorityMaxPanel = new JPanel();
        randomPriorityMaxPanel.add(randomPriorityMaxLabel);
        randomPriorityMaxPanel.add(randomPriorityMaxSpinner);

        Box box = Box.createVerticalBox();
        box.add(randomTimeMinPanel);
        box.add(randomTimeMaxPanel);
        box.add(randomPriorityMinPanel);
        box.add(randomPriorityMaxPanel);

        add(box, BorderLayout.CENTER);
    }

    private void initButton() {
        JButton confirmButton = new JButton("确认");
        JButton cancelButton = new JButton("取消");

        confirmButton.addActionListener(event -> {
            ControllerPanel.setTimeMinn(randomTimeMin);
            ControllerPanel.setTimeMaxx(randomTimeMax);
            ControllerPanel.setPriorityMinn(randomPriorityMin);
            ControllerPanel.setPriorityMaxx(randomPriorityMax);
            dispose();
        });
        cancelButton.addActionListener(event -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void clear() {
        randomTimeMin = 1;
        randomTimeMax = 10;
        randomPriorityMin = 1;
        randomPriorityMax = 50;
    }
}
