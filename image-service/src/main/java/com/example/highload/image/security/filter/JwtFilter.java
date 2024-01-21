package com.example.highload.image.security.filter;

import com.example.highload.image.feign.LoginServiceFeignClient;
import com.example.highload.image.feign.UserServiceFeignClient;
import com.example.highload.image.mapper.UserMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {
    private static final String AUTHORIZATION = "Authorization";
    private final UserServiceFeignClient userService;
    private final UserMapper userMapper;
    private final LoginServiceFeignClient loginServiceFeignClient;


    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = getTokenFromRequest((HttpServletRequest) servletRequest);
        if (token != null && validateToken(token)) {
            String userLogin = getLoginFromToken(token);
            UserDetails account = userMapper.userDtoToUser(userService.findByLoginElseNull(userLogin, "Bearer " + token).getBody());
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(account, null, getRoleFromJwtToken(token).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private Claims getClaimsFromToken(String token){
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    public List<String> getRoleFromJwtToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return List.of(claims.get("roles", String.class));
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
