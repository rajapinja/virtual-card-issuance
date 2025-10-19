package com.laraid.vci.cardstock.repo;

import com.laraid.vci.cardstock.entity.CardStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardStockRepository extends JpaRepository<CardStock, Long> {

    Optional<CardStock> findFirstByAssignedFalseOrderByIdAsc();
}
