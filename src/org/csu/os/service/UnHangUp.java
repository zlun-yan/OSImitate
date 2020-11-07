package org.csu.os.service;

import org.csu.os.domain.pojo.MyPCB;
import org.csu.os.domain.table.HangUpQueue;
import org.csu.os.domain.table.ReadyQueue;
import org.csu.os.view.MainFrame;

public class UnHangUp {
    public static void doUnHangUp(MainFrame mainFrame) {
        MyPCB myPCB = HangUpQueue.PCBPop(mainFrame.getHangUpTableSelectedIndex());
        ReadyQueue.addPCB(myPCB);

        mainFrame.refresh();
        mainFrame.showHangUpDate();
    }
}
