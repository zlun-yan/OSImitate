package org.csu.os.domain.signal;

import org.csu.os.domain.pojo.MyPCB;
import org.csu.os.domain.table.ReadyQueue;

public class CPUSemaphore {
    private static int mutex = 1;

    public static boolean waitSemaphore() {
        mutex--;
        if (mutex < 0) {
            return true;
        }

        return false;
    }

    public static void signalSemaphore() {
        mutex++;
        if (mutex <= 0) {
            ReadyQueue.wakeUp();
        }
    }
}
