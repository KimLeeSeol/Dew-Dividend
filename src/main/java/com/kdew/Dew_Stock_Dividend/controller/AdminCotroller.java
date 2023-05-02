package com.kdew.Dew_Stock_Dividend.controller;

import com.kdew.Dew_Stock_Dividend.model.Company;
import com.kdew.Dew_Stock_Dividend.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AdminCotroller {

    private final CompanyService companyService;
    @PostMapping("/company")
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        String ticker = request.getTicker().trim(); // 사용자가 입력한 ticker
        if (ObjectUtils.isEmpty(ticker)) {
            // 만약 ticker가 빈 값이면 에러 발생
            throw new RuntimeException("ticker is empty");
        }
        Company company = this.companyService.save(ticker);

        return ResponseEntity.ok(company);
    }

    @DeleteMapping("/company")
    public ResponseEntity<?> deleteCompany() {
        return null;
    }
}
