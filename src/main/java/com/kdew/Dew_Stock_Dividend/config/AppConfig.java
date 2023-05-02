package com.kdew.Dew_Stock_Dividend.config;


import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    //trie Bean을 생성해주기 위해서
    // trie는 하나만 서비스내에서 하나만 유지되어야 하고 일관성 때문!
    // trie Bean이 초기화되면서 CompanyService 주입되면서 Trie로 사용될 수 있음!!
    @Bean
    public Trie<String, String> trie() {
        return new PatriciaTrie<>();
    }
}


