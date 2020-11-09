package org.csu.os.domain.signal;

import org.csu.os.domain.pojo.MyPCB;
import org.csu.os.domain.table.ReadyQueue;

public class CPUSemaphore {
    private static int mutex = 1;

    public static boolean waitSemaphore() {
        mutex--;
        return mutex < 0;
    }

    public static void signalSemaphore() {
        mutex++;
        if (mutex <= 0) {
            ReadyQueue.wakeUp();
        }
    }

    public static void clear() {
        mutex = 1;
    }
}
