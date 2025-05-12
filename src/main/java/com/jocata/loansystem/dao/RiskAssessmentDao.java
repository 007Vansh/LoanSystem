package com.jocata.loansystem.dao;

import com.jocata.loansystem.entity.RiskAssessmentDetails;

public interface RiskAssessmentDao {
    RiskAssessmentDetails save(RiskAssessmentDetails riskAssessmentDetails);
    RiskAssessmentDetails get(String applicationId);
}
