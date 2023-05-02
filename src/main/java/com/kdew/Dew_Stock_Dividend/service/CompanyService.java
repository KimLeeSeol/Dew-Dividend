package com.kdew.Dew_Stock_Dividend.service;

import com.kdew.Dew_Stock_Dividend.entity.CompanyEntity;
import com.kdew.Dew_Stock_Dividend.entity.DividendEntity;
import com.kdew.Dew_Stock_Dividend.model.Company;
import com.kdew.Dew_Stock_Dividend.model.ScrapedResult;
import com.kdew.Dew_Stock_Dividend.repository.CompanyRepository;
import com.kdew.Dew_Stock_Dividend.repository.DividendRepository;
import com.kdew.Dew_Stock_Dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;


import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Trie trie;

    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker); // 회사의 존재 여부를 boolean 값으로 받음
        if (exists) {
            throw new RuntimeException("already exists ticker -> " + ticker);
        }
        return this.storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return this.companyRepository.findAll(pageable);

    }
    // 저장한 회사의 Company instance를 반환
    private Company storeCompanyAndDividend(String ticker) {
        // ticker를 기준으로 회사를 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            // 회사 정보가 존재하지 않을 때
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }

        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        // 스크래핑 결과 반환
        // 배당금은 companyId랑 같이 저장이 되어야함. 그래서 회사 정보를 먼저 저장하고 결과를 받은 entity에서 id를 가져와서 d
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .collect(Collectors.toList());
        this.dividendRepository.saveAll(dividendEntities);
        return company;
    }


    // 데이터 저장
    public void addAutoCompleteKeyword(String keyword) {
        this.trie.put(keyword, null); // value에 따로 넣을 것이 없이 자동완성 기능만 필요하기때문에 null로 일단 넣어주기
    }
    // 검색
    public List<String> autocomplete(String keyword) {
        return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    // 삭제 기능
    public void deleteAutoCompleteKeyword(String keyword) {
        this.trie.remove(keyword);
    }
}