package org.csu.os.view;

import org.csu.os.domain.pojo.MyPCB;
import org.csu.os.domain.pojo.MyProgress;
import org.csu.os.domain.signal.CPUSemaphore;
import org.csu.os.domain.signal.PCBSemaphore;
import org.csu.os.domain.table.*;
import org.csu.os.service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

import static org.csu.os.service.DispatchMode.*;

public class MainFrame extends JFrame {
    private boolean started = false;

    private JRadioButton FCFSButton = new JRadioButton("先到先服务调度");
    private JRadioButton SJFButton = new JRadioButton("最短作业调度");
    private JRadioButton PSAButton = new JRadioButton("优先级调度", true);
    private JRadioButton RRButton = new JRadioButton("轮转法调度");

    private JButton startButton = new JButton("开始");
    private JButton pauseButton = new JButton("暂停");
    private JButton hangButton = new JButton("挂起");
    private JButton unHangButton = new JButton("解挂");
    private JButton recordButton = new JButton("显示日志");

    private ControllerPanel controllerPanel;

    private String[] backUpColumnName = new String[] {"进程名称", "预计运行时间", "优先级"};  //表头
    private String[] readyColumnName = new String[] {"PID", "进程名称", "预计运行时间", "优先级", "队尾指针"};
    private String[] PCBColumnName = new String[] {"PID", "状态", "进程名称", "进程状态",
            "预计运行时间", "优先级"};
    private String[] doProgressColumnName = new String[]{"PID", "进程名称", "预计运行时间", "优先级"};

    private JPanel threePanel;

    private JTable PCBTable = new JTable();
    private JTable backUpTable = new JTable();

    private JTable readyTable = new JTable();
    private JTable runningTable = new JTable();
    private JTable hangUpTable = new JTable();

    private Box vBox = Box.createVerticalBox();

