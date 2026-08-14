package com.gp.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.exception.BusinessException;
import com.gp.system.domain.SysRole;
import com.gp.system.mapper.SysRoleMapper;
import org.springframework.stereotype.Service;

@Service
public class SysRoleService extends ServiceImpl<SysRoleMapper, SysRole> {

    public List<SysRole> listAll() {
        return list();
    }

    public boolean checkRoleKeyUnique(SysRole role) {
        Long roleId = role.getId() == null ? -1L : role.getId();
        SysRole info = this.lambdaQuery()
                .eq(SysRole::getRoleKey, role.getRoleKey())
                .one();
        return info == null || info.getId().equals(roleId);
    }

    public boolean hasChildByRoleId(Long roleId) {
        return this.baseMapper.selectCount(
                this.lambdaQuery().eq(SysRole::getId, roleId).getWrapper()
        ) > 0;
    }

    public void checkRoleAllowed(Long roleId) {
        SysRole role = getById(roleId);
        if (role != null && "admin".equals(role.getRoleKey())) {
            throw new BusinessException("不允许删除超级管理员角色");
        }
    }

}
