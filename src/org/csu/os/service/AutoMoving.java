package org.csu.os.service;

import org.csu.os.domain.pojo.MyPCB;
import org.csu.os.domain.pojo.MyProgress;
import org.csu.os.domain.table.ReadyQueue;
import org.csu.os.domain.table.RunningPCB;
import org.csu.os.view.MainFrame;

import java.util.*;
import static org.csu.os.service.DispatchMode.*;

public class AutoMoving {
    private static boolean state = false;

    private static int step = 1;
    private static int systemTime = 0;
    private static MainFrame targetFrame;

    private static Timer SystemTimer;
    private static Timer PSATimer;
    private static Timer RRTimer;
    private static Timer FCFSTimer;
    private static Timer SJFTimer;

    public static void start(MainFrame targetFrame) {
        if (!state) return;
        AutoMoving.targetFrame = targetFrame;
        if (SystemTimer == null) {
            SystemTimer = new Timer(true);
            SystemTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    doMoving();
                }
            }, 0, 1000); // 每1000毫秒进行一次
        }

        switch (mode) {
            case PSA:
                PSATimer = new Timer(true);
                PSATimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        doPSAMoving();
                    }
                }, 0, 1000); // 每1000毫秒进行一次
                break;
            case RR:
                RRTimer = new Timer(true);
                RRTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        doRRMoving();
                    }
                }, 0, 1000);
                break;
            case SJF:
                SJFTimer = new Timer(true);
                SJFTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        doSJFMoving();
                    }
                }, 0, 1000);
                break;
            case FCFS:
                FCFSTimer = new Timer(true);
                FCFSTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        doFCFSMoving();
                    }
                }, 0, 1000);
                break;
            default:
                break;
        }
    }

    public static int getSystemTime() {
        return systemTime;
    }

    public static void setState(boolean state) {
        AutoMoving.state = state;
    }

    public static void pause() {
        state = false;
        switch (mode) {
            case RR:
                if (RRTimer == null) return;
                RRTimer.cancel();
                break;
            case PSA:
                if (PSATimer == null) return;
                PSATimer.cancel();
                break;
            case SJF:
                if (SJFTimer == null) return;
                SJFTimer.cancel();
                break;
            case FCFS:
                if (FCFSTimer == null) return;
                FCFSTimer.cancel();
                break;
            default:
                break;
        }
        HangUp.doHangUp(targetFrame);
    }

    private static void doMoving() {
        systemTime++;
        ReadyQueue.updateWaitTime(step);
    }

    private static void doFCFSMoving() {
        MyPCB myPCB = RunningPCB.getRunningPCB();
        if (myPCB == null) return;
        MyProgress myProgress = myPCB.getMyProgress();
        myProgress.setTime(myProgress.getTime() - step);
        if (myProgress.getTime() <= 0) RunningPCB.finishRunning();

        targetFrame.refresh();
    }

    private static void doPSAMoving() {
        MyPCB myPCB = RunningPCB.getRunningPCB();
        if (myPCB == null) return;
        MyProgress myProgress = myPCB.getMyProgress();
        myProgress.setTime(myProgress.getTime() - step);
        myProgress.setPriority(myProgress.getPriority() - step);
        RunningPCB.cutRunning();

//        System.out.println("##############################");
//        System.out.println("CPUSemaphore count: " + CPUSemaphore.getMutex());
//        System.out.println("PCBSemaphore count: " + PCBSemaphore.getCount());

        targetFrame.refresh();
    }

    private static void doRRMoving() {
        MyPCB myPCB = RunningPCB.getRunningPCB();
        if (myPCB == null) return;
        MyProgress myProgress = myPCB.getMyProgress();
        myProgress.setTime(myProgress.getTime() - step);
        RunningPCB.updateTimeSlice();
        if (RunningPCB.getTimeSlice() == 0 || myProgress.getTime() == 0) RunningPCB.cutRunning();

//        System.out.println("##############################");
//        System.out.println("CPUSemaphore count: " + CPUSemaphore.getMutex());
//        System.out.println("PCBSemaphore count: " + PCBSemaphore.getCount());

        targetFrame.refresh();
    }

    private static void doSJFMoving() {
        doFCFSMoving();
    }
}
