package com.kdew.Dew_Stock_Dividend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ScrapedResult {

    private Company company;

    private List<Dividend> dividendEntities;

    public ScrapedResult() {
        this.dividendEntities = new ArrayList<>(); // 리스트에 넣어줌
    }
}