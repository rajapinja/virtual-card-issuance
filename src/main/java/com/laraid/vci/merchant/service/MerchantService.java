package com.laraid.vci.merchant.service;

import com.laraid.vci.enums.Status;
import com.laraid.vci.merchant.dto.MerchantDTO;
import com.laraid.vci.merchant.entity.Merchant;
import com.laraid.vci.merchant.event.MerchantEventPublisher;
import com.laraid.vci.merchant.repo.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private static final Logger log
            = LoggerFactory. getLogger(MerchantService.class);

    private final MerchantRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final MerchantEventPublisher merchantEventPublisher;

    @Transactional
    public String registerMerchant(MerchantDTO dto) {
        if (repo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Merchant already exists");
        }

        Merchant merchant = Merchant.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .status(Status.PENDING)
                .build();

        merchant = repo.save(merchant); // DB commit first

        // 🔁 Publish event to Kafka (or call async service)
        merchantEventPublisher.publishMerchantCreated(merchant, dto);

        return "Merchant created with PENDING status";
    }

}

