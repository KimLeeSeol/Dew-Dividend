package com.kdew.Dew_Stock_Dividend.controller;

import com.kdew.Dew_Stock_Dividend.model.Company;
import com.kdew.Dew_Stock_Dividend.model.constants.CacheKey;
import com.kdew.Dew_Stock_Dividend.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class AdminCotroller {

    private final CompanyService companyService;
    private final CacheManager cacheManager;

    /**
     * 회사 및 배당금 정보 추가!
     */
    @PostMapping("/company")
    @PreAuthorize("hasRole('WRITE')") // 쓰기 권한이 있는 유저만 API를 호출할 수 있음
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        String ticker = request.getTicker().trim(); // 사용자가 입력한 ticker
        if (ObjectUtils.isEmpty(ticker)) {
            // 만약 ticker가 빈 값이면 에러 발생
            throw new RuntimeException("ticker is empty");
        }
        Company company = this.companyService.save(ticker);
        this.companyService.addAutoCompleteKeyword(company.getName()); // 회사가 저장될때마다 trie에 회사명이 저장됨
        return ResponseEntity.ok(company);
    }

    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker) {
        String companyName = this.companyService.deleteCompany(ticker);

        this.clearFinanceCache(companyName);
        return ResponseEntity.ok(companyName);
    }

    public void clearFinanceCache(String companyMame) {
        // 캐시에서도 데이터 지워줘야 함
        this.cacheManager.getCache(CacheKey.KEY_FINANCE).evict(companyMame);
    }
}
