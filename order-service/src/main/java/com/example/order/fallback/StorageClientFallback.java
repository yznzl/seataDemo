package com.example.order.fallback;

import com.example.order.feign.StorageClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StorageClientFallback implements StorageClient {
    @Override
    public Map<String, Object> deduct(int count) {
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("deducted", 0);
        hashMap.put("stock", 0);
        return hashMap;
    }

    @Override
    public Map<String, Object> state() {
        return null;
    }
}
