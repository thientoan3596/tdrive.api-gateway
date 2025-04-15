package org.thluon.tdrive.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thientoan3596.dto.ErrorResponseDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.thluon.tdrive.service.JwtService;
import reactor.core.publisher.Mono;

import java.security.SignatureException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request =  exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        request = request.mutate()
                .header("X-User-Subject", "")
                .header("X-User-Id", "")
                .header("X-User-Name", "")
                .header("X-User-Role", "")
                .build();
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try{
                Claims claims = jwtService.extractAllClaims(token);
                request = request.mutate()
                        .header("X-User-Subject", claims.getSubject())
                        .header("X-User-Id", String.valueOf(claims.get("id")))
                        .header("X-User-Name", String.valueOf(claims.get("name")))
                        .header("X-User-Role", String.valueOf(claims.get("role")))
                        .build();
                return chain.filter(exchange.mutate().request(request).build());
            } catch (SignatureException | ExpiredJwtException | MalformedJwtException e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                ErrorResponseDTO responseJson = ErrorResponseDTO.builder()
                        .message("Token Expired")
                        .error("Token Expired")
                        .status(HttpStatus.UNAUTHORIZED)
                        .build();
                try {
                    byte[] bytes = objectMapper.writeValueAsBytes(responseJson);
                    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                    return exchange.getResponse().writeWith(Mono.just(buffer));
                } catch (JsonProcessingException jsonException) {
                    return Mono.error(jsonException);
                }
            }
        }
        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
