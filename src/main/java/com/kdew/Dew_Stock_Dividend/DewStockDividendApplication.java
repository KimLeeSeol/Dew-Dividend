package com.kdew.Dew_Stock_Dividend;

import com.kdew.Dew_Stock_Dividend.model.Company;
import com.kdew.Dew_Stock_Dividend.scraper.YahooFinanceScraper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class DewStockDividendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DewStockDividendApplication.class, args);

	}

}
