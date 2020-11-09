package org.csu.os.domain.table;

import org.csu.os.domain.pojo.MyProgress;

import java.util.ArrayList;

public class BackUpQueue {
    private static ArrayList<MyProgress> items = new ArrayList<>();
    private static int count = 0;

    public static void addProgress(MyProgress myProgress) {
        items.add(myProgress);
        count++;
    }

    public static MyProgress getFirst() {
        count--;
        return items.remove(0);
    }

    public static ArrayList<MyProgress> getQueue() {
        return items;
    }

    public static int getCount() {
        return count;
    }

    public static void wakeUp() {
        PCBQueue.addProgress(getFirst());
    }

    public static void clear() {
        count = 0;
        items.clear();
    }
}
