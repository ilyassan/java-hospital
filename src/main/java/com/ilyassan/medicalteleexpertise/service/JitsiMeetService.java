package com.ilyassan.medicalteleexpertise.service;

import com.ilyassan.medicalteleexpertise.model.Consultation;
import java.util.UUID;

public class JitsiMeetService {
    public String createMeetLinkForConsultation(Consultation consultation) {
        String roomName = "consultation-" + consultation.getId() + "-" + UUID.randomUUID().toString().substring(0, 8);
        return "https://meet.jit.si/" + roomName;
    }
}