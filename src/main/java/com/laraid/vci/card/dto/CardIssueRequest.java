package com.laraid.vci.card.dto;

import com.laraid.vci.enums.GovernmentIdType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardIssueRequest {

    @NotBlank(message = "Cardholder name is required")
    private String cardholderName;

    @NotNull(message = "Wallet ID is required")
    private Long walletId;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Card type is required") // e.g., "VIRTUAL", "PHYSICAL"
    private String cardType;

    private String merchantId; // Optional if needed for linkage

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$")
    private String mobileNumber;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Government Id is required")
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]$")
    private String governmentId;         // Could be PAN, SSN, or Passport

    @NotBlank(message = "GovernmentId Type is required")
    private GovernmentIdType governmentIdType; // Enum to indicate ID type

    private String employerType;

    private String companyName;

    private String address;

    private String dob;

    @NotBlank(message = "Monthly Salary is required")
    @DecimalMin("1000.00")
    private BigDecimal monthlySalary;
}

