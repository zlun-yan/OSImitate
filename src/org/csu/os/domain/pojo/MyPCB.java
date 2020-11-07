package org.csu.os.domain.pojo;

public class MyPCB {
    private boolean busy;
    private String state;
    private int pid;
    private int next;

    private MyProgress myProgress;

    public MyPCB() {

    }

    public boolean isBusy() {
        return busy;
    }

    public int getPid() {
        return pid;
    }

    public int getNext() {
        return next;
    }

    public String getState() {
        return state;
    }

    public MyProgress getMyProgress() {
        return myProgress;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setMyProgress(MyProgress myProgress) {
        this.myProgress = myProgress;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public void setNext(int next) {
        this.next = next;
    }
}
