package com.maven.spring.apigateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.spring.apigateway.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain
    ) {

        String path = exchange
                .getRequest()
                .getURI()
                .getPath();

        /*
         * These endpoints do not require an access token.
         *
         * Refresh is public because the access token may
         * already be expired.
         */
        if (isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }

        String authorizationHeader = exchange
                .getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        /*
         * Protected requests must contain:
         *
         * Authorization: Bearer <access-token>
         */
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            return unauthorizedResponse(
                    exchange,
                    "Authorization header is missing or invalid"
            );
        }

        String token = authorizationHeader.substring(7);

        if (token.isBlank()) {

            return unauthorizedResponse(
                    exchange,
                    "Access token is missing"
            );
        }

        try {

            boolean validAccessToken =
                    jwtService.isValidAccessToken(token);

            if (!validAccessToken) {

                return unauthorizedResponse(
                        exchange,
                        "Invalid access token"
                );
            }

            String email =
                    jwtService.extractEmail(token);

            String role =
                    jwtService.extractRole(token);

            Long userId =
                    jwtService.extractUserId(token);

            /*
             * Forward user information to downstream services.
             *
             * The User Service can read these headers.
             */
            ServerWebExchange modifiedExchange =
                    exchange.mutate()
                            .request(request ->
                                    request.headers(headers -> {

                                        headers.remove("X-User-Email");
                                        headers.remove("X-User-Role");
                                        headers.remove("X-User-Id");

                                        if (email != null) {
                                            headers.add(
                                                    "X-User-Email",
                                                    email
                                            );
                                        }

                                        if (role != null) {
                                            headers.add(
                                                    "X-User-Role",
                                                    role
                                            );
                                        }

                                        if (userId != null) {
                                            headers.add(
                                                    "X-User-Id",
                                                    userId.toString()
                                            );
                                        }
                                    })
                            )
                            .build();

            return chain.filter(modifiedExchange);

        } catch (IllegalArgumentException exception) {

            return unauthorizedResponse(
                    exchange,
                    exception.getMessage()
            );
        }
    }

    /*
     * Signup, login and refresh do not require
     * Authorization headers.
     */
    private boolean isPublicEndpoint(String path) {

        return path.equals("/api/auth/signup")
                || path.equals("/api/auth/login")
                || path.equals("/api/auth/refresh");
    }

    /*
     * Return a JSON 401 response when JWT validation fails.
     */
    private Mono<Void> unauthorizedResponse(
            ServerWebExchange exchange,
            String message
    ) {

        exchange.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);

        exchange.getResponse()
                .getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("success", false);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        response.put("error", "Unauthorized");
        response.put("message", message);
        response.put(
                "path",
                exchange.getRequest().getURI().getPath()
        );

        byte[] responseBytes;

        try {

            responseBytes =
                    objectMapper.writeValueAsBytes(response);

        } catch (JsonProcessingException exception) {

            String fallbackResponse =
                    "{\"success\":false,"
                            + "\"status\":401,"
                            + "\"error\":\"Unauthorized\"}";

            responseBytes = fallbackResponse.getBytes(
                    StandardCharsets.UTF_8
            );
        }

        DataBuffer dataBuffer =
                exchange.getResponse()
                        .bufferFactory()
                        .wrap(responseBytes);

        return exchange.getResponse()
                .writeWith(Mono.just(dataBuffer));
    }

    /*
     * Run this filter before normal Gateway processing.
     */
    @Override
    public int getOrder() {
        return -1;
    }
}