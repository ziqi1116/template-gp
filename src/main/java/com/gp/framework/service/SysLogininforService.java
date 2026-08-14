package com.gp.framework.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.utils.RedisUtils;
import com.gp.framework.domain.SysLogininfor;
import com.gp.framework.mapper.SysLogininforMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysLogininforService extends ServiceImpl<SysLogininforMapper, SysLogininfor> {

    @Autowired
    private RedisUtils redisUtils;

    public List<SysLogininfor> listAll() {
        return list();
    }

    public void unlockUser(Long userId) {
        if (userId == null) {
            return;
        }
        // 解除登录失败锁定（预留扩展：从 Redis 中移除对应用户的锁定次数）
        String lockKey = "login:lock:" + userId;
        if (redisUtils.hasKey(lockKey)) {
            redisUtils.delete(lockKey);
        }
    }

}