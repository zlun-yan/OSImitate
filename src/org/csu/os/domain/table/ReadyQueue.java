package org.csu.os.domain.table;

import org.csu.os.domain.pojo.MyPCB;
import org.csu.os.domain.pojo.MyProgress;
import org.csu.os.domain.signal.CPUSemaphore;
import org.csu.os.service.AutoMoving;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static org.csu.os.service.DispatchMode.*;

public class ReadyQueue {
    private static ArrayList<MyPCB> items = new ArrayList<>();
    private static int count = 0;

    public static void addPCB(MyPCB myPCB) {
        if (myPCB == null) return;
        if (CPUSemaphore.waitSemaphore()) {
            myPCB.setState("waiting");
            items.add(myPCB);
            count++;

            if (mode == Mode.PSA) PCBGetPSASort();
            else if (mode == Mode.SJF) PCBGetSJFSort();
            else PCBGetMQAndMFQSort();
            refreshNextPoint();
        }
        else {
            // CPU 空闲
            if (mode == Mode.MFQ) RunningPCB.setTimeSliceDefault((int) Math.pow(2, myPCB.getMyProgress().getQueueOrder() + 1));
            RunningPCB.setRunningPCB(myPCB);
        }
    }

    private static MyPCB PCBPop() {
        if (count == 0) return null;
        if (mode == Mode.PSA) PCBGetPSASort();
        else if (mode == Mode.SJF) PCBGetSJFSort();
        else PCBGetMQAndMFQSort();
        refreshNextPoint();
        count--;

        items.get(0).getMyProgress().setResponseTime(AutoMoving.getSystemTime());
        return items.remove(0);
    }

    public static MyPCB PCBHang(int index) {
        count--;
        return items.remove(index);
    }

    private static void PCBGetPSASort() {
        Collections.sort(items, new Comparator<MyPCB>() {
            @Override
            public int compare(MyPCB o1, MyPCB o2) {
                MyProgress myProgress1 = o1.getMyProgress();
                MyProgress myProgress2 = o2.getMyProgress();
                if (myProgress1.getPriority() < myProgress2.getPriority()) return 1;
                else return -1;
            }
        });

        refreshNextPoint();
    }

    private static void PCBGetSJFSort() {
        Collections.sort(items, new Comparator<MyPCB>() {
            @Override
            public int compare(MyPCB o1, MyPCB o2) {
                MyProgress myProgress1 = o1.getMyProgress();
                MyProgress myProgress2 = o2.getMyProgress();
                if (myProgress1.getTime() > myProgress2.getTime()) return 1;
                else return -1;
            }
        });

        refreshNextPoint();
    }

    private static void PCBGetMQAndMFQSort() {
        Collections.sort(items, new Comparator<MyPCB>() {
            // 只需要按照队列顺序排好就行了
            @Override
            public int compare(MyPCB o1, MyPCB o2) {
                MyProgress myProgress1 = o1.getMyProgress();
                MyProgress myProgress2 = o2.getMyProgress();
                if (myProgress1.getQueueOrder() > myProgress2.getQueueOrder()) return 1;
                else return -1;
            }
        });

        refreshNextPoint();
    }

    private static void refreshNextPoint() {
        for (int i = 0; i < count - 1; i++) {
            items.get(i).setNext(items.get(i + 1).getPid());
        }

        items.get(count - 1).setNext(0);
    }

    public static void wakeUp() {
        RunningPCB.setRunningPCB(PCBPop());
    }

    public static ArrayList<MyPCB> getQueue() {
        return items;
    }

    public static int getCount() {
        return count;
    }

    public static void updateWaitTime(int step) {
        for (int i = 0; i < count; i++) {
            items.get(i).getMyProgress().updateWaitTime(step);
        }
    }

    public static void clear() {
        count = 0;
        items.clear();
    }
}
