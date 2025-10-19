package com.laraid.vci.merchant.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantCreatedEvent {
    private Long merchantId;
    private String name;
    private String email;
    private String password;
    private String status;
    private Instant createdAt;
    private String firstName;
    private String lastName;

}
