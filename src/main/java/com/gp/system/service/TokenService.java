package com.gp.system.service;

import java.util.concurrent.TimeUnit;

import cn.hutool.core.util.IdUtil;
import com.gp.common.constant.CacheConstant;
import com.gp.common.utils.JwtUtils;
import com.gp.common.utils.RedisUtils;
import com.gp.framework.security.LoginUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisUtils redisUtils;

    public String createToken(LoginUserDetails loginUser) {
        String uuid = IdUtil.fastSimpleUUID();
        long expire = jwtUtils.getExpireTime();
        redisUtils.setObject(CacheConstant.LOGIN_TOKEN_KEY + uuid, loginUser, expire, TimeUnit.MINUTES);
        return jwtUtils.createToken(uuid);
    }

    public void removeToken(String token) {
        String uuid = jwtUtils.parseTokenAndGetUuid(token);
        if (uuid != null) {
            redisUtils.delete(CacheConstant.LOGIN_TOKEN_KEY + uuid);
        }
    }

}
