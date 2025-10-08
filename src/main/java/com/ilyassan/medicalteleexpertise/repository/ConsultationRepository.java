package com.ilyassan.medicalteleexpertise.repository;

import com.ilyassan.medicalteleexpertise.model.Consultation;
import com.ilyassan.medicalteleexpertise.util.JpaUtil;
import jakarta.persistence.EntityManager;

public class ConsultationRepository extends BaseRepository<Consultation> {

    public ConsultationRepository() {
        super(Consultation.class);
    }

    /**
     * Find consultation by ID with technical acts eagerly loaded
     */
    public Consultation findByIdWithTechnicalActs(Long id) {
        return executeQuery(em -> {
            Consultation consultation = em.createQuery(
                    "SELECT c FROM Consultation c " +
                    "LEFT JOIN FETCH c.technicalActs " +
                    "WHERE c.id = :id", Consultation.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return consultation;
        });
    }
}
