package com.gp.system.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.gp.common.exception.BusinessException;
import com.gp.framework.security.LoginUserDetails;
import com.gp.system.domain.SysMenu;
import com.gp.system.domain.SysRole;
import com.gp.system.domain.SysUser;
import com.gp.system.mapper.SysMenuMapper;
import com.gp.system.mapper.SysRoleMapper;
import com.gp.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SysLoginService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysMenuMapper menuMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    public String login(String username, String password) {
        SysUser user = userMapper.selectUserByUserName(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if ("1".equals(user.getStatus())) {
            throw new BusinessException("账号已停用，请联系管理员");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        LoginUserDetails loginUser = new LoginUserDetails();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUserName());
        loginUser.setPassword(user.getPassword());
        loginUser.setDeptId(user.getDeptId());

        Set<String> perms = menuMapper.selectPermsByUserId(user.getId());
        loginUser.setPermissions(perms);

        List<SysRole> roles = roleMapper.selectRolesByUserId(user.getId());
        Set<String> roleKeys = roles.stream()
                .map(SysRole::getRoleKey)
                .collect(Collectors.toSet());
        loginUser.setRoles(roleKeys);

        return tokenService.createToken(loginUser);
    }

}
