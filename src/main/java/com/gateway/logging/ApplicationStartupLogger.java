package com.gateway.logging;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Date;

@Component
@PropertySource("classpath:git.properties")
@PropertySource("classpath:META-INF/build-info.properties")
@RequiredArgsConstructor
public class ApplicationStartupLogger implements
        ApplicationListener<ApplicationReadyEvent> {

    private final Environment environment;
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationStartupLogger.class);

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        logApplicationStartupInfo();
    }

    private void logApplicationStartupInfo() {
        String buildVersion = environment.getProperty("build.version");
        String buildArtifact = environment.getProperty("build.artifact");
        String buildTime = environment.getProperty("build.time");
        String activeProfiles = String.join("|", environment.getActiveProfiles());
        String gitCommitTime = environment.getProperty("git.commit.time");
        String gitCommitId = environment.getProperty("git.commit.id");
        String gitBranch = environment.getProperty("git.branch");
        String port = environment.getProperty("local.server.port");
        Date applicationStartDate = getApplicationStartDate();

        String onStartupLog = String.format(
                "Microservice has been started with buildVersion=%1$s, buildArtifact=%2$s, buildTime=%3$s, runTime=%4$s, gitCommitId=%5$s, gitCommitTime=%6$s, gitBranch=%7$s, runActiveProfiles=%8$s, port=%9$s",
                buildVersion, buildArtifact, buildTime, applicationStartDate, gitCommitId, gitCommitTime, gitBranch, activeProfiles, port);

        LOGGER.info(onStartupLog);
    }

    private Date getApplicationStartDate() {
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        long startTime = rb.getStartTime();
        return new Date(startTime);
    }
}
