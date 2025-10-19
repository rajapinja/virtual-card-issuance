package com.laraid.vci.wallet.repo;

import com.laraid.vci.wallet.entity.WalletLedger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletLedgerRepository extends JpaRepository<WalletLedger, Long> {
    List<WalletLedger> findByUserId(String userId);
}
