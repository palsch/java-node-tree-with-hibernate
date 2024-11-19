package com.example.demo.questions.insurance;

import com.example.demo.base.AnswerNodeEntityWithDocs;
import com.example.demo.base.NodeEntity;
import com.example.demo.base.documents.DocumentType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@MappedSuperclass
public abstract class InsuranceBenefitAnswer extends AnswerNodeEntityWithDocs {

    private LocalDate requestDate;
    private LocalDate receiveDate;

    @Enumerated(EnumType.STRING)
    private InsurancePaymentType paymentType;

    private BigDecimal payment;

    protected abstract DocumentType getInsuranceApplicationDocumentType();

    protected abstract DocumentType getInsuranceDecisionDocumentType();

    protected abstract DocumentType getDailyAllowanceStatementDocumentType();

    @Transient
    @Override
    protected List<DocumentType> getRequiredDocumentTypes() {
        if (paymentType != null) {
            return receiveDate != null ? List.of(getInsuranceApplicationDocumentType()) :
                    requestDate != null ? List.of(getInsuranceDecisionDocumentType()) :
                            List.of();
        }
        return List.of();
    }

    @Transient
    @Override
    protected List<DocumentType> getOptionalDocumentTypes() {
        if (paymentType != null && paymentType.equals(InsurancePaymentType.DAILY)) {
            return payment != null
                    ? List.of(getDailyAllowanceStatementDocumentType())
                    : List.of();
        }
        return List.of();
    }

    @Override
    protected int getRequiredDocumentMaxCount() {
        return 5;
    }

    @Override
    protected int getOptionalDocumentMaxCount() {
        return 5;
    }

    @Override
    protected String updateAnswer(NodeEntity<?> nodeEntity) {
        assertAnswerType(nodeEntity);
        InsuranceBenefitAnswer answer = (InsuranceBenefitAnswer) nodeEntity;

        // update the answer
        setRequestDate(answer.getRequestDate());
        setReceiveDate(answer.getReceiveDate());
        setPaymentType(answer.getPaymentType());
        setPayment(answer.getPayment());

        return "answer updated";
    }

    private void assertAnswerType(NodeEntity<?> nodeEntity) {
        if (!(nodeEntity instanceof InsuranceBenefitAnswer)) {
            throw new IllegalArgumentException("NodeEntity must be of type InsuranceBenefitAnswer");
        }
    }

}
