//package com.partymaker.filter;
//
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class AppConfig {
//
//    @Bean
//    public FilterRegistrationBean<CorsFilter> corsFilterRegistration(CorsFilter filter) {
//        FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setOrder(1);
//        registrationBean.setFilter(filter);
//        registrationBean.addUrlPatterns("/api/v1/*"); // 적절한 URL 패턴으로 변경해야 함
//        return registrationBean;
//    }
//
//}