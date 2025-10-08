package com.ilyassan.medicalteleexpertise.repository;

import com.ilyassan.medicalteleexpertise.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class BaseRepository<T> {

    private final Class<T> entityClass;

    public BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Execute operation within transaction
     */
    protected void executeInTransaction(Consumer<EntityManager> operation) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            operation.accept(em);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Execute query and return result
     */
    protected <R> R executeQuery(Function<EntityManager, R> query) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return query.apply(em);
        } finally {
            em.close();
        }
    }

    /**
     * Create/persist entity
     */
    public void create(T entity) {
        executeInTransaction(em -> em.persist(entity));
    }

    /**
     * Update/merge entity
     */
    public void update(T entity) {
        executeInTransaction(em -> em.merge(entity));
    }

    /**
     * Delete entity
     */
    public void delete(T entity) {
        executeInTransaction(em -> {
            Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
            T managedEntity = em.find(entityClass, id);
            if (managedEntity != null) {
                em.remove(managedEntity);
            }
        });
    }

    /**
     * Find entity by ID
     */
    public T find(Long id) {
        return executeQuery(em -> em.find(entityClass, id));
    }

    /**
     * Get all entities
     */
    public List<T> all() {
        return executeQuery(em -> {
            String queryString = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            TypedQuery<T> query = em.createQuery(queryString, entityClass);
            return query.getResultList();
        });
    }
}
