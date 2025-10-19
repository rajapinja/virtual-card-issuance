package com.laraid.vci.merchant.controller;

import com.laraid.vci.merchant.dto.MerchantDTO;
import com.laraid.vci.merchant.service.MerchantService;
import com.laraid.vci.auth.util.CurrentUser;
import com.laraid.vci.auth.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private static final Logger log
            = LoggerFactory. getLogger(MerchantController.class);

    private final MerchantService service;

    @PostMapping("/onboard")
    public ResponseEntity<?> onboardMerchant(@CurrentUser JwtUtils.UserInfo user) {
        // user.email(), user.name(), user.roles() available here
        return ResponseEntity.ok("Onboarding initiated by " + user.email());
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody MerchantDTO dto) {
        String result = service.registerMerchant(dto);
        return ResponseEntity.ok(result);
    }



}