    public MainFrame(int count) {
        CPUSemaphore.waitSemaphore();

        PCBQueue.setCount(count);
        initRadioButton();
        initButton();
        initController();
        initThreePanel();
        add(vBox, BorderLayout.CENTER);

        pack();
        setTitle("调度算法模拟");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initRadioButton() {
        JPanel panel = new JPanel();
        ButtonGroup radioButtonGroup = new ButtonGroup();

        radioButtonGroup.add(FCFSButton);
        radioButtonGroup.add(SJFButton);
        radioButtonGroup.add(PSAButton);
        radioButtonGroup.add(RRButton);

        FCFSButton.addActionListener(event -> {
            if (FCFSButton.isSelected()) {
                AutoMoving.pause();
                mode = Mode.FCFS;
                AutoMoving.start(this);
                showRunningData();
                controllerPanel.setTimeSlicePanelVisible(false);
            }
        });
        SJFButton.addActionListener(event -> {
            if (SJFButton.isSelected()) {
                AutoMoving.pause();
                mode = Mode.SJF;
                AutoMoving.start(this);
                showRunningData();
                controllerPanel.setTimeSlicePanelVisible(false);
            }
        });
        PSAButton.addActionListener(event -> {
            if (PSAButton.isSelected()) {
                AutoMoving.pause();
                mode =  Mode.PSA;
                AutoMoving.start(this);
                showRunningData();
                controllerPanel.setTimeSlicePanelVisible(false);
            }
        });
        RRButton.addActionListener(event -> {
            if (RRButton.isSelected()) {
                AutoMoving.pause();
                mode = Mode.RR;
                AutoMoving.start(this);
                showRRRuningDate();
                controllerPanel.setTimeSlicePanelVisible(true);
            }
        });

        panel.add(FCFSButton);
        panel.add(SJFButton);
        panel.add(PSAButton);
        panel.add(RRButton);
        this.add(panel, BorderLayout.NORTH);
    }

    private void initButton() {
        JPanel panel = new JPanel();

        panel.add(startButton);
        panel.add(pauseButton);
        panel.add(hangButton);
        panel.add(unHangButton);
        panel.add(recordButton);

        startButton.addActionListener(event -> {
            if (!started) CPUSemaphore.signalSemaphore();
            AutoMoving.setState(true);
            AutoMoving.start(this);
        });
        pauseButton.addActionListener(event -> {
            AutoMoving.setState(false);
            AutoMoving.pause();
        });

        hangButton.addActionListener(event -> {
//            threePanel.setVisible(false);
//             使用 setVisible 就可以来实现这个 控件的动态增删了?
            HangUp.doHangUp(this);
        });
        unHangButton.addActionListener(event -> {
            UnHangUp.doUnHangUp(this);
        });
        recordButton.addActionListener(event -> {
            RecordDialog recordDialog = new RecordDialog();
            recordDialog.setVisible(true);
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
        showPCBDate();
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

        panel.add(PCBTablePanel, BorderLayout.WEST);
        panel.add(tablePanel, BorderLayout.CENTER);
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
        showHangUpDate();
        hangUpScrollPanel.setPreferredSize(new Dimension(450, 300));

        threePanel.add(readyTablePanel, BorderLayout.WEST);
        threePanel.add(PSARunningPanel, BorderLayout.CENTER);
        threePanel.add(hangUpPanel, BorderLayout.EAST);
        vBox.add(threePanel);
    }

    public void addProgress(String name, int time, int priority) {
        MyProgress myProgress = new MyProgress();
        myProgress.setName(name);
        myProgress.setTime(time);
        myProgress.setPriority(priority);

        RecordTable.addProgress(myProgress);
        if (PCBSemaphore.waitSemaphore(myProgress)) {
            BackUpQueue.addProgress(myProgress);
            showBackUpData();
        }
        else {
            PCBQueue.addProgress(myProgress);
            showPCBDate();
        }
        RecordDialog.refresh();
    }

    public int getHangUpTableSelectedIndex() {
        return hangUpTable.getSelectedRow();
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

        backUpTable.setModel(new DefaultTableModel(tableData, backUpColumnName){
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }

    public void showPCBDate() {
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
                setOneRowBackgroundColor(PCBTable, i, Color.green);
            }
        }

        PCBTable.setModel(new DefaultTableModel(tableData, PCBColumnName){
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }

    public void showReadyData() {
        int count = ReadyQueue.getCount();
        ArrayList<MyPCB> items = ReadyQueue.getQueue();
        Object[][] tableData = new Object[count][5];
        for (int i = 0; i < count; i++) {
            MyProgress myProgress = items.get(i).getMyProgress();
            tableData[i][0] = items.get(i).getPid();
            tableData[i][1] = myProgress.getName();
            tableData[i][2] = myProgress.getTime();
            tableData[i][3] = myProgress.getPriority();
            tableData[i][4] = items.get(i).getNext();
        }

        readyTable.setModel(new DefaultTableModel(tableData, readyColumnName){
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }

    public void showHangUpDate() {
        int count = HangUpQueue.getCount();
        ArrayList<MyPCB> items = HangUpQueue.getQueue();
        Object[][] tableData = new Object[count][4];
        for (int i = 0; i < count; i++) {
            MyProgress myProgress = items.get(i).getMyProgress();
            tableData[i][0] = items.get(i).getPid();
            tableData[i][1] = myProgress.getName();
            tableData[i][2] = myProgress.getTime();
            tableData[i][3] = myProgress.getPriority();
        }

        hangUpTable.setModel(new DefaultTableModel(tableData, doProgressColumnName){
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }

    public void showRunningData() {
        MyPCB myPCB = RunningPCB.getRunningPCB();
        Object[][] tableData;
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

        runningTable.setModel(new DefaultTableModel(tableData, doProgressColumnName){
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }

    public void showRRRuningDate() {
        MyPCB myPCB = RunningPCB.getRunningPCB();
        Object[][] tableData;
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

        runningTable.setModel(new DefaultTableModel(tableData, new String[]{"PID", "进程名称", "预计运行时间", "时间片剩余时间"}){
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }

    public void refresh() {
        showBackUpData();
        if (mode == Mode.RR) showRRRuningDate();
        else showRunningData();
        showReadyData();
        showPCBDate();

        RecordDialog.refresh();
    }


    /**
     * 设置表格的某一行的背景色
     * @param table
     */
    public void setOneRowBackgroundColor(JTable table, int rowIndex,
                                                Color color) {
        try {
            DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {

                public Component getTableCellRendererComponent(JTable table,
                                                               Object value, boolean isSelected, boolean hasFocus,
                                                               int row, int column) {
                    if (row == rowIndex) {
                        System.out.println("Hello");
                        setBackground(color);
                        setForeground(Color.WHITE);
                    }else if(row > rowIndex){
                        setBackground(Color.BLACK);
                        setForeground(Color.WHITE);
                    }else{
                        setBackground(Color.BLACK);
                        setForeground(Color.WHITE);
                    }

                    return super.getTableCellRendererComponent(table, value,
                            isSelected, hasFocus, row, column);
                }
            };
            int columnCount = table.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                table.getColumn(table.getColumnName(i)).setCellRenderer(tcr);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
