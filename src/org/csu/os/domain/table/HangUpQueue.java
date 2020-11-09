package org.csu.os.domain.table;

import org.csu.os.domain.pojo.MyPCB;
import org.csu.os.domain.pojo.MyProgress;

import java.util.ArrayList;

public class HangUpQueue {
    private static ArrayList<MyPCB> items = new ArrayList<>();
    private static int count = 0;

    public static void addPCB(MyPCB myPCB) {
        myPCB.setState("hanging");
        items.add(myPCB);
        count++;
    }

    public static MyPCB PCBPop(int index) {
        if (count == 0) return null;
        count--;
        return items.remove(index);
    }

    public static ArrayList<MyPCB> getQueue() {
        return items;
    }

    public static int getCount() {
        return count;
    }

    public static void clear() {
        count = 0;
        items.clear();
    }
}
