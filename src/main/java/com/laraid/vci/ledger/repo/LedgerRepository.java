package com.laraid.vci.ledger.repo;

import com.laraid.vci.ledger.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LedgerRepository extends JpaRepository<LedgerEntry, Long> {

}
