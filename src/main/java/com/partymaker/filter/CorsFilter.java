//package com.partymaker.filter;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.List;
//
//@Component
//@Slf4j
//public class CorsFilter implements Filter {
//
//    private final List<String> allowedOrigin = List.of("localhost:5500", "127.0.0.1:5500", "http://localhost:5500", "http://127.0.0.1:5500");
//
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//        log.info("CorsFilter.doFilter START");
//        HttpServletRequest request = (HttpServletRequest) req;
//        HttpServletResponse response = (HttpServletResponse) res;
//
//        String origin = request.getHeader("Origin");
//        log.info("CorsFilter Origin: " + origin);
//
//        if (!allowedOrigin.contains(origin)) {
//            chain.doFilter(req, res); // allowedOrigin 목록에 없는 경우, 필터 체인으로 요청을 계속 진행
//            return;
//        }
//
//        response.setHeader("Access-Control-Allow-Origin", origin);
//        response.setHeader("Access-Control-Allow-Credentials", "true");
//        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
//        response.setHeader("Access-Control-Max-Age", "3600");
//        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization, provider");
//        response.setHeader("Access-Control-Expose-Headers", "Authorization, provider");
//        log.info("CorsFilter.doFilter End");
//
//        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//            log.info("CorsFilter.doFilter OPTION called");
//            response.setStatus(HttpServletResponse.SC_OK);
//        } else {
//            chain.doFilter(req, res);
//        }
//    }
//}
