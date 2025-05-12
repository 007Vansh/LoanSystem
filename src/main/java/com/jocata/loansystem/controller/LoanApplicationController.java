package com.jocata.loansystem.controller;

import com.jocata.loansystem.form.LoanApplicationRequestForm;
import com.jocata.loansystem.service.LoanApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loanapplication")
public class LoanApplicationController {
    private final LoanApplicationService loanApplicationService;

    public LoanApplicationController(LoanApplicationService loanApplicationService) {
        this.loanApplicationService = loanApplicationService;
    }

    @PostMapping("/create")
    public Object createLoanApplication(@RequestBody LoanApplicationRequestForm loanApplicationRequestForm){
        if((loanApplicationRequestForm.getPanNo()!=null && !loanApplicationRequestForm.getPanNo().trim().isEmpty())&&
                (loanApplicationRequestForm.getAadharNo()!=null && !loanApplicationRequestForm.getAadharNo().trim().isEmpty())&&
                (loanApplicationRequestForm.getMobileNo()!=null && !loanApplicationRequestForm.getMobileNo().trim().isEmpty())){
            return loanApplicationService.createLoanApplication(loanApplicationRequestForm);
        }
        return "Mandatory details are missing";
    }
}
