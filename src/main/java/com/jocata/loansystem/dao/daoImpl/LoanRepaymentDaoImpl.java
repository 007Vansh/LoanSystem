package com.jocata.loansystem.dao.daoImpl;

import com.jocata.loansystem.dao.LoanRepaymentDao;
import com.jocata.loansystem.entity.LoanPaymentDetails;
import jakarta.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class LoanRepaymentDaoImpl implements LoanRepaymentDao {
    private final SessionFactory sessionFactory;

    public LoanRepaymentDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public String rePayment(LoanPaymentDetails loanPaymentDetails) {
        try(Session session=sessionFactory.openSession()){
            Transaction tx=session.beginTransaction();
            try{
                session.persist(loanPaymentDetails);
                tx.commit();
                session.close();
                return "Re-Payment Successfull.";
            }catch (Exception e){
                tx.rollback();
                throw new PersistenceException("Re-payment Failed.");
            }
        }
    }

    @Override
    public Long getinstllment(String loanId) {
        Session session=sessionFactory.getCurrentSession();
        String sql="SELECT COUNT(*) FROM LoanPaymentDetails l WHERE l.loan.loanId= :id ";
        Query<Long> query=session.createQuery(sql, Long.class);
        query.setParameter("id",loanId);
        return query.uniqueResult();
    }
}
