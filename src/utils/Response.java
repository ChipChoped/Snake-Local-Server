package utils;

import org.json.JSONObject;

public record Response(int code, JSONObject content) {}
