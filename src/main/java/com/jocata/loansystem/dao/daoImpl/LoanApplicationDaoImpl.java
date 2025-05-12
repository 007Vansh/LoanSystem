package com.jocata.loansystem.dao.daoImpl;

import com.jocata.loansystem.dao.LoanApplicationDao;
import com.jocata.loansystem.entity.CustomerDetails;
import com.jocata.loansystem.entity.LoanApplicationDetails;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class LoanApplicationDaoImpl implements LoanApplicationDao {
    private SessionFactory sessionFactory;

    public LoanApplicationDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public LoanApplicationDetails createLoan(LoanApplicationDetails loanApplicationDetails) {
        Session session=sessionFactory.openSession();
        Transaction tx=session.beginTransaction();
        session.persist(loanApplicationDetails);
        tx.commit();
        return loanApplicationDetails;
    }

    @Override
    public CustomerDetails getCustomerFromLoan(int id) {
        Session session=sessionFactory.getCurrentSession();
        String sql="SELECT l.customer FROM LoanApplicationDetails l WHERE l.customer.customerId = :id";
        Query<CustomerDetails> query=session.createQuery(sql, CustomerDetails.class);
        query.setParameter("id",id);
        return query.uniqueResult();
    }
    @Override
    public LoanApplicationDetails updateLoan(LoanApplicationDetails loanApplicationDetails) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.merge(loanApplicationDetails);
        transaction.commit();
        return loanApplicationDetails;
    }

    @Override
    public LoanApplicationDetails getLatestLoanApplicationByCustomerId(Integer customerId) {
        Session session=sessionFactory.getCurrentSession();
        String hql = "FROM LoanApplicationDetails l WHERE l.customer.customerId = :customerId ORDER BY l.applicationDate DESC";
        return session.createQuery(hql, LoanApplicationDetails.class)
                .setParameter("customerId", customerId)
                .setMaxResults(1)
                .uniqueResult();
    }

    @Override
    public LoanApplicationDetails getApplicationFromId(String applicationId) {
        Session session=sessionFactory.getCurrentSession();
        String sql="FROM LoanApplicationDetails l WHERE l.applicationId= :id";
        Query<LoanApplicationDetails> query=session.createQuery(sql, LoanApplicationDetails.class);
        query.setParameter("id",applicationId);
        return query.uniqueResult();
    }
}
