/*
package com.jocata.loansystem.util;

public class EntityManagerUtil {

    @Autowired
    private HibernateUtil hibernateUtil;

    @Autowired
    private PlatformTransactionManager transactionManager;

    public <T> void createEntity(T entity) {
        TransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);
        try {
            hibernateUtil.save(entity);
            transactionManager.commit(status); // Commit transaction
        } catch (Exception e) {
            transactionManager.rollback(status); // Rollback if any error occurs
            throw e;  // Re-throw the exception after rollback
        }
    }

    // Read an entity by ID (generic)
    public <T> T getEntity(Class<T> clazz, long id) {
        TransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);
        T entity = null;
        try {
            entity = hibernateUtil.find(clazz, id);
            transactionManager.commit(status); // Commit if successful
        } catch (Exception e) {
            transactionManager.rollback(status); // Rollback if error
            throw e;
        }
        return entity;
    }

    // Update an entity (generic)
    public <T> void updateEntity(T entity) {
        TransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);
        try {
            hibernateUtil.update(entity);
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    // Delete an entity (generic)
    public <T> void deleteEntity(Class<T> clazz, long id) {
        TransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);
        try {
            hibernateUtil.delete(clazz, id);
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    // List all entities (generic)
    public <T> List<T> listEntities(Class<T> clazz) {
        TransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);
        List<T> entities = null;
        try {
            entities = hibernateUtil.list(clazz);
            transactionManager.commit(status);  // Commit if successful
        } catch (Exception e) {
            transactionManager.rollback(status);  // Rollback on error
            throw e;
        }
        return entities;
    }

}
*/
