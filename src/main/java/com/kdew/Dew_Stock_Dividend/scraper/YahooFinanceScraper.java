package com.kdew.Dew_Stock_Dividend.scraper;

import com.kdew.Dew_Stock_Dividend.model.Company;
import com.kdew.Dew_Stock_Dividend.model.Dividend;
import com.kdew.Dew_Stock_Dividend.model.ScrapedResult;
import com.kdew.Dew_Stock_Dividend.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper {

    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo"; // 배당금 경로

    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s"; // 회사명 경로

    private static final long START_TIME = 86400; // 60* 60 * 24, 시작 날짜

    @Override
    public ScrapedResult scrap(Company company) {
        var scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);

        try {
            long now = System.currentTimeMillis() / 1000; // 마지막 날짜 현재까지의 시간을 초로 가져옴
            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);
            Connection connection = Jsoup.connect(url); // http connection
            Document document = connection.get();

            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableEle = parsingDivs.get(0); // table 전체

            Element tbody = tableEle.children().get(1); // 배당금 결과 가져오기

            List<Dividend> dividends = new ArrayList<>(); // 배당금 리스트에 담기
            for (Element e : tbody.children()) {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]); // 문자열을 숫자로 바꾸도록
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    //enum에 정의되지 않는 다른 날짜가 온 것이기 때문에!
                    throw new RuntimeException("Unexpected Month enum value -> " + splits[0]);
                }

                dividends.add(new Dividend(LocalDateTime.of(year,month,day,0,0), dividend));


            }
            scrapResult.setDividends(dividends);

        } catch (IOException e) {
            // 스크래핑이 정상적으로 완료되지 못함
            e.printStackTrace();
        }
        return scrapResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {
        // ticker코드를 받으면 그 ticker에 해당하는 회사의 메타 정보를 스크래핑해서 결과로 반환
        String url = String.format(SUMMARY_URL,ticker,ticker);

        try {
            Document document = Jsoup.connect(url).get(); // document가져오기
            Element titleEle = document.getElementsByTag("h1").get(0); // document에서 회사명 가져오기, 타이틀
            String title = titleEle.text().split(" - ")[1].trim(); // 회사명 깔끔하게 가져오기 위해 처리, 회사명이 "-"로 구분되어있는게 있기때문!

            return new Company(ticker,title);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}