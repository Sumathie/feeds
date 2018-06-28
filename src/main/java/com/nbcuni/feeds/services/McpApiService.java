package com.nbcuni.feeds.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class McpApiService {


    @Autowired
    private McpService mcpService;

    public void runFeeds() {
        try {
            mcpService.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
