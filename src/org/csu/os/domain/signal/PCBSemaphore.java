package org.csu.os.domain.signal;

import org.csu.os.domain.pojo.MyProgress;
import org.csu.os.domain.table.BackUpQueue;
import org.csu.os.domain.table.PCBQueue;

public class PCBSemaphore {
    private static int count = PCBQueue.getCount();

    public static boolean waitSemaphore(MyProgress myProgress) {
        count--;
        if (count < 0) {
            return true;
        }

        return false;
    }

    public static void signalSemaphore() {
        count++;
        if (count <= 0) {
            BackUpQueue.wakeUp();
        }
    }
}
