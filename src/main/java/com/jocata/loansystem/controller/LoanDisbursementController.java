package com.jocata.loansystem.controller;

import com.jocata.loansystem.form.LoanDisbursementForm;
import com.jocata.loansystem.service.LoanDisbursementService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loandisbursement")
public class LoanDisbursementController {
    private final LoanDisbursementService loanDisbursementService;

    public LoanDisbursementController(LoanDisbursementService loanDisbursementService) {
        this.loanDisbursementService = loanDisbursementService;
    }
    @PostMapping("/{applicationId}")
    public LoanDisbursementForm create(@PathVariable String applicationId){
        if(applicationId!=null && !applicationId.isEmpty()){
            return loanDisbursementService.createDisbursement(applicationId);
        }
        throw new IllegalArgumentException("Invalid application id.");
    }

}
