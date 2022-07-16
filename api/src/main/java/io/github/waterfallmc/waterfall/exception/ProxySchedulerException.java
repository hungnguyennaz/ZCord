package io.github.waterfallmc.waterfall.exception;

import net.md_5.bungee.api.scheduler.ScheduledTask;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Thrown when a plugin's scheduler fails with an exception
 */
public class ProxySchedulerException extends ProxyPluginException {

    private final ScheduledTask task;

    public ProxySchedulerException(String message, Throwable cause, ScheduledTask task) {
        super(message, cause, task.getOwner());
        this.task = checkNotNull(task, "task");
    }

    public ProxySchedulerException(Throwable cause, ScheduledTask task) {
        super(cause, task.getOwner());
        this.task = checkNotNull(task, "task");
    }

    protected ProxySchedulerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ScheduledTask task) {
        super(message, cause, enableSuppression, writableStackTrace, task.getOwner());
        this.task = checkNotNull(task, "task");
    }

    /**
     * Gets the task which threw the exception
     *
     * @return exception throwing task
     */
    public ScheduledTask getTask() {
        return task;
    }
}
