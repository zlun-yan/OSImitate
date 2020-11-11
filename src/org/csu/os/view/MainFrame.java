package org.csu.os.view;

import org.csu.os.domain.pojo.MyPCB;
import org.csu.os.domain.pojo.MyProgress;
import org.csu.os.domain.signal.CPUSemaphore;
import org.csu.os.domain.signal.PCBSemaphore;
import org.csu.os.domain.table.*;
import org.csu.os.service.*;
import org.csu.os.view.component.ControllerPanel;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

import static org.csu.os.service.DispatchMode.*;

public class MainFrame extends JFrame {
    EntryFrame parentFrame;
    private boolean started = false;
    RecordDialog recordDialog = null;

    private JButton startButton = new JButton("开始");
    private JButton pauseButton = new JButton("暂停");
    private JButton hangButton = new JButton("挂起");
    private JButton unHangButton = new JButton("解挂");
    private JButton recordButton = new JButton("显示日志");
    private JButton backButton = new JButton("返回");

    private ControllerPanel controllerPanel;

    private String[] backUpColumnName = new String[] {"进程名称", "预计运行时间", "优先级"};  //表头
    private String[] readyColumnName = new String[] {"PID", "进程名称", "预计运行时间", "优先级", "队尾指针"};
    private String[] PCBColumnName = new String[] {"PID", "状态", "进程名称", "进程状态",
            "预计运行时间", "优先级"};

    private JPanel threePanel;

    private JTable PCBTable = new JTable();
    private JTable backUpTable = new JTable();

    private JTable readyTable = new JTable();
    private JTable runningTable = new JTable();
    private JTable hangUpTable = new JTable();

    private DefaultTableModel backUpModel = null;
    private DefaultTableModel PCBModel = null;
    private DefaultTableModel readyModel = null;
    private DefaultTableModel CPUModel = null;
    private DefaultTableModel hangUpModel = null;

    private Box vBox = Box.createVerticalBox();

    public MainFrame(EntryFrame parentFrame) {
        this.parentFrame = parentFrame;
//        clear();
        CPUSemaphore.waitSemaphore();

        initButton();
        initController();
        initThreePanel();
        add(vBox, BorderLayout.CENTER);

        pack();
        setTitle("调度算法模拟");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initButton() {
        JPanel panel = new JPanel();

        startButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
        pauseButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.red));

        panel.add(startButton);
        panel.add(pauseButton);
        panel.add(hangButton);
        panel.add(unHangButton);
        panel.add(recordButton);
        panel.add(backButton);

        startButton.addActionListener(event -> {
            if (!started) {  // 这个是为了阻塞 一开始没有点开始的时候 不准就绪队列中的进程进入CPU
                CPUSemaphore.signalSemaphore();
                started = true;
            }
            AutoMoving.start(this);
        });
        pauseButton.addActionListener(event -> AutoMoving.pause());
        hangButton.addActionListener(event -> {
//            threePanel.setVisible(false);
//             使用 setVisible 就可以来实现这个 控件的动态增删了?
            HangUp.doHangUp(this);
        });
        unHangButton.addActionListener(event -> UnHangUp.doUnHangUp(this));
        recordButton.addActionListener(event -> {
            if (recordDialog == null) recordDialog = new RecordDialog();
            recordDialog.setVisible(true);
        });
        backButton.addActionListener(event -> {
            clear();
            parentFrame.setVisible(true);
            setVisible(false);
        });

