package org.mangorage.filehost.common.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Scheduler {
    public final static ScheduledExecutorService RUNNER = Executors.newScheduledThreadPool(32);
}
