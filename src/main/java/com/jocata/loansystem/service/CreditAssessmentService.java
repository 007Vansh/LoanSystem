package com.jocata.loansystem.service;

import com.jocata.loansystem.form.CibilRequestForm;
import com.jocata.loansystem.form.CibilResponseForm;
import com.jocata.loansystem.form.CreditAssessmentForm;

public interface CreditAssessmentService {
    public String getCreditAssessment(CreditAssessmentForm creditAssessmentForm);
    public CibilResponseForm saveCibil(CibilRequestForm cibilRequestForm);
}
