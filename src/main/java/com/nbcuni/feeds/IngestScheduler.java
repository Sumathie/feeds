package com.nbcuni.feeds;

import java.util.concurrent.RejectedExecutionException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class IngestScheduler {

    private final IngestExcecutor executor;

    public IngestScheduler(IngestExcecutor executor) {
        this.executor = executor;
    }

    @Scheduled(cron = "${intake.schedule.cron:0/15 * * * * *}")
    public void schedule() {
        try {
            executor.execute();
        } catch (RejectedExecutionException e) {

        }
    }
}
