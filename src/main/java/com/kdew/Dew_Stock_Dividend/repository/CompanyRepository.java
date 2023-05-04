package com.kdew.Dew_Stock_Dividend.repository;

import com.kdew.Dew_Stock_Dividend.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    boolean existsByTicker(String ticker);

    Optional<CompanyEntity> findByName(String name); // 회사 이름으로 회사 정보 찾기 위해

    Optional<CompanyEntity> findByTicker(String ticker); // ticker명으로 회사 정보 찾을 때


}