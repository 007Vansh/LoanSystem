package com.jocata.loansystem.controller;

import com.jocata.loansystem.form.CreditAssessmentForm;
import com.jocata.loansystem.service.CreditAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/creditassessment")
public class CreditAssessmentController {
    @Autowired
    private CreditAssessmentService creditAssessmentService;

    @GetMapping("/get")
    public String creditAssessment(@RequestBody CreditAssessmentForm creditAssessmentForm){
        if(creditAssessmentForm.getPanNo()!=null && !creditAssessmentForm.getPanNo().trim().isEmpty()
        && creditAssessmentForm.getIncome()!=null && !creditAssessmentForm.getIncome().trim().isEmpty()
        && creditAssessmentForm.getRequestedAmount()!=null && !creditAssessmentForm.getRequestedAmount().trim().isEmpty()
        && creditAssessmentForm.getRequestedTenure()!=null && !creditAssessmentForm.getRequestedTenure().trim().isEmpty()){
            return creditAssessmentService.getCreditAssessment(creditAssessmentForm);
        }else{
            return "Mandatory Fields are missing.";
        }
    }

}
