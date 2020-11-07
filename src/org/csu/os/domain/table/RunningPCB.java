package org.csu.os.domain.table;

import org.csu.os.domain.pojo.MyPCB;
import org.csu.os.domain.signal.CPUSemaphore;
import org.csu.os.domain.signal.PCBSemaphore;

public class RunningPCB {
    private static MyPCB runningPCB;
    private static boolean busy = false;

    private static int timeSliceDefault = 8;
    private static int timeSlice;

    public static int getTimeSliceDefault() {
        return timeSliceDefault;
    }

    public static int getTimeSlice() {
        return timeSlice;
    }

    public static void setTimeSliceDefault(int timeSliceDefault) {
        RunningPCB.timeSliceDefault = timeSliceDefault;
    }

    public static void updateTimeSlice() {
        timeSlice--;
    }

    public static void setRunningPCB(MyPCB runningPCB) {
        RunningPCB.runningPCB = runningPCB;
        timeSlice = timeSliceDefault;
        if (runningPCB == null) {
            busy = false;
            return;
        }

        runningPCB.setState("running");
        busy = true;
    }

    public static void finishRunning() {
        if (runningPCB == null) return;
        PCBQueue.PCBRelease(runningPCB.getPid());
        runningPCB = null;
        busy = false;
        CPUSemaphore.signalSemaphore();
        PCBSemaphore.signalSemaphore();
    }

    public static void cutRunning() {
        if (runningPCB == null) return;
        if (runningPCB.getMyProgress().getTime() == 0) {
            finishRunning();
            return;
        }

        runningPCB.setState("waiting");
        ReadyQueue.addPCB(runningPCB);
        runningPCB = null;
        busy = false;

        CPUSemaphore.signalSemaphore();
    }

    public static MyPCB getRunningPCB() {
        return runningPCB;
    }

    public static boolean isBusy() {
        return busy;
    }
}
