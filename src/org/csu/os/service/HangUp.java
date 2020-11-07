package org.csu.os.service;

import org.csu.os.domain.pojo.MyPCB;
import org.csu.os.domain.signal.CPUSemaphore;
import org.csu.os.domain.table.HangUpQueue;
import org.csu.os.domain.table.RunningPCB;
import org.csu.os.view.MainFrame;

public class HangUp {
    public static void doHangUp(MainFrame mainFrame) {
        MyPCB myPCB = RunningPCB.getRunningPCB();
        if (myPCB == null) return;
        HangUpQueue.addPCB(myPCB);
        RunningPCB.setRunningPCB(null);
        CPUSemaphore.signalSemaphore();

        mainFrame.refresh();
        mainFrame.showHangUpDate();
    }


}