        this.add(panel, BorderLayout.SOUTH);
    }

    private void initController() {
        JPanel panel = new JPanel();

        //initPCBTable
        JPanel PCBTablePanel = new JPanel();
        JScrollPane PCBScrollPanel = new JScrollPane(PCBTable);
        PCBTablePanel.add(PCBTable.getTableHeader(), BorderLayout.NORTH);
        PCBTablePanel.add(PCBScrollPanel, BorderLayout.CENTER);
        showPCBData();
        PCBScrollPanel.setPreferredSize(new Dimension(647, 300));

        //initBackUpTable
        JPanel tablePanel = new JPanel();
        JScrollPane scrollPanel = new JScrollPane(backUpTable);
        tablePanel.add(backUpTable.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(scrollPanel, BorderLayout.CENTER);
        showBackUpData();
        scrollPanel.setPreferredSize(new Dimension(450, 300));

        //initAddProgressDialog
        controllerPanel = new ControllerPanel(this);

        Box PCBBox = Box.createVerticalBox();
        JLabel PCBLabel = new JLabel("PCB");
        PCBBox.add(PCBLabel);
        PCBBox.add(PCBTablePanel);

        Box backBox = Box.createVerticalBox();
        JLabel backLabel = new JLabel("后备队列");
        backBox.add(backLabel);
        backBox.add(tablePanel);

        panel.add(PCBBox, BorderLayout.WEST);
        panel.add(backBox, BorderLayout.CENTER);
        panel.add(controllerPanel, BorderLayout.EAST);
        vBox.add(panel);
    }

    private void initThreePanel() {
        threePanel = new JPanel();

        JPanel readyTablePanel = new JPanel();
        JScrollPane readyScrollPanel = new JScrollPane(readyTable);
        readyTablePanel.add(readyTable.getTableHeader(), BorderLayout.NORTH);
        readyTablePanel.add(readyScrollPanel, BorderLayout.CENTER);
        showReadyData();
        readyScrollPanel.setPreferredSize(new Dimension(450, 300));

        JPanel PSARunningPanel = new JPanel();
        JScrollPane PSARunningScrollPanel = new JScrollPane(runningTable);
        PSARunningPanel.add(runningTable.getTableHeader(), BorderLayout.NORTH);
        PSARunningPanel.add(PSARunningScrollPanel, BorderLayout.CENTER);
        showRunningData();
        PSARunningScrollPanel.setPreferredSize(new Dimension(450, 300));

        JPanel hangUpPanel = new JPanel();
        JScrollPane hangUpScrollPanel = new JScrollPane(hangUpTable);
        hangUpPanel.add(hangUpTable.getTableHeader(), BorderLayout.NORTH);
        hangUpPanel.add(hangUpScrollPanel, BorderLayout.CENTER);
        showHangUpData();
        hangUpScrollPanel.setPreferredSize(new Dimension(450, 300));

        Box readyBox = Box.createVerticalBox();
        JLabel readyLabel = new JLabel("就绪队列", SwingConstants.CENTER);
        readyBox.add(readyLabel);
        readyBox.add(readyTablePanel);

        Box CPUBox = Box.createVerticalBox();
        JLabel CPULabel = new JLabel("CPU", SwingConstants.CENTER);
        CPUBox.add(CPULabel, BorderLayout.NORTH);
        CPUBox.add(PSARunningPanel, BorderLayout.CENTER);

        Box hangBox = Box.createVerticalBox();
        JLabel hangLabel = new JLabel("挂起队列", SwingConstants.CENTER);
        hangBox.add(hangLabel, BorderLayout.NORTH);
        hangBox.add(hangUpPanel, BorderLayout.CENTER);

        threePanel.add(readyBox, BorderLayout.WEST);
        threePanel.add(CPUBox, BorderLayout.CENTER);
        threePanel.add(hangBox, BorderLayout.EAST);
        vBox.add(threePanel);
    }

    public void addProgress(String name, int time, int priority, int queueOrder) {
        MyProgress myProgress = new MyProgress();
        myProgress.setName(name);
        myProgress.setTime(time);
        myProgress.setPriority(priority);
        myProgress.setQueueOrder(queueOrder);

        if (PCBSemaphore.waitSemaphore()) {
            BackUpQueue.addProgress(myProgress);
            showBackUpData();
        }
        else {
            PCBQueue.addProgress(myProgress);
            showPCBData();
        }
        RecordDialog.refresh();
    }

    public int getHangUpTableSelectedIndex() {
        // 如果没有选中则是返回 -1
        return hangUpTable.getSelectedRow();
    }

    public int getReadyTableSelectedIndex() {
        // 如果没有选中则是返回 -1
        return readyTable.getSelectedRow();
    }

    public void showBackUpData() {
        int count = BackUpQueue.getCount();
        ArrayList<MyProgress> items = BackUpQueue.getQueue();

        Object[][] tableData = new Object[count][3];
        for (int i = 0; i < count; i++) {
            tableData[i][0] = items.get(i).getName();
            tableData[i][1] = items.get(i).getTime();
            tableData[i][2] = items.get(i).getPriority();
        }

        if (backUpModel == null) {
            backUpModel = new DefaultTableModel(tableData, backUpColumnName){
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            backUpTable.setModel(backUpModel);
        }
        else {
            backUpModel.setDataVector(tableData, backUpColumnName);
        }
    }

    public void showPCBData() {
        int count = PCBQueue.getCount();
        ArrayList<MyPCB> items = PCBQueue.getQueue();

        Object[][] tableData = new Object[count][6];
        for (int i = 0; i < count; i++) {
            tableData[i][0] = items.get(i).getPid();
            if (items.get(i).isBusy()) {
                MyProgress thisProgress = items.get(i).getMyProgress();
                tableData[i][1] = "占用";
                tableData[i][2] = thisProgress.getName();
                tableData[i][3] = items.get(i).getState();
                tableData[i][4] = thisProgress.getTime();
                tableData[i][5] = thisProgress.getPriority();


            }
            else {
                tableData[i][1] = "空闲";
            }
        }

        if (PCBModel == null) {
            PCBModel = new DefaultTableModel(tableData, PCBColumnName){
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            PCBTable.setModel(PCBModel);
        }
        else {
            PCBModel.setDataVector(tableData, PCBColumnName);
        }
    }

    public void showReadyData() {
        int count = ReadyQueue.getCount();
        ArrayList<MyPCB> items = ReadyQueue.getQueue();
        Object[][] tableData = new Object[count][5];
        if (mode == Mode.MQ) {
            for (int i = 0; i < count; i++) {
                MyProgress myProgress = items.get(i).getMyProgress();
                tableData[i][0] = items.get(i).getPid();
                tableData[i][1] = myProgress.getName();
                if (items.get(i).getMyProgress().getQueueOrder() == 1) tableData[i][2] = "前台";
                else tableData[i][2] = "后台";
                tableData[i][3] = myProgress.getTime();
                tableData[i][4] = items.get(i).getNext();
            }

            if (readyModel == null) {
                readyModel = new DefaultTableModel(tableData, new String[]{"PID", "进程名称", "类型", "预计运行时间", "队尾指针"}){
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                readyTable.setModel(readyModel);
            }
            else {
                readyModel.setDataVector(tableData, new String[]{"PID", "进程名称", "类型", "预计运行时间", "队尾指针"});
            }
        }
        else if (mode == Mode.MFQ) {
            for (int i = 0; i < count; i++) {
                MyProgress myProgress = items.get(i).getMyProgress();
                tableData[i][0] = items.get(i).getPid();
                tableData[i][1] = myProgress.getName();
                tableData[i][2] = items.get(i).getMyProgress().getQueueOrder();
                tableData[i][3] = myProgress.getTime();
                tableData[i][4] = items.get(i).getNext();
            }

            if (readyModel == null) {
                readyModel = new DefaultTableModel(tableData, new String[]{"PID", "进程名称", "队列优先级", "预计运行时间", "队尾指针"}){
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                readyTable.setModel(readyModel);
            }
            else {
                readyModel.setDataVector(tableData, new String[]{"PID", "进程名称", "队列优先级", "预计运行时间", "队尾指针"});
            }
        }
        else {
            for (int i = 0; i < count; i++) {
                MyProgress myProgress = items.get(i).getMyProgress();
                tableData[i][0] = items.get(i).getPid();
                tableData[i][1] = myProgress.getName();
                tableData[i][2] = myProgress.getTime();
                tableData[i][3] = myProgress.getPriority();
                tableData[i][4] = items.get(i).getNext();
            }

            if (readyModel == null) {
                readyModel = new DefaultTableModel(tableData, readyColumnName){
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                readyTable.setModel(readyModel);
            }
            else {
                readyModel.setDataVector(tableData, readyColumnName);
            }
        }
    }

    public void showHangUpData() {
        int count = HangUpQueue.getCount();
        ArrayList<MyPCB> items = HangUpQueue.getQueue();
        Object[][] tableData;
        if (mode == Mode.PSA) {
            tableData = new Object[count][4];
            for (int i = 0; i < count; i++) {
                MyProgress myProgress = items.get(i).getMyProgress();
                tableData[i][0] = items.get(i).getPid();
                tableData[i][1] = myProgress.getName();
                tableData[i][2] = myProgress.getTime();
                tableData[i][3] = myProgress.getPriority();
            }

            if (hangUpModel == null) {
                hangUpModel = new DefaultTableModel(tableData, new String[]{"PID", "进程名称", "预计运行时间", "优先级"}){
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                hangUpTable.setModel(hangUpModel);
            }
            else {
                hangUpModel.setDataVector(tableData, new String[]{"PID", "进程名称", "预计运行时间", "优先级"});
            }
        }  //  "PID", "进程名称", "预计运行时间", "优先级"
        else {
            tableData = new Object[count][3];
            for (int i = 0; i < count; i++) {
                MyProgress myProgress = items.get(i).getMyProgress();
                tableData[i][0] = items.get(i).getPid();
                tableData[i][1] = myProgress.getName();
                tableData[i][2] = myProgress.getTime();
            }

            if (hangUpModel == null) {
                hangUpModel = new DefaultTableModel(tableData, new String[]{"PID", "进程名称", "预计运行时间"}){
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                hangUpTable.setModel(hangUpModel);
            }
            else {
                hangUpModel.setDataVector(tableData, new String[]{"PID", "进程名称", "预计运行时间"});
            }
        } // "PID", "进程名称", "预计运行时间"
    }

    public void showRunningData() {
        MyPCB myPCB = RunningPCB.getRunningPCB();
        Object[][] tableData;
        if (mode == Mode.PSA) {
            if (RunningPCB.isBusy()) {
                tableData = new Object[1][4];
                MyProgress myProgress = myPCB.getMyProgress();
                tableData[0][0] = myPCB.getPid();
                tableData[0][1] = myProgress.getName();
                tableData[0][2] = myProgress.getTime();
                tableData[0][3] = myProgress.getPriority();
            }
            else {
                tableData = new Object[0][];
            }

            if (CPUModel == null) {
                CPUModel = new DefaultTableModel(tableData, new String[]{"PID", "进程名称", "预计运行时间", "优先级"}){
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                runningTable.setModel(CPUModel);
            }
            else {
                CPUModel.setDataVector(tableData, new String[]{"PID", "进程名称", "预计运行时间", "优先级"});
            }
        }
        else if (mode == Mode.RR) {
            if (RunningPCB.isBusy()) {
                tableData = new Object[1][4];
                MyProgress myProgress = myPCB.getMyProgress();
                tableData[0][0] = myPCB.getPid();
                tableData[0][1] = myProgress.getName();
                tableData[0][2] = myProgress.getTime();
                tableData[0][3] = RunningPCB.getTimeSlice();
            }
            else {
                tableData = new Object[0][];
            }

            if (CPUModel == null) {
                CPUModel = new DefaultTableModel(tableData, new String[]{"PID", "进程名称", "预计运行时间", "时间片剩余时间"}){
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                runningTable.setModel(CPUModel);
            }
            else {
                CPUModel.setDataVector(tableData, new String[]{"PID", "进程名称", "预计运行时间", "时间片剩余时间"});
            }
        }
        else if (mode == Mode.MQ) {
            if (RunningPCB.isBusy()) {
                if (myPCB.getMyProgress().getQueueOrder() == 1) {
                    // 前台队列 时间片轮转调度
                    tableData = new Object[1][5];
                    MyProgress myProgress = myPCB.getMyProgress();
                    tableData[0][0] = myPCB.getPid();
                    tableData[0][1] = myProgress.getName();
                    tableData[0][2] = "前台";
                    tableData[0][3] = myProgress.getTime();
                    tableData[0][4] = RunningPCB.getTimeSlice();

                    if (CPUModel == null) {
                        CPUModel = new DefaultTableModel(tableData, new String[]{"PID", "进程名称", "类型", "预计运行时间", "时间片剩余时间"}){
                            public boolean isCellEditable(int row, int column) {
                                return false;
                            }
                        };
                        runningTable.setModel(CPUModel);
                    }
                    else {
                        CPUModel.setDataVector(tableData, new String[]{"PID", "进程名称", "类型", "预计运行时间", "时间片剩余时间"});
                    }
                }
                else {
                    // 后台队列 先来先服务调度 后期可置换为优先级调度
                    tableData = new Object[1][4];
                    MyProgress myProgress = myPCB.getMyProgress();
                    tableData[0][0] = myPCB.getPid();
                    tableData[0][1] = myProgress.getName();
                    tableData[0][2] = "后台";
                    tableData[0][3] = myProgress.getTime();

                    if (CPUModel == null) {
                        CPUModel = new DefaultTableModel(tableData, new String[]{"PID", "进程名称", "类型", "预计运行时间"}){
                            public boolean isCellEditable(int row, int column) {
                                return false;
                            }
                        };
                        runningTable.setModel(CPUModel);
                    }
                    else {
                        CPUModel.setDataVector(tableData, new String[]{"PID", "进程名称", "类型", "预计运行时间"});
                    }
                }
            }
            else {
                tableData = new Object[0][];

                if (CPUModel == null) {
                    CPUModel = new DefaultTableModel(tableData, new String[]{"PID", "进程名称", "类型", "预计运行时间"}){
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }
                    };
                    runningTable.setModel(CPUModel);
                }
                else {
                    CPUModel.setDataVector(tableData, new String[]{"PID", "进程名称", "类型", "预计运行时间"});
                }
            }
        }
        else if (mode == Mode.MFQ) {
            if (RunningPCB.isBusy()) {
                tableData = new Object[1][5];
                MyProgress myProgress = myPCB.getMyProgress();
                tableData[0][0] = myPCB.getPid();
                tableData[0][1] = myProgress.getName();
                tableData[0][2] = myProgress.getQueueOrder(); // 在多级反馈队列中 这个里面保存的是 其所属的队列的优先级 和这个队列的 时间片长度
                tableData[0][3] = myProgress.getTime();
                tableData[0][4] = RunningPCB.getTimeSlice();
            }
            else {
                tableData = new Object[0][];
            }

            if (CPUModel == null) {
                CPUModel = new DefaultTableModel(tableData, new String[]{"PID", "进程名称", "队列优先级", "预计运行时间", "时间片剩余时间"}){
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                runningTable.setModel(CPUModel);
            }
            else {
                CPUModel.setDataVector(tableData, new String[]{"PID", "进程名称", "队列优先级", "预计运行时间", "时间片剩余时间"});
            }
        }
        else if (mode == Mode.SJF || mode == Mode.FCFS) {
            if (RunningPCB.isBusy()) {
                tableData = new Object[1][3];
                MyProgress myProgress = myPCB.getMyProgress();
                tableData[0][0] = myPCB.getPid();
                tableData[0][1] = myProgress.getName();
                tableData[0][2] = myProgress.getTime();
            }
            else {
                tableData = new Object[0][];
            }

            if (CPUModel == null) {
                CPUModel = new DefaultTableModel(tableData, new String[]{"PID", "进程名称", "预计运行时间"}){
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                runningTable.setModel(CPUModel);
            }
            else {
                CPUModel.setDataVector(tableData, new String[]{"PID", "进程名称", "预计运行时间"});
            }
        }
    }

    public void refresh() {
        showBackUpData();
        showRunningData();
        showReadyData();
        showPCBData();

        RecordDialog.refresh();
        controllerPanel.refresh();
    }

    private void clear() {
        AutoMoving.clear();
        BackUpQueue.clear();
        HangUpQueue.clear();
        ReadyQueue.clear();
        RecordTable.clear();
        RunningPCB.clear();
        CPUSemaphore.clear();
        PCBSemaphore.clear();
        ControllerPanel.clear();
        SettingDialog.clear();

        refresh();
        showHangUpData();
    }
}
