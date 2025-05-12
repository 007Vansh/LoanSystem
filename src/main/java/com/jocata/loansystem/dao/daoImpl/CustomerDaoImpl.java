package com.jocata.loansystem.dao.daoImpl;

import com.jocata.loansystem.dao.CustomerDao;
import com.jocata.loansystem.entity.CustomerDetails;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerDaoImpl implements CustomerDao {
    private final SessionFactory sessionFactory;

    public CustomerDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public CustomerDetails createCustomer(CustomerDetails customerDetails) {
        Session session= sessionFactory.openSession();
        Transaction tx=session.beginTransaction();
        session.persist(customerDetails);
        tx.commit();
        return customerDetails;
    }

    @Override
    public CustomerDetails getCustomer(String panNumber) {
        Session session=sessionFactory.getCurrentSession();
        String sql="FROM CustomerDetails c WHERE c.identityNumber= :id";
        Query<CustomerDetails> query=session.createQuery(sql, CustomerDetails.class);
        query.setParameter("id",panNumber);
        return query.uniqueResult();
    }
}
