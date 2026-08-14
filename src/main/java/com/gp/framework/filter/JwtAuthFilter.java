package com.gp.framework.filter;

import java.io.IOException;

import com.gp.common.constant.CacheConstant;
import com.gp.common.constant.CommonConstant;
import com.gp.common.utils.JwtUtils;
import com.gp.common.utils.RedisUtils;
import com.gp.framework.security.LoginUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (StringUtils.hasText(token)) {
            String uuid = jwtUtils.parseTokenAndGetUuid(token);
            if (uuid != null) {
                String redisKey = CacheConstant.LOGIN_TOKEN_KEY + uuid;
                LoginUserDetails loginUser = redisUtils.getObject(redisKey, LoginUserDetails.class);
                if (loginUser != null) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(CommonConstant.TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(CommonConstant.TOKEN_PREFIX)) {
            return bearerToken.substring(CommonConstant.TOKEN_PREFIX.length());
        }
        return null;
    }

}
