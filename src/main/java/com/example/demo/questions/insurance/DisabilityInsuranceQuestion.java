package com.example.demo.questions.insurance;

import com.example.demo.base.YesNoQuestion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

/**
 * Question 5c - Disability Insurance (IV - Invalidenversicherung)
 * (yes/no question with multiple answers and documents)
 */
@Getter
@Setter

@Entity
@DiscriminatorValue("disability_insurance")
public class DisabilityInsuranceQuestion extends YesNoQuestion<DisabilityInsuranceAnswer> {
    @Override
    public Optional<DisabilityInsuranceAnswer> createNewChildNode() {
        return Optional.of(new DisabilityInsuranceAnswer());
    }
}

