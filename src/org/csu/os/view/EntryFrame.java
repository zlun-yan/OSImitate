package org.csu.os.view;

import org.csu.os.domain.signal.PCBSemaphore;
import org.csu.os.domain.table.PCBQueue;
import org.csu.os.domain.table.RunningPCB;
import org.csu.os.service.DispatchMode;
import org.csu.os.view.component.ControllerPanel;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import javax.swing.*;
import java.awt.*;

import static org.csu.os.service.DispatchMode.mode;

public class EntryFrame extends JFrame {
    private int PCBCount = 6;
    MainFrame mainFrame = null;

    private JRadioButton FCFSButton = new JRadioButton("先到先服务调度", true);
    private JRadioButton SJFButton = new JRadioButton("最短作业调度");
    private JRadioButton PSAButton = new JRadioButton("优先级调度");
    private JRadioButton RRButton = new JRadioButton("轮转法调度");
    private JRadioButton MQButton = new JRadioButton("多级队列调度");
    private JRadioButton MFQButton = new JRadioButton("多级反馈队列调度");

    public EntryFrame() {
        initPanel();
        initButton();
        initRadioButton();

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
        PCBCountSpinner.addChangeListener(event -> PCBCount = (int) PCBCountSpinner.getValue());

        JPanel PCBCountPanel = new JPanel();
        PCBCountPanel.add(PCBCountLabel);
        PCBCountPanel.add(PCBCountSpinner);

        add(PCBCountPanel, BorderLayout.CENTER);
    }

    private void initRadioButton() {
        JPanel panel = new JPanel();
        ButtonGroup radioButtonGroup = new ButtonGroup();

        radioButtonGroup.add(FCFSButton);
        radioButtonGroup.add(SJFButton);
        radioButtonGroup.add(PSAButton);
        radioButtonGroup.add(RRButton);
        radioButtonGroup.add(MQButton);
        radioButtonGroup.add(MFQButton);

        FCFSButton.addActionListener(event -> {
            if (FCFSButton.isSelected()) {
                mode = DispatchMode.Mode.FCFS;
            }
        });  // 先到先服务
        SJFButton.addActionListener(event -> {
            if (SJFButton.isSelected()) {
                mode = DispatchMode.Mode.SJF;
            }
        });  // 最短时间调度
        PSAButton.addActionListener(event -> {
            if (PSAButton.isSelected()) {
                mode =  DispatchMode.Mode.PSA;
            }
        });  //  优先级调度
        RRButton.addActionListener(event -> {
            if (RRButton.isSelected()) {
                mode = DispatchMode.Mode.RR;
                RunningPCB.setTimeSliceDefault(ControllerPanel.getTimeSlice());
            }
        });  //  时间片轮转
        MQButton.addActionListener(event -> {
            if (MQButton.isSelected()) {
                mode = DispatchMode.Mode.MQ;
                RunningPCB.setTimeSliceDefault(ControllerPanel.getTimeSlice());
            }
        });  // 多级队列调度
        MFQButton.addActionListener(event -> {
            if (MFQButton.isSelected()) {
                mode = DispatchMode.Mode.MFQ;
            }
        });  // 多级反馈队列调度


        panel.add(FCFSButton);
        panel.add(SJFButton);
        panel.add(PSAButton);
        panel.add(RRButton);
        panel.add(MQButton);
        panel.add(MFQButton);
        add(panel, BorderLayout.NORTH);
    }

    private void initButton() {
        JButton confirmButton = new JButton("开始");
        confirmButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.red));

        confirmButton.addActionListener(event -> {
            PCBQueue.setCount(PCBCount);
            PCBSemaphore.setCount(PCBCount);
            if (mainFrame == null) mainFrame = new MainFrame(this);
            else mainFrame.refresh();

            switch (mode) {
                case FCFS:
                    mainFrame.setTitle("先到先服务调度");
                    break;
                case SJF:
                    mainFrame.setTitle("最短作业调度");
                    break;
                case PSA:
                    mainFrame.setTitle("优先级调度");
                    break;
                case RR:
                    mainFrame.setTitle("轮转法调度");
                    break;
                case MQ:
                    mainFrame.setTitle("多级队列调度");
                    break;
                case MFQ:
                    mainFrame.setTitle("多级反馈队列调度");
                    break;
                default:
                    break;
            }

            mainFrame.setVisible(true);
            setVisible(false);
        });


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(confirmButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
