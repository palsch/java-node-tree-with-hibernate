package com.example.demo.questions.insurance;

import com.example.demo.base.Node;
import com.example.demo.base.documents.DocumentType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@Builder
@AllArgsConstructor

@Entity
@DiscriminatorValue("disability_insurance_answer")
public class DisabilityInsuranceAnswer extends InsuranceBenefitAnswer {

    @Override
    public Optional<Node<?>> createNewChildNode() {
        return Optional.empty();
    }

    @Transient
    @Override
    protected DocumentType getInsuranceApplicationDocumentType() {
        return DocumentType.DISABILITY_INSURANCE_APPLICATION;
    }

    @Transient
    @Override
    protected DocumentType getInsuranceDecisionDocumentType() {
        return DocumentType.DISABILITY_INSURANCE_DECISION;
    }

    @Transient
    @Override
    protected DocumentType getDailyAllowanceStatementDocumentType() {
        return DocumentType.DISABILITY_INSURANCE_DAILY_ALLOWANCE_STATEMENT;
    }
}
