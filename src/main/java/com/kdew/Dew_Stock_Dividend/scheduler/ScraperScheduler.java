package com.kdew.Dew_Stock_Dividend.scheduler;

import com.kdew.Dew_Stock_Dividend.entity.CompanyEntity;
import com.kdew.Dew_Stock_Dividend.entity.DividendEntity;
import com.kdew.Dew_Stock_Dividend.model.Company;
import com.kdew.Dew_Stock_Dividend.model.ScrapedResult;
import com.kdew.Dew_Stock_Dividend.repository.CompanyRepository;
import com.kdew.Dew_Stock_Dividend.repository.DividendRepository;
import com.kdew.Dew_Stock_Dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository; // 회사 목록을 조회해야 하기 때문

    private final Scraper yahooFinanceScraper; // 회사마다 배당금 정보를 스크래핑 하기 위해

    private final DividendRepository dividendRepository; // 배당금 정보를 저장해야하기때문

    // 매일 정각에 실행
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        log.info("scraping scheduler is started");

        // 저장되어있는 회사 목록을 조회
        List< CompanyEntity> companies = this.companyRepository.findAll();

        // 회사마다 배당금 정보를 새로 스크래핑
        for (var company : companies) {
            log.info("scraping scheduler is started -> " + company.getName());// 스케줄러가 정상적으로 돌아가고있는지 알기위해

            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(Company.builder()
                                    .name(company.getName())
                                    .ticker(company.getTicker())
                                    .build());

            // DB에 없는 배당금 스크래핑 정보를 저장
            scrapedResult.getDividends().stream()
                    // Dividen model을 DividendEntity mapping 해주기
                    .map(e -> new DividendEntity(company.getId(), e))
                    // 엘리먼트를 하나씩 디비든 레파지토리에 삽입
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if (!exists) {
                            this.dividendRepository.save(e);
                        }
                    });

            // 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
            try {
                Thread.sleep(3000); // 3초
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
// saveall로 하면 오류가 발생해서 유니크때문에 모든 데이터들을 처리하지 않게 됨
// 배당금을 stream으로 하나씩 돌면서 확인한다음에 저장을 해줌