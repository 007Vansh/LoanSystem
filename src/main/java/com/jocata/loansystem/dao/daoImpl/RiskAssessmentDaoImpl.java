package com.jocata.loansystem.dao.daoImpl;

import com.jocata.loansystem.dao.RiskAssessmentDao;
import com.jocata.loansystem.entity.LoanProductDetails;
import com.jocata.loansystem.entity.RiskAssessmentDetails;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class RiskAssessmentDaoImpl implements RiskAssessmentDao {
    private final SessionFactory sessionFactory;

    public RiskAssessmentDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public RiskAssessmentDetails save(RiskAssessmentDetails riskAssessmentDetails) {
        Session session=sessionFactory.getCurrentSession();
        Transaction transaction=session.beginTransaction();
        session.persist(riskAssessmentDetails);
        transaction.commit();
        return riskAssessmentDetails;
    }

    @Override
    public RiskAssessmentDetails get(String applicationId) {
        Session session=sessionFactory.getCurrentSession();
        String sql="FROM RiskAssessmentDetails r WHERE r.loanApplication.applicationId= :id";
        Query<RiskAssessmentDetails> query=session.createQuery(sql,RiskAssessmentDetails.class);
        query.setParameter("id",applicationId);
        return query.uniqueResult();
    }
}
