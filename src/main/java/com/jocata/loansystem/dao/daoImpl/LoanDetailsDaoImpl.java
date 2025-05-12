package com.jocata.loansystem.dao.daoImpl;

import com.jocata.loansystem.dao.LoanDetailsDao;
import com.jocata.loansystem.entity.LoanDetails;
import com.jocata.loansystem.entity.LoanProductDetails;
import jakarta.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class LoanDetailsDaoImpl implements LoanDetailsDao {

    private final SessionFactory sessionFactory;

    public LoanDetailsDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public LoanDetails create(LoanDetails loanDetails) {
        try (Session session=sessionFactory.openSession()){
            Transaction tx= session.beginTransaction();
            try {
                session.persist(loanDetails);
                tx.commit();
                session.close();
                return loanDetails;
            }catch (Exception e){
                tx.rollback();
                session.close();
                throw new PersistenceException("Loan Creation failed.");
            }
        }
    }

    @Override
    public LoanDetails getLoanByLoanId(String loanId) {
        Session session=sessionFactory.getCurrentSession();
        String sql="FROM LoanDetails l WHERE l.loanId= :id";
        Query<LoanDetails> query=session.createQuery(sql,LoanDetails.class);
        query.setParameter("id",loanId);
        return query.uniqueResult();
    }
}
