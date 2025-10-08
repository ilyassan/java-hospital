package com.ilyassan.medicalteleexpertise.service;

import com.ilyassan.medicalteleexpertise.model.Patient;
import com.ilyassan.medicalteleexpertise.model.Queue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class QueueService {

    public List<Queue> getAllQueuesSortedByArrival() {
        return Queue.all().stream()
                .sorted((q1, q2) -> q1.getArrivalTime().compareTo(q2.getArrivalTime()))
                .collect(Collectors.toList());
    }

    public boolean isPatientInQueue(Long patientId) {
        return Queue.all().stream()
                .anyMatch(q -> q.getPatient().getId().equals(patientId));
    }

    public Set<Long> getPatientIdsInQueue() {
        return Queue.all().stream()
                .map(q -> q.getPatient().getId())
                .collect(Collectors.toSet());
    }

    public Queue findById(Long id) {
        return Queue.find(id);
    }

    public void addPatientToQueue(Patient patient) {
        Queue queue = new Queue();
        queue.setPatient(patient);
        queue.create();
    }
}
