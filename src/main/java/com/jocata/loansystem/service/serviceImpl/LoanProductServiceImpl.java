package com.jocata.loansystem.service.serviceImpl;

import com.jocata.loansystem.dao.LoanProductDao;
import com.jocata.loansystem.entity.LoanProductDetails;
import com.jocata.loansystem.form.LoanProductRequestForm;
import com.jocata.loansystem.form.LoanProductResponseForm;
import com.jocata.loansystem.service.LoanProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class LoanProductServiceImpl implements LoanProductService {
//    @Autowired
    private LoanProductDao loanProductDao;

    public LoanProductServiceImpl(LoanProductDao loanProductDao) {
        this.loanProductDao = loanProductDao;
    }

    public LoanProductDetails beanToEntity(LoanProductRequestForm loanProductRequestForm){
        LoanProductDetails loanProductDetails=new LoanProductDetails();
        loanProductDetails.setProductName(loanProductRequestForm.getName());
        loanProductDetails.setDescription(loanProductRequestForm.getDescription());
        loanProductDetails.setMinAmount(new BigDecimal(loanProductRequestForm.getMinAmount()));
        loanProductDetails.setMaxAmount(new BigDecimal(loanProductRequestForm.getMaxAmount()));
        loanProductDetails.setTermMonths(Integer.parseInt(loanProductRequestForm.getTermMonth()));
        loanProductDetails.setInterestRate(new BigDecimal(loanProductRequestForm.getInterestRate()));
        return loanProductDetails;
    }
    public LoanProductResponseForm entityToBean(LoanProductDetails loanProductDetails){
        LoanProductResponseForm loanProductResponseForm=new LoanProductResponseForm();
        loanProductResponseForm.setId(String.valueOf(loanProductDetails.getProductId()));
        loanProductResponseForm.setName(loanProductDetails.getProductName());
        loanProductResponseForm.setDescription(loanProductDetails.getDescription());
        loanProductResponseForm.setInterestRate(String.valueOf(loanProductDetails.getInterestRate()));
        loanProductResponseForm.setMinAmount(String.valueOf(loanProductDetails.getMinAmount()));
        loanProductResponseForm.setMaxAmount(String.valueOf(loanProductDetails.getMaxAmount()));
        loanProductResponseForm.setTermMonth(String.valueOf(loanProductDetails.getTermMonths()));
        return loanProductResponseForm;
    }

    public LoanProductDetails responseBeanToEntity(LoanProductResponseForm loanProductResponseForm){
        LoanProductDetails loanProductDetails=new LoanProductDetails();
        loanProductDetails.setProductId(Integer.parseInt(loanProductResponseForm.getId()));
        loanProductDetails.setProductName(loanProductResponseForm.getName());
        loanProductDetails.setDescription(loanProductResponseForm.getDescription());
        loanProductDetails.setMinAmount(new BigDecimal(loanProductResponseForm.getMinAmount()));
        loanProductDetails.setMaxAmount(new BigDecimal(loanProductResponseForm.getMaxAmount()));
        loanProductDetails.setInterestRate(new BigDecimal(loanProductResponseForm.getInterestRate()));
        loanProductDetails.setTermMonths(Integer.parseInt(loanProductResponseForm.getTermMonth()));
        return loanProductDetails;
    }
    @Override
    public LoanProductResponseForm createProduct(LoanProductRequestForm loanProductRequestForm) {
        return entityToBean(loanProductDao.create(beanToEntity(loanProductRequestForm)));
    }

    @Override
    public LoanProductResponseForm getProduct(String id) {
        return entityToBean(loanProductDao.get(Integer.parseInt(id)));
    }

    @Override
    public LoanProductResponseForm updateProduct(LoanProductResponseForm loanProductResponseForm) {
        return entityToBean(loanProductDao.update(responseBeanToEntity(loanProductResponseForm)));
    }

    @Override
    public String deleteProduct(String id) {
        return loanProductDao.delete(Integer.parseInt(id));
    }
}
