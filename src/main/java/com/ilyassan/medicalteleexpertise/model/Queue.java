package com.ilyassan.medicalteleexpertise.model;

import com.ilyassan.medicalteleexpertise.repository.BaseRepository;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "queues")
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false, updatable = false)
    private LocalDateTime arrivalTime;

    @PrePersist
    protected void onCreate() {
        arrivalTime = LocalDateTime.now();
    }

    public Queue() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    // Repository instance
    private static final BaseRepository<Queue> repository = new BaseRepository<>(Queue.class);

    // CRUD Methods
    public void create() {
        repository.create(this);
    }

    public void update() {
        repository.update(this);
    }

    public void delete() {
        repository.delete(this);
    }

    public static Queue find(Long id) {
        return repository.find(id);
    }

    public static List<Queue> all() {
        return repository.all();
    }
}
