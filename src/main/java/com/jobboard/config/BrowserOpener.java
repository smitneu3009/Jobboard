package com.jobboard.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.awt.Desktop;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class BrowserOpener implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(BrowserOpener.class);

    @Override
    public void run(String... args) {
        try {
            Thread.sleep(2000); // Wait for the server to start
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI("http://localhost:8080"));
                logger.info("Opening browser at http://localhost:8080");
            }
        } catch (Exception e) {
            logger.error("Failed to open browser: {}", e.getMessage());
        }
    }
}