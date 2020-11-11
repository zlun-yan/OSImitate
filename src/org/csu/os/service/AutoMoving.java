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

    private static int step = 1;  // 这个要出问题的  看看doRRMoving里面
    private static int systemTime = 0;
    private static MainFrame targetFrame;

    private static Timer SystemTimer;

    public static void start(MainFrame targetFrame) {
        if (state) return; // 如果已经开始了就返回吧
        state = true;
        AutoMoving.targetFrame = targetFrame;
        SystemTimer = new Timer(true);
        SystemTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                doMoving();
            }
        }, 0, 1000); // 每100毫秒进行一次
    }

    public static int getSystemTime() {
        return systemTime;
    }

    public static void pause() {
        if (!state) return;
        state = false;
        SystemTimer.cancel();
        HangUp.doHangUp(targetFrame);
    }

    private static void doMoving() {
        systemTime++;
        ReadyQueue.updateWaitTime(step);

        switch (mode) {
            case FCFS:
                doFCFSMoving();
                break;
            case PSA:
                doPSAMoving();
                break;
            case RR:
                doRRMoving();
                break;
            case SJF:
                doSJFMoving();
                break;
            case MQ:
                doMQMoving();
                break;
            case MFQ:
                doMFQMoving();
                break;
            default:
                break;
        }
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

        targetFrame.refresh();
    }

    private static void doRRMoving() {
        MyPCB myPCB = RunningPCB.getRunningPCB();
        if (myPCB == null) return;
        MyProgress myProgress = myPCB.getMyProgress();
        myProgress.setTime(myProgress.getTime() - step);
        RunningPCB.updateTimeSlice(step);  // 看吧 这里要出问题的  因为这个每次只是更新1 而已
        if (RunningPCB.getTimeSlice() <= 0 || myProgress.getTime() <= 0) RunningPCB.cutRunning();

        targetFrame.refresh();
    }

    private static void doSJFMoving() {
        doFCFSMoving();
    }

    private static void doMQMoving() {
        MyPCB myPCB = RunningPCB.getRunningPCB();
        if (myPCB == null) return;
        MyProgress myProgress = myPCB.getMyProgress();
        myProgress.setTime(myProgress.getTime() - step);
        if (myProgress.getQueueOrder() == 1) {
            // 前台队列
            RunningPCB.updateTimeSlice(step);  // 执行时间片轮转操作
            if (RunningPCB.getTimeSlice() <= 0 || myProgress.getTime() <= 0) RunningPCB.cutRunning();
        }
        else {
            // 后台队列
//            myProgress.setPriority(myProgress.getPriority() - step);  还是不可抢占吧
            if (myProgress.getTime() <= 0) RunningPCB.finishRunning();
        }

        targetFrame.refresh();
    }

    private static void doMFQMoving() {
        MyPCB myPCB = RunningPCB.getRunningPCB();
        if (myPCB == null) return;
        MyProgress myProgress = myPCB.getMyProgress();
        myProgress.setTime(myProgress.getTime() - step);
        RunningPCB.updateTimeSlice(step);  // 看吧 这里要出问题的  因为这个每次只是更新1 而已
        if (RunningPCB.getTimeSlice() <= 0 || myProgress.getTime() <= 0) {
            myProgress.setQueueOrder(myProgress.getQueueOrder() + 1);
            RunningPCB.cutRunning();
        }

        targetFrame.refresh();
    }

    public static void clear() {
        pause();
        step = 1;
        systemTime = 0;
    }
}
