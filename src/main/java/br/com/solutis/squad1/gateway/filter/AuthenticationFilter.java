package br.com.solutis.squad1.gateway.filter;

import br.com.solutis.squad1.gateway.dto.UserDetailsDto;
import br.com.solutis.squad1.gateway.exception.UnauthorizedException;
import br.com.solutis.squad1.gateway.service.AuthenticationService;
import br.com.solutis.squad1.gateway.service.TokenService;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    private final TokenService tokenService;
    private final AuthenticationService authenticationService;

    public AuthenticationFilter(TokenService tokenService, AuthenticationService authenticationService) {
        super(Config.class);
        this.tokenService = tokenService;
        this.authenticationService = authenticationService;
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {
            if (authenticationService.shouldAuthenticateRequest(exchange.getRequest())) {
                String token = extractAuthToken(exchange.getRequest().getHeaders());
                tokenService.verifyToken(token);

                UserDetailsDto userDetailsDto = tokenService.getUserDetails(token);
                ServerHttpRequest request = getCustomHeaders(exchange, userDetailsDto);

                return chain.filter(exchange.mutate().request(request).build());
            }
            return chain.filter(exchange);
        };
    }

    private String extractAuthToken(HttpHeaders headers) {
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Invalid or missing Bearer token");
        }
        return authHeader.replace("Bearer ", "");
    }

    private ServerHttpRequest getCustomHeaders(ServerWebExchange exchange, UserDetailsDto userDetailsDto) {
        return exchange.getRequest().mutate()
                .header("Authorities", userDetailsDto.authorities())
                .build();
    }

    public static class Config {
    }
}
