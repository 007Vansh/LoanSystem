package com.jocata.loansystem.dao.daoImpl;

import com.jocata.loansystem.dao.CreditScoreDao;
import com.jocata.loansystem.entity.CreditScoreDetails;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class CreditScoreDaoImpl implements CreditScoreDao {
    private SessionFactory sessionFactory;

    public CreditScoreDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public CreditScoreDetails createCreditScore(CreditScoreDetails creditScoreDetails) {
        Session session= sessionFactory.openSession();
        Transaction tx=session.beginTransaction();
        session.persist(creditScoreDetails);
        tx.commit();
        return creditScoreDetails;
    }

    @Override
    public CreditScoreDetails getCustomerFromCreditScore(int customerId) {
        Session session=sessionFactory.getCurrentSession();
        String sql="FROM CreditScoreDetails c WHERE c.customer.customerId= :id";
        Query<CreditScoreDetails> query=session.createQuery(sql,CreditScoreDetails.class);
        query.setParameter("id",customerId);
        return query.uniqueResult();
    }

    @Override
    public void updateCreditScore(CreditScoreDetails existingScore) {
        Session session=sessionFactory.openSession();
        Transaction tx=session.beginTransaction();
        session.merge(existingScore);
        tx.commit();
    }
}
