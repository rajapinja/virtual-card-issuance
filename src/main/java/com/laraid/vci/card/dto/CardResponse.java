package com.laraid.vci.card.dto;

import com.laraid.vci.enums.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardResponse
{
    private Long id;
    private String cardNumber;
    private String cardholderName;
    private String validThrough;
    private String currency; //USD, IND
    private String cardType; // VIRTUAL, PHYSICAL
    private Long walletId;
    private String status;// ISSUED, BLOCKED, EXPIRED
    private BigDecimal balanceLimit;  // Total allowed
    private BigDecimal currentBalance; // Remaining balance
    private LocalDateTime issuedAt;
    private LocalDateTime activatedAt;
    private LocalDateTime updatedAt;
    private String mobileNumber;
    private String email;
    private String governmentIdType;            // Enum to indicate ID type
    private String governmentId;                // Could be PAN, SSN, or Passport
    private BigDecimal monthlySalary;
    private LocalDate dob;
    private String employerType;
    private String companyName;
    private String address;
    private String last4;
    private String cvv;
    private boolean kycVerified;

}
