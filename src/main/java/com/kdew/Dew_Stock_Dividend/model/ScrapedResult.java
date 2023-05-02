package com.kdew.Dew_Stock_Dividend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ScrapedResult {

    private Company company; // 회사 정보 저장

    private List<Dividend> dividends; // 배당금 리스트

    public ScrapedResult() {
        this.dividends = new ArrayList<>(); // 리스트에 넣어줌
    }
}