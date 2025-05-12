package com.jocata.loansystem.dao.daoImpl;

import com.jocata.loansystem.dao.LoanDisbursementDao;
import com.jocata.loansystem.entity.LoanDisbursementDetails;
import jakarta.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class LoanDisbursementDaoImpl implements LoanDisbursementDao {
    private final SessionFactory sessionFactory;

    public LoanDisbursementDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public LoanDisbursementDetails create(LoanDisbursementDetails loanDisbursementDetails) {
        try (Session session=sessionFactory.openSession()){
            Transaction tx=session.beginTransaction();
            try {
                session.persist(loanDisbursementDetails);
                tx.commit();
                session.close();
                return loanDisbursementDetails;
            } catch (Exception e) {
                tx.rollback();
                session.close();
                throw new PersistenceException("Disbursement save Failed.");
            }
        }
    }
}
