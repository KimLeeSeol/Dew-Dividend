package com.kdew.Dew_Stock_Dividend;

import com.kdew.Dew_Stock_Dividend.model.Company;
import com.kdew.Dew_Stock_Dividend.scraper.YahooFinanceScraper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DewStockDividendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DewStockDividendApplication.class, args);

	}

}
