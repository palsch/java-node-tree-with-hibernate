package com.example.demo.questions.insurance;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum InsurancePaymentType {
    MONTHLY,
    DAILY
}
