package edu.shtoiko.authservice.service.implementation;

import edu.shtoiko.authservice.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionScheduler {
    private final UserSessionService sessionService;

    @Scheduled(cron = "0 0 * * * *")
    public void removeExpiredSessions(){
        sessionService.deleteExpiredSessions();
    }
}
