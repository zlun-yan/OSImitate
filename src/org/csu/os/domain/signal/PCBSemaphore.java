package org.csu.os.domain.signal;

import org.csu.os.domain.table.BackUpQueue;
import org.csu.os.domain.table.PCBQueue;

public class PCBSemaphore {
    private static int count = 6;

    public static boolean waitSemaphore() {
        count--;
        return count < 0;
    }

    public static void signalSemaphore() {
        count++;
        if (count <= 0) {
            BackUpQueue.wakeUp();
        }
    }

    public static void clear() {
        count = PCBQueue.getCount();
    }

    public static void setCount(int count) {
        PCBSemaphore.count = count;
    }
}
