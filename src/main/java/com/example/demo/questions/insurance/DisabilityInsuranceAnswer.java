package com.example.demo.questions.insurance;

import com.example.demo.questions.AleAntragDocumentType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor

@Entity
@DiscriminatorValue("disability_insurance_answer")
public class DisabilityInsuranceAnswer extends InsuranceBenefitAnswer {

    @Transient
    @Override
    protected AleAntragDocumentType getInsuranceApplicationDocumentType() {
        return AleAntragDocumentType.DISABILITY_INSURANCE_APPLICATION;
    }

    @Transient
    @Override
    protected AleAntragDocumentType getInsuranceDecisionDocumentType() {
        return AleAntragDocumentType.DISABILITY_INSURANCE_DECISION;
    }

    @Transient
    @Override
    protected AleAntragDocumentType getDailyAllowanceStatementDocumentType() {
        return AleAntragDocumentType.DISABILITY_INSURANCE_DAILY_ALLOWANCE_STATEMENT;
    }
}
