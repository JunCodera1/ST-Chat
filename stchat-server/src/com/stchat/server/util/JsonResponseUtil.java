package com.stchat.server.util;

import org.json.JSONObject;

public class JsonResponseUtil {
    public static JSONObject success(String message) {
        return new JSONObject()
                .put("status", "success")
                .put("message", message);
    }

    public static JSONObject error(String message) {
        return new JSONObject()
                .put("status", "error")
                .put("message", message);
    }
}
