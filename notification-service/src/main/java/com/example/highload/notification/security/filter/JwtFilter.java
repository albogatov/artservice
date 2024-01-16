package com.example.highload.notification.security.filter;

import com.example.highload.notification.feign.LoginServiceFeignClient;
import com.example.highload.notification.feign.UserServiceFeignClient;
import com.example.highload.notification.mapper.UserMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

import static org.springframework.util.StringUtils.hasText;

@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {
    private static final String AUTHORIZATION = "Authorization";
    private final UserServiceFeignClient userService;
    private final UserMapper userMapper;
    private final LoginServiceFeignClient loginServiceFeignClient;
    private final CircuitBreaker countCircuitBreaker;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = getTokenFromRequest((HttpServletRequest) servletRequest);
        if (token != null && validateToken(token)) {
            String userLogin = getLoginFromToken(token);
            UserDetails account = userMapper.userDtoToUser(userService.findByLoginElseNull(userLogin, "Bearer " + token).getBody());
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }


    private boolean validateToken(String token) {
        return (boolean) loginServiceFeignClient.validateToken(token).getBody();
    }

    private String getLoginFromToken(String token) {
        return loginServiceFeignClient.getLoginFromToken(token).getBody();
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
