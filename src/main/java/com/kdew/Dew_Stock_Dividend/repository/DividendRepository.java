package com.kdew.Dew_Stock_Dividend.repository;

import com.kdew.Dew_Stock_Dividend.entity.DividendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DividendRepository extends JpaRepository<DividendEntity, Long> {
}