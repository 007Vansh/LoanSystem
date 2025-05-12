package com.jocata.loansystem.controller;

import com.jocata.loansystem.form.LoanRepaymentResponseForm;
import com.jocata.loansystem.service.LoanRepaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repayment")
public class LoanRepaymentController {
    private final LoanRepaymentService repaymentService;

    public LoanRepaymentController(LoanRepaymentService repaymentService) {
        this.repaymentService = repaymentService;
    }

    @GetMapping("getSchedule/{applicationId}")
    public List<LoanRepaymentResponseForm> getSchedule(@PathVariable String applicationId){
        if(applicationId!=null && !applicationId.trim().isEmpty()){
            return repaymentService.getSchedule(applicationId);
        }
        throw new IllegalArgumentException("Invalid application Id.");
    }
    @PostMapping("/{loanId}")
    public String rePayment(@PathVariable String loanId){
        if(loanId!=null && !loanId.trim().isEmpty()){
            return repaymentService.rePayment(loanId);
        }
        throw new IllegalArgumentException("Invalid Loan Id.");
    }
}
