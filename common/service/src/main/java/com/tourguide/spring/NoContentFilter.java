package com.tourguide.spring;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class NoContentFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        chain.doFilter(req, res);
        if ((res.getContentType() == null || res.getContentType().isEmpty()) && res.getStatus() == HttpStatus.OK.value()) {
            res.setStatus(HttpStatus.NO_CONTENT.value());
        }
    }
}
