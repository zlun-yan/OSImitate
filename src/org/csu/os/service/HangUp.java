package org.csu.os.service;

import org.csu.os.domain.pojo.MyPCB;
import org.csu.os.domain.signal.CPUSemaphore;
import org.csu.os.domain.table.HangUpQueue;
import org.csu.os.domain.table.ReadyQueue;
import org.csu.os.domain.table.RunningPCB;
import org.csu.os.view.MainFrame;

public class HangUp {
    public static void doHangUp(MainFrame mainFrame) {
        int index = mainFrame.getReadyTableSelectedIndex();
        if (index == -1) {
            MyPCB myPCB = RunningPCB.getRunningPCB();
            if (myPCB == null) return;
            HangUpQueue.addPCB(myPCB);
            RunningPCB.setRunningPCB(null);
            CPUSemaphore.signalSemaphore();
        }
        else {
            MyPCB myPCB = ReadyQueue.PCBHang(index);
            HangUpQueue.addPCB(myPCB);
        }

        mainFrame.refresh();
        mainFrame.showHangUpData();
    }


}
