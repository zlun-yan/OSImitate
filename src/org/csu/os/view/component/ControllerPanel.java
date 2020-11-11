package org.csu.os.view.component;

import org.csu.os.domain.table.RunningPCB;
import org.csu.os.view.RecordDialog;
import org.csu.os.view.MainFrame;
import org.csu.os.view.SettingDialog;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

import static org.csu.os.service.DispatchMode.*;

public class ControllerPanel extends JPanel {
    private SettingDialog settingDialog = null;

    private MainFrame parentFrame;
    private static int timeSlice = 4;
    private static boolean queueOrder = true;

    private static int timeMinn = 1;
    private static int timeMaxx = 10;
    private static int priorityMinn = 1;
    private static int priorityMaxx = 50;

    private JTextField nameField;
    private JTextField timeField;
    private JTextField priorityField;

    private JLabel tipLabel;

    private JPanel timeSlicePanel;
    private JPanel radioButtonPanel;

    private Box box = Box.createVerticalBox();
    private static int count = 0;

    public ControllerPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        init();
        initButton();
        refresh();

        add(box, BorderLayout.CENTER);
    }

    private void init() {
        JLabel timeSliceDefaultLabel = new JLabel("时间片长度:");
        timeSliceDefaultLabel.setPreferredSize(new Dimension(80, 30));
        JSpinner timeSliceDefaultSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 2048, 1));
        timeSliceDefaultSpinner.setPreferredSize(new Dimension(120, 30));
        timeSliceDefaultSpinner.addChangeListener(event -> {
            timeSlice = (int) timeSliceDefaultSpinner.getValue();
            RunningPCB.setTimeSliceDefault(timeSlice);
        });


        JLabel nameLabel = new JLabel("进程名称:");
        JLabel timeLabel = new JLabel("预计运行时间:");
        JLabel priorityLabel = new JLabel("优先级:");
        nameLabel.setPreferredSize(new Dimension(80, 30));
        timeLabel.setPreferredSize(new Dimension(80, 30));
        priorityLabel.setPreferredSize(new Dimension(80, 30));

        nameField = new JTextField();
        timeField = new JTextField();
        priorityField = new JTextField();
        nameField.setPreferredSize(new Dimension(120, 30));
        timeField.setPreferredSize(new Dimension(120, 30));
        priorityField.setPreferredSize(new Dimension(120, 30));

        JRadioButton receptionButton = new JRadioButton("前台", true);
        JRadioButton backgroundButton = new JRadioButton("后台");

        receptionButton.addActionListener(event -> {
            if (receptionButton.isSelected()) queueOrder = true;
        });
        backgroundButton.addActionListener(event -> {
            if (backgroundButton.isSelected()) queueOrder = false;
        });

        ButtonGroup radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(receptionButton);
        radioButtonGroup.add(backgroundButton);

        radioButtonPanel = new JPanel();
        radioButtonPanel.add(receptionButton);
        radioButtonPanel.add(backgroundButton);

        timeSlicePanel = new JPanel();
        timeSlicePanel.add(timeSliceDefaultLabel);
        timeSlicePanel.add(timeSliceDefaultSpinner);


        tipLabel = new JLabel();
        tipLabel.setPreferredSize(new Dimension(200, 30));
        tipLabel.setVisible(false);
        JPanel tipPanel = new JPanel();
        tipPanel.add(tipLabel);

        JPanel namePanel = new JPanel();
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        JPanel timePanel = new JPanel();
        timePanel.add(timeLabel);
        timePanel.add(timeField);
        JPanel priorityPanel = new JPanel();
        priorityPanel.add(priorityLabel);
        priorityPanel.add(priorityField);

        box.add(timeSlicePanel);
        box.add(namePanel);
        box.add(timePanel);
        box.add(priorityPanel);
        box.add(radioButtonPanel);
        box.add(tipPanel);
    }

    private void initButton() {
        JButton confirmButton = new JButton("增加进程");
        JButton cancelButton = new JButton("清除");
        JButton randomAddButton = new JButton("随机添加");
        JButton multiRandomButton = new JButton("随机添加十个");
        JButton settingButton = new JButton("设置");

        multiRandomButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));

        confirmButton.addActionListener(event -> {
            tipLabel.setVisible(false);
            String name = nameField.getText();
            String time = timeField.getText();
            String priority = priorityField.getText();

            if (name == null || name.equals("")) {
                tipLabel.setText("请输入进程名称");
                tipLabel.setVisible(true);
                return;
            }
            if (time == null || time.equals("")) {
                tipLabel.setText("请输入进程预计运行时间");
                tipLabel.setVisible(true);
                return;
            }
            if (priority == null || priority.equals("")) {
                tipLabel.setText("请输入进程优先级");
                tipLabel.setVisible(true);
                return;
            }

            int timeToInt, priorityToInt;
            try {
                timeToInt = Integer.parseInt(time);
            } catch (NumberFormatException e) {
                tipLabel.setText("请输入整数预计运行时间");
                tipLabel.setVisible(true);
                return;
            }
            try {
                priorityToInt = Integer.parseInt(priority);
            } catch (NumberFormatException e) {
                tipLabel.setText("请输入整数优先级");
                tipLabel.setVisible(true);
                return;
            }

            if (queueOrder) parentFrame.addProgress(name, timeToInt, priorityToInt, 1);
            else parentFrame.addProgress(name, timeToInt, priorityToInt, 2);
            nameField.setText("");
            timeField.setText("");
            priorityField.setText("");
        });
        cancelButton.addActionListener(event -> {
            tipLabel.setVisible(false);
            nameField.setText("");
            timeField.setText("");
            priorityField.setText("");
        });
        randomAddButton.addActionListener(event -> {
            tipLabel.setVisible(false);
            randomAdd();
        });
        multiRandomButton.addActionListener(event -> {
            tipLabel.setVisible(false);
            for (int i = 0; i < 10; i++) {
                randomAdd();
            }
        });
        settingButton.addActionListener(event -> {
            if (settingDialog == null) settingDialog = new SettingDialog();
            settingDialog.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(settingButton);

        JPanel buttonPanelLine2 = new JPanel();
        buttonPanelLine2.add(randomAddButton);
        buttonPanelLine2.add(multiRandomButton);

//        JPanel buttonPanelLine3 = new JPanel();
//        buttonPanelLine3.add(settingButton);

        box.add(buttonPanel);
        box.add(buttonPanelLine2);
//        box.add(buttonPanelLine3);
    }

    private void randomAdd() {
        String name = "Random_" + ++count;
        int time = new Random().nextInt(timeMaxx - timeMinn) + timeMinn;
        int priority = new Random().nextInt(priorityMaxx - priorityMinn) + priorityMinn;
        RecordDialog.refresh();

        if (mode == Mode.MQ) parentFrame.addProgress(name, time, priority, new Random().nextInt(2) + 1);
        else parentFrame.addProgress(name, time, priority, 1);
    }

    public static int getTimeMinn() {
        return timeMinn;
    }

    public static void setTimeMinn(int timeMinn) {
        ControllerPanel.timeMinn = timeMinn;
    }

    public static int getTimeMaxx() {
        return timeMaxx;
    }

    public static void setTimeMaxx(int timeMaxx) {
        ControllerPanel.timeMaxx = timeMaxx;
    }

    public static int getPriorityMinn() {
        return priorityMinn;
    }

    public static void setPriorityMinn(int priorityMinn) {
        ControllerPanel.priorityMinn = priorityMinn;
    }

    public static int getPriorityMaxx() {
        return priorityMaxx;
    }

    public static void setPriorityMaxx(int priorityMaxx) {
        ControllerPanel.priorityMaxx = priorityMaxx;
    }

    public static int getTimeSlice() {
        return timeSlice;
    }

    public static void clear() {
        count = 0;
        timeMinn = 1;
        timeMaxx = 10;
        priorityMinn = 1;
        priorityMaxx = 50;
    }

    public void refresh() {
        if (mode != Mode.MQ && mode != Mode.RR && mode != Mode.MFQ) timeSlicePanel.setVisible(false);
        else timeSlicePanel.setVisible(true);

        if (mode != Mode.MQ) radioButtonPanel.setVisible(false);
        else radioButtonPanel.setVisible(true);
    }
}
