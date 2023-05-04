package com.kdew.Dew_Stock_Dividend.controller;

import com.kdew.Dew_Stock_Dividend.entity.CompanyEntity;
import com.kdew.Dew_Stock_Dividend.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/company") // 공통 경로는 빼주기
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/autocomplete")
    public ResponseEntity<?> autoComplete(@RequestParam String keyword) {
        var result = this.companyService.autocomplete(keyword);

        return ResponseEntity.ok(result);
    }

    @GetMapping
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> searchCompany(final Pageable pageable) {

        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);

        return ResponseEntity.ok(companies);
    }

}
