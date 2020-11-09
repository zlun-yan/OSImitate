package org.csu.os.domain.pojo;

public class MyProgress {
    private String name;
    private int priority;
    private int time;

    private int queueOrder;

    private int startTime = -1; // 记录进程新建的时间
    private int responseTime = -1;  // 记录进程第一次得到相应花费的时间
    private int turnAroundTime = -1;  // 记录进程从开始到完成花费的时间

    private int waitTime = -1;  // 记录进程在Ready队列中等待的时间
    private boolean state = false;  // 用于记录进程是否进内存

    public MyProgress() {

    }

    public int getQueueOrder() {
        return queueOrder;
    }

    public void setQueueOrder(int queueOrder) {
        this.queueOrder = queueOrder;
    }

    public void setResponseTime(int responseTime) {
        if (this.responseTime == -1) {
            this.responseTime = responseTime - startTime;
        }
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setTurnAroundTime(int turnAroundTime) {
        this.turnAroundTime = turnAroundTime - startTime;
    }

    public void updateWaitTime(int step) {
        waitTime += step;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void havePCB() {
        this.state = true;
    }

    public int getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public int getTurnAroundTime() {
        return turnAroundTime;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public boolean inMemory() {
        return state;
    }
}
