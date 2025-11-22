package vn.ean.legia.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Api {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    private static HttpRequest.Builder buildRequest(String url, Map<String, String> headers) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url));
        if (headers != null) {
            headers.forEach(builder::header);
        }

        return builder;
    }

    public static Map<String, Object> get(String url, Map<String, String> headers) {
        try {
            HttpRequest request = buildRequest(url, headers).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return mapper.readValue(response.body(), Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static Map<String, Object> post(String url, Map<String, String> headers, Map<String, Object> body) {
        try {
            String requestBody = mapper.writeValueAsString(body);
            HttpRequest request = buildRequest(url, headers).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            return mapper.readValue(response.body(), Map.class);
        } catch (Exception e) {
            return null;
        }
    }
}
