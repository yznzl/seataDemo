package com.example.storage.config;

import io.seata.core.context.RootContext;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** 将订单服务传入的 Seata XID 绑定到当前请求。 */
@Component
public class SeataXidFilter extends OncePerRequestFilter {

    /**
     * 在请求处理期间绑定 Seata XID。
     *
     * @param request HTTP 请求
     * @param response HTTP 响应
     * @param filterChain Servlet 过滤器链
     * @throws ServletException 请求处理失败
     * @throws IOException 请求或响应 I/O 失败
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String xid = request.getHeader(RootContext.KEY_XID);
        boolean bound = xid != null && !xid.isEmpty();
        if (bound) {
            RootContext.bind(xid);
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            if (bound) {
                RootContext.unbind();
            }
        }
    }
}
