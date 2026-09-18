package com.lifted.incident;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.HashMap;
import java.util.Map;

public class LambdaHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    @Override
    public Map<String, Object> handleRequest(
            Map<String, Object> input,
            Context context) {

        Map<String, Object> response = new HashMap<>();

        response.put("statusCode", 200);
        response.put("message", "Cloud Incident API is running!");
        response.put("service", "cloud-incident-api");
        response.put("runtime", "Java 21");
        response.put("status", "healthy");

        return response;
    }
}