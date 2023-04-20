package ru.coffeecoders.questbot.managers;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author ezuykow
 */
@Component
public class ApplicationShutdownManager {

    private final ApplicationContext context;

    public ApplicationShutdownManager(ApplicationContext context) {
        this.context = context;
    }

    public void stopBot() {
        System.exit(SpringApplication.exit(context, () -> 0));
    }
}
