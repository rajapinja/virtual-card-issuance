package com.laraid.vci.merchant.repo;

import com.laraid.vci.enums.Status;
import com.laraid.vci.merchant.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    boolean existsByEmail(String email);
    Optional<Merchant> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE Merchant m SET m.status = :status WHERE m.id = :merchantId")
    void updateStatus(@Param("merchantId") Long merchantId, @Param("status") Status status);

//    @Modifying
//    @Transactional
//    @Query("UPDATE Merchant m SET m.status = :status WHERE m.id = :merchantId")
//    void updateStatus(Long merchantId, Status status);  // ✅ Works if param names match JPQL


}
