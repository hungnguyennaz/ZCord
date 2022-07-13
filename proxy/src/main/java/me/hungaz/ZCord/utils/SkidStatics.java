package me.hungaz.ZCord.utils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class SkidStatics {

    private AtomicInteger connectionsPerSecond = new AtomicInteger(),
            totalConnectionsPerSecond = new AtomicInteger();

    public int getConnectionsPerSecond() {
        return this.connectionsPerSecond.get();
    }

    public void addConnectionPerSecond() {
        this.connectionsPerSecond.incrementAndGet();
    }

    public void addTotalConnectionPerSecond() {
        this.totalConnectionsPerSecond.incrementAndGet();
    }

    public void startUpdating() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            int cpsBefore = 0;
            int totalBefore = 0;

            @Override
            public void run() {
                int currentCps = connectionsPerSecond.get();
                if (currentCps > 0) {
                    connectionsPerSecond.set(connectionsPerSecond.get() - cpsBefore);

                    cpsBefore = connectionsPerSecond.get();
                }
                int total = totalConnectionsPerSecond.get();
                if (total > 0) {
                    totalConnectionsPerSecond.set(totalConnectionsPerSecond.get() - totalBefore);

                    totalBefore = totalConnectionsPerSecond.get();
                }
            }
        }, 1000, 1000);
    }
}