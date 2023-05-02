package com.kdew.Dew_Stock_Dividend.scraper;

import com.kdew.Dew_Stock_Dividend.model.Company;
import com.kdew.Dew_Stock_Dividend.model.ScrapedResult;

public interface Scraper {

    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);

}