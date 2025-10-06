package com.ilyassan.medicalteleexpertise.model;

import com.ilyassan.medicalteleexpertise.repository.BaseRepository;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "technical_acts")
public class TechnicalAct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double price;

    @ManyToMany(mappedBy = "technicalActs")
    private List<Consultation> consultations = new ArrayList<>();

    public TechnicalAct() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(List<Consultation> consultations) {
        this.consultations = consultations;
    }

    // Repository instance
    private static final BaseRepository<TechnicalAct> repository = new BaseRepository<>(TechnicalAct.class);

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

    public static TechnicalAct find(Long id) {
        return repository.find(id);
    }

    public static List<TechnicalAct> all() {
        return repository.all();
    }
}
