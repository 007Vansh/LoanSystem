package com.jocata.loansystem.dao.daoImpl;

import com.jocata.loansystem.dao.LoanProductDao;
import com.jocata.loansystem.entity.LoanProductDetails;
import jakarta.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class LoanProductDaoImpl implements LoanProductDao {

//    @Autowired
    private final SessionFactory sessionFactory;

    public LoanProductDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public LoanProductDetails create(LoanProductDetails loanProductDetails) {
        Session session= sessionFactory.openSession();
        Transaction tx=session.beginTransaction();
        session.persist(loanProductDetails);
        tx.commit();
        return loanProductDetails;
    }

    @Override
    public LoanProductDetails get(int id) {
        Session session=sessionFactory.getCurrentSession();
        String sql="FROM LoanProductDetails l WHERE l.productId= :id";
        Query<LoanProductDetails> query=session.createQuery(sql,LoanProductDetails.class);
        query.setParameter("id",id);
        return query.uniqueResult();
    }

    @Override
    public LoanProductDetails getProductByTerm(int term) {
        Session session=sessionFactory.getCurrentSession();
        String sql="FROM LoanProductDetails l WHERE l.termMonths= :term";
        Query<LoanProductDetails> query=session.createQuery(sql,LoanProductDetails.class);
        query.setParameter("term",term);
        return query.uniqueResult();
    }

    @Override
    public LoanProductDetails update(LoanProductDetails loanProductDetails) {
        Session session= sessionFactory.openSession();
        Transaction tx=session.beginTransaction();
        session.merge(loanProductDetails);
        tx.commit();
        return loanProductDetails;
    }

    @Override
    public String delete(int id) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        String sql = "DELETE FROM LoanProductDetails l WHERE l.productId= :id";
        Query query = session.createQuery(sql);
        query.setParameter("id", id);
        int result = query.executeUpdate();
        tx.commit();
        if(result>0){
            return "Product Deleted Successfully.";
        }
        return "Product Not Found";
    }
    @Override
    public List<LoanProductDetails> getAll() {
        try (Session session = sessionFactory.openSession()) {
            String sql = "FROM LoanProductDetails";
            Query<LoanProductDetails> query = session.createQuery(sql, LoanProductDetails.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new PersistenceException("Failed to retrieve loan products", e);
        }
    }
}
