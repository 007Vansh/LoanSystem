package com.jocata.loansystem.controller;

import com.jocata.loansystem.form.LoanProductRequestForm;
import com.jocata.loansystem.form.LoanProductResponseForm;
import com.jocata.loansystem.service.LoanProductService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loanproduct")
public class LoanProductController {

    private final LoanProductService loanProductService;

    public LoanProductController(LoanProductService loanProductService) {
        this.loanProductService = loanProductService;
    }

    @PostMapping("/create")
    public LoanProductResponseForm createProduct(@RequestBody LoanProductRequestForm loanProductRequestForm){
        if(loanProductRequestForm.getName()!=null && !loanProductRequestForm.getName().trim().isEmpty() &&
        loanProductRequestForm.getMinAmount()!=null && !loanProductRequestForm.getMinAmount().trim().isEmpty() &&
        loanProductRequestForm.getMaxAmount()!=null && !loanProductRequestForm.getMaxAmount().trim().isEmpty() &&
        loanProductRequestForm.getTermMonth()!=null && !loanProductRequestForm.getTermMonth().trim().isEmpty() &&
        loanProductRequestForm.getInterestRate()!=null && !loanProductRequestForm.getInterestRate().trim().isEmpty()){
            return loanProductService.createProduct(loanProductRequestForm);
        }
        throw new IllegalArgumentException("Invalid Loan Product Details.");
    }

    @GetMapping("/read/{id}")
    public LoanProductResponseForm getProduct(@PathVariable String id){
        if(id!=null && !id.trim().isEmpty()){
            return loanProductService.getProduct(id);
        }
        throw new IllegalArgumentException("Invalid Product id.");
    }

    @PutMapping("/update")
    public LoanProductResponseForm updateProduct(@RequestBody LoanProductResponseForm loanProductResponseForm){
        if(loanProductResponseForm.getId()!=null && !loanProductResponseForm.getId().trim().isEmpty() &&
                loanProductResponseForm.getName()!=null && !loanProductResponseForm.getName().trim().isEmpty() &&
                loanProductResponseForm.getMinAmount()!=null && !loanProductResponseForm.getMinAmount().trim().isEmpty() &&
                loanProductResponseForm.getMaxAmount()!=null && !loanProductResponseForm.getMaxAmount().trim().isEmpty() &&
                loanProductResponseForm.getTermMonth()!=null && !loanProductResponseForm.getTermMonth().trim().isEmpty() &&
                loanProductResponseForm.getInterestRate()!=null && !loanProductResponseForm.getInterestRate().trim().isEmpty()){
            return loanProductService.updateProduct(loanProductResponseForm);
        }
        throw new IllegalArgumentException("Invalid Product Details.");
    }

    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable String id){
        if(id!=null && !id.trim().isEmpty()){
            return loanProductService.deleteProduct(id);
        }
        throw new IllegalArgumentException("Invalid Id.");
    }
}