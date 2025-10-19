package com.laraid.vci.card.entity;

import com.laraid.vci.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardNumber;

    private String cardholderName;

    private String validThrough;

    @Enumerated(EnumType.STRING)
    private CurrencyType currency; //USD, IND

    @Enumerated(EnumType.STRING)
    private CardType cardType; // VIRTUAL, PHYSICAL

    private Long walletId;

    @Enumerated(EnumType.STRING)
    private CardStatus status;// ISSUED, BLOCKED, EXPIRED

    private BigDecimal balanceLimit;  // Total allowed
    private BigDecimal currentBalance; // Remaining balance

    private LocalDateTime issuedAt;
    private LocalDateTime activatedAt;
    private LocalDateTime updatedAt;

    private String mobileNumber;
    private String email;
    @Enumerated(EnumType.STRING)
    private GovernmentIdType governmentIdType;  // Enum to indicate ID type
    private String governmentId;                // Could be PAN, SSN, or Passport
    private BigDecimal monthlySalary;
    private LocalDate dob;
    @Enumerated(EnumType.STRING)
    private EmployerType employerType;
    private String companyName;
    private String address;
    private String cvv;

    private String cardToken;

    private boolean kycVerified = false;
}

