package com.nbcuni.feeds;


import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.nbcuni.feeds.services.McpApiService;

@Service
public class IngestExcecutor {

    private final McpApiService mcpApiService;

    public IngestExcecutor(McpApiService mcpApiService) {
        this.mcpApiService = mcpApiService;
    }

    @Async
    public void execute() {
        while (true) {
            mcpApiService.runFeeds();

        }
    }
}
