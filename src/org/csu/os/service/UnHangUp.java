package org.csu.os.service;

import org.csu.os.domain.pojo.MyPCB;
import org.csu.os.domain.table.HangUpQueue;
import org.csu.os.domain.table.ReadyQueue;
import org.csu.os.view.MainFrame;

public class UnHangUp {
    public static void doUnHangUp(MainFrame mainFrame) {
        int index = mainFrame.getHangUpTableSelectedIndex();
        MyPCB myPCB;
        if (index == -1) myPCB = HangUpQueue.PCBPop(0);
        else myPCB = HangUpQueue.PCBPop(index);

        ReadyQueue.addPCB(myPCB);

        mainFrame.refresh();
        mainFrame.showHangUpData();
    }
}
