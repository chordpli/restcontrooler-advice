package com.restcontroller.advice.config;

import com.restcontroller.advice.domain.User;
import com.restcontroller.advice.service.UserService;
import com.restcontroller.advice.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.restcontroller.advice.util.JwtTokenUtil.isExpired;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    // service는 변경되거나 없어도 가능. 필요에 따라 추가
    private final UserService userService;
    // 시크릿 키를 가지고 토큰을 열기 때문에 시크릿 키는 필수.
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 권한을 부여하거나 부여하지 않는다.
        // 개찰구 역할, 설정 이전에는 모두 닫혀 있다.
        // SecurityConfig의 SecurityConfig의 .antMatchers(HttpMethod.POST,"/api/v1/**").authenticated()가 문을 만들어준다.

        // 언제 막아야 하는가?
        // 1. 허가증이 없을 때
        // 2. 적절하지 않은 허가증을 가지고 올 때
        // 3. 기간이 지난 허가증을 가지고 올 때
        final String AUTHORIZATION_HEADER = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authorization header = {}", AUTHORIZATION_HEADER);

        // AUTHORIZATION_HEADER 없을 때 null 처리
        if (AUTHORIZATION_HEADER == null || !AUTHORIZATION_HEADER.startsWith("bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        // TOKEN 분리
        String token = null;
        try {
            token = AUTHORIZATION_HEADER.split(" ")[1];
        } catch (Exception e) {
            log.error("token 추출에 실패했습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        if (isExpired(token, secretKey)) {
            filterChain.doFilter(request, response);
            return;
        }

        // token에서 claim에서 username 꺼내기
        String userName = JwtTokenUtil.getUserName(token, secretKey);
        log.info("userName ={}", userName);

        // userDetail 가져오기
        User user = userService.getUserByUserName(userName);
        log.info("userDeatil = {}", user);

        // 문 열어주기, Role 바인딩
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("", null, List.of(new SimpleGrantedAuthority(user.getRole().name())));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
