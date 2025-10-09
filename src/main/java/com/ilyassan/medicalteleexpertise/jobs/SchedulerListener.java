package com.ilyassan.medicalteleexpertise.jobs;

import com.ilyassan.medicalteleexpertise.model.Consultation;
import com.ilyassan.medicalteleexpertise.service.ConsultationService;
import com.ilyassan.medicalteleexpertise.service.JitsiMeetService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@WebListener
public class SchedulerListener implements ServletContextListener {

    private final ConsultationService consultationService = new ConsultationService();
    private final JitsiMeetService jitsiMeetService = new JitsiMeetService();
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            List<Consultation> consultations = consultationService.getAllConsultations();
            LocalDateTime nowDateTime = LocalDateTime.now();

            // Filter consultations needing a meet link
            Stream<Consultation> progressedConsultations = consultations.stream().filter(c ->
                    c.getMeetLink() == null &&
                            nowDateTime.isAfter(c.getCreatedAt()) &&
                            nowDateTime.isBefore(c.getDate())
            );

            progressedConsultations.forEach(consultation -> {
                // Generate Jitsi link
                String meetLink = jitsiMeetService.createMeetLinkForConsultation(consultation);
                consultation.setMeetLink(meetLink);
                consultation.update(); // Persist to DB
            });
        }, 0, 1, TimeUnit.MINUTES);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }
}