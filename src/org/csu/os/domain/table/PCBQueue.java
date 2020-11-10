package org.csu.os.domain.table;

import org.csu.os.domain.pojo.MyPCB;
import org.csu.os.domain.pojo.MyProgress;
import org.csu.os.service.AutoMoving;

import java.util.ArrayList;

public class PCBQueue {
    private static ArrayList<MyPCB> items = new ArrayList<>();
    private static int count = 6;

    private static void PCBInit() {
        items.clear();
        for (int i = 1; i <= count; i++) {
            MyPCB myPCB = new MyPCB();
            myPCB.setBusy(false);
            myPCB.setPid(i);
            items.add(myPCB);
        }
    }

    public static void addProgress(MyProgress myProgress) {
        int i = 0;
        for (; i < count; i++) {
            if (!items.get(i).isBusy()) {
                items.get(i).setBusy(true);
                items.get(i).setMyProgress(myProgress);
                myProgress.setStartTime(AutoMoving.getSystemTime());
                break;
            }
        }
        // 找到一个空闲的PCB

        RecordTable.addProgress(myProgress);
        myProgress.havePCB();
        myProgress.updateWaitTime(1);
        ReadyQueue.addPCB(items.get(i));
    }

    public static void PCBRelease(int pid) {
        items.get(pid - 1).setBusy(false);
        items.get(pid - 1).getMyProgress().setTurnAroundTime(AutoMoving.getSystemTime());
    }

    public static ArrayList<MyPCB> getQueue() {
        return items;
    }

    public static int getCount() {
        return count;
    }

    public static void setCount(int count) {
        PCBQueue.count = count;
        PCBInit();
    }
}
