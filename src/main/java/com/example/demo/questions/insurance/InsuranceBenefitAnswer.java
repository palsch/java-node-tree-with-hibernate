package com.example.demo.questions.insurance;

import com.example.demo.base.NodeEntity;
import com.example.demo.base.documents.DocumentType;
import com.example.demo.base.documents.DocumentUploadConfiguration;
import com.example.demo.base.documents.DocumentUploadType;
import com.example.demo.base.node.LeafNodeWithDocuments;
import com.example.demo.questions.AleAntragDocumentType;
import com.example.demo.questions.AleAntragDocumentUploadTypes;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@MappedSuperclass
public abstract class InsuranceBenefitAnswer extends LeafNodeWithDocuments {

    private LocalDate requestDate;
    private LocalDate receiveDate;

    @Enumerated(EnumType.STRING)
    private InsurancePaymentType paymentType;

    private BigDecimal payment;

    protected abstract AleAntragDocumentType getInsuranceApplicationDocumentType();

    protected abstract AleAntragDocumentType getInsuranceDecisionDocumentType();

    protected abstract AleAntragDocumentType getDailyAllowanceStatementDocumentType();

    @Transient
    @Override
    protected Map<DocumentUploadType, DocumentUploadConfiguration> getDocumentUploadConfigurations() {
        return Map.of(
                AleAntragDocumentUploadTypes.REQUIRED, new DocumentUploadConfiguration(getRequiredDocumentTypes(), 5, true),
                AleAntragDocumentUploadTypes.OPTIONAL, new DocumentUploadConfiguration(getOptionalDocumentTypes(), 5, false)
        );
    }

    @Transient
    protected List<DocumentType> getRequiredDocumentTypes() {
        if (paymentType != null) {
            return receiveDate != null ? List.of(getInsuranceApplicationDocumentType()) :
                    requestDate != null ? List.of(getInsuranceDecisionDocumentType()) :
                            List.of();
        }
        return List.of();
    }

    @Transient
    protected List<DocumentType> getOptionalDocumentTypes() {
        if (paymentType != null && paymentType.equals(InsurancePaymentType.DAILY)) {
            return payment != null
                    ? List.of(getDailyAllowanceStatementDocumentType())
                    : List.of();
        }
        return List.of();
    }

    @Override
    protected String updateNodeImpl(NodeEntity<?> nodeEntity) {
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
