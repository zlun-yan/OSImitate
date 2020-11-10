package org.csu.os.service;

public class DispatchMode {
    public enum Mode {PSA, RR, SJF, FCFS, MQ, MFQ}
    public static Mode mode = Mode.FCFS;
}
