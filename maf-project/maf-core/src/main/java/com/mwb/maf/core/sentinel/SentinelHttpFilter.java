package com.mwb.maf.core.sentinel;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import java.io.IOException;


@Slf4j
public class SentinelHttpFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

}