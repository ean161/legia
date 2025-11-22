package vn.ean.legia.util;

import java.util.HashMap;
import java.util.Map;

public class Response {
    
    public Map<String, Object> create(int code, String message, Object data) {
        Map<String, Object> response = new HashMap<>();

        response.put("code", code);
        response.put("message", message);
        response.put("data", data);

        return response;
    }
}
