package com.express.yto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationResultDTO {

    private boolean valid;

    @Builder.Default
    private List<ValidationErrorDTO> errors = new ArrayList<>();

    public void addError(String customerName, String customerCode, String errorType) {
        errors.add(ValidationErrorDTO.builder()
                .customerName(customerName)
                .customerCode(customerCode)
                .errorType(errorType)
                .build());
    }

    public void markInvalid() {
        this.valid = false;
    }
}
