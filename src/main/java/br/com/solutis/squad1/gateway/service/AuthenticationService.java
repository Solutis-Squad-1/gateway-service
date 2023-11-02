package br.com.solutis.squad1.gateway.service;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthenticationService {
    private final Map<HttpMethod, List<String>> nonSecuredAPIs;

    public AuthenticationService() {
        Map<HttpMethod, List<String>> nonSecuredAPIs = new HashMap<>();

        nonSecuredAPIs.put(HttpMethod.GET, List.of(
                "/api/v1/catalog/products",
                "/api/v1/catalog/products/**",
                "/api/v1/catalog/products/sellers/**",
                "/api/v1/catalog/products/images/**",
                "/api/v1/catalog/categories",
                "/api/v1/catalog/categories/**"
        ));
        nonSecuredAPIs.put(HttpMethod.POST, List.of(
                "/api/v1/identity/auth/register",
                "/api/v1/identity/auth/login",
                "/api/v1/identity/auth/validate"
        ));
        nonSecuredAPIs.put(HttpMethod.HEAD, List.of("/eureka"));

        this.nonSecuredAPIs = nonSecuredAPIs;
    }

    public boolean shouldAuthenticateRequest(ServerHttpRequest request) {
        HttpMethod requestMethod = request.getMethod();
        String requestPath = request.getURI().getPath();

        if (nonSecuredAPIs.containsKey(requestMethod)) {
            List<String> nonSecuredUrls = nonSecuredAPIs.get(requestMethod);
            for (String pattern : nonSecuredUrls) {
                if (requestPath.matches(pattern.replace("**", ".*"))) {
                    return false; // É uma rota que não precisa de autenticação
                }
            }
        }

        return true; // É uma rota que precisa de autenticação
    }
}
