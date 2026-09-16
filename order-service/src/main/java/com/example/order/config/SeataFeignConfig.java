package com.example.order.config;

import feign.RequestInterceptor;
import io.seata.core.context.RootContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Seata Feign 配置。 */
@Configuration
public class SeataFeignConfig {
    /**
     * 向下游请求传播当前 Seata XID。
     *
     * @return Feign 请求拦截器
     */
    @Bean
    public RequestInterceptor seataXidInterceptor() {
        return template -> {
            String xid = RootContext.getXID();
            if (xid != null && !xid.isEmpty()) {
                template.header(RootContext.KEY_XID, xid);
            }
        };
    }
}
