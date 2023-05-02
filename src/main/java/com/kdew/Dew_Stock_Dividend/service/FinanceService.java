package com.kdew.Dew_Stock_Dividend.service;

import com.kdew.Dew_Stock_Dividend.entity.CompanyEntity;
import com.kdew.Dew_Stock_Dividend.entity.DividendEntity;
import com.kdew.Dew_Stock_Dividend.model.Company;
import com.kdew.Dew_Stock_Dividend.model.Dividend;
import com.kdew.Dew_Stock_Dividend.model.ScrapedResult;
import com.kdew.Dew_Stock_Dividend.model.constants.CacheKey;
import com.kdew.Dew_Stock_Dividend.repository.CompanyRepository;
import com.kdew.Dew_Stock_Dividend.repository.DividendRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;


    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        log.info("search company -> " + companyName);

        // 회사명 기준으로 회사명 조회
        CompanyEntity companyEntity = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다."));

        // 조회된 회사 ID로 배당금 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(companyEntity.getId());

        // 결과 반환
        // CompanyEntity -> Company model로 바꿔주기
        List<Dividend> dividends = dividendEntities.stream()
                        .map(e -> new Dividend(e.getDate(), e.getDividend()))
                        .collect(Collectors.toList());


        return new ScrapedResult(new Company(companyEntity.getTicker(), companyEntity.getName()), dividends);

    }
}
