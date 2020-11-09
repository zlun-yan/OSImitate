package org.csu.os.domain.table;

import org.csu.os.domain.pojo.MyProgress;

import java.util.ArrayList;

public class RecordTable {
    private static ArrayList<MyProgress> items = new ArrayList<>();
    private static int count = 0;

    public static void addProgress(MyProgress myProgress) {
        items.add(myProgress);
        count++;
    }

    public static ArrayList<MyProgress> getQueue() {
        return items;
    }

    public static int getCount() {
        return count;
    }

    public static double getAveWaitTime() {
        double totTime = 0;
        double havePCBCount = 0;
        for (int i = 0; i < count; i++) {
            if (items.get(i).inMemory()) {
                totTime += items.get(i).getWaitTime();
                havePCBCount++;
            }
        }

        if (havePCBCount == 0) return 0;
        return totTime / havePCBCount;
    }

    public static void clear() {
        count = 0;
        items.clear();
    }
}
