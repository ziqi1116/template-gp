package com.gp.system.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.gp.common.constant.CommonConstant;
import com.gp.common.core.result.Result;
import com.gp.common.exception.BusinessException;
import com.gp.common.utils.RedisUtils;
import com.gp.common.utils.SecurityUtils;
import com.gp.framework.domain.SysLogininfor;
import com.gp.framework.security.LoginUserDetails;
import com.gp.framework.service.SysLogininforService;
import com.gp.system.domain.RouterVo;
import com.gp.system.domain.SysUser;
import com.gp.system.mapper.SysUserMapper;
import com.gp.system.service.SysLoginService;
import com.gp.system.service.SysMenuService;
import com.gp.system.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Tag(name = "登录认证")
@RestController
public class SysLoginController {

    @Autowired
    private SysLoginService loginService;

    @Autowired
    private SysMenuService menuService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysLogininforService logininforService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisUtils redisUtils;

    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String nickName = body.get("nickName");
        String code = body.get("code");
        String uuid = body.get("uuid");

        if (username == null || username.length() < 3 || username.length() > 20) {
            return Result.error("用户名长度需在3-20位之间");
        }
        if (password == null || password.length() < 6 || password.length() > 20) {
            return Result.error("密码长度需在6-20位之间");
        }

        if (code != null && uuid != null && !code.isEmpty() && !uuid.isEmpty()) {
            String key = "captcha:" + uuid;
            Object cached = redisUtils.get(key);
            if (cached == null) {
                return Result.error("验证码已过期");
            }
            try {
                int expected = Integer.parseInt(String.valueOf(cached));
                int actual = Integer.parseInt(code.trim());
                if (expected != actual) {
                    return Result.error("验证码错误");
                }
            } catch (NumberFormatException e) {
                return Result.error("验证码格式错误");
            }
            redisUtils.delete(key);
        }

        SysUser exist = userMapper.selectUserByUserName(username);
        if (exist != null) {
            return Result.error("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUserName(username);
        user.setNickName(nickName != null && !nickName.isEmpty() ? nickName : username);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus("0");
        user.setSex("0");
        user.setDeptId(100L);
        userMapper.insert(user);

        Long userId = user.getId();
        try {
            userMapper.insertUserRole(userId, java.util.Collections.singletonList(2L));
        } catch (Exception ignored) {
        }

        return Result.success();
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String token = loginService.login(username, password);

        SysLogininfor logininfor = new SysLogininfor();
        logininfor.setUserName(username);
        logininfor.setStatus("0");
        logininfor.setMsg("登录成功");
        logininfor.setLoginTime(new Date());
        logininforService.save(logininfor);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        return Result.success(result);
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String token = request.getHeader(CommonConstant.TOKEN_HEADER);
        if (token != null && token.startsWith(CommonConstant.TOKEN_PREFIX)) {
            token = token.substring(CommonConstant.TOKEN_PREFIX.length());
        }
        tokenService.removeToken(token);
        return Result.success();
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/getInfo")
    public Result<Map<String, Object>> getInfo() {
        LoginUserDetails loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        SysUser user = userMapper.selectById(loginUser.getUserId());
        if (user != null) {
            user.setPassword(null);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("roles", loginUser.getRoles());
        result.put("permissions", loginUser.getPermissions());
        return Result.success(result);
    }

    @Operation(summary = "获取路由菜单")
    @GetMapping("/getRouters")
    public Result<List<RouterVo>> getRouters() {
        LoginUserDetails loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        boolean isAdmin = loginUser.getRoles() != null && loginUser.getRoles().contains(CommonConstant.SUPER_ADMIN);
        List<RouterVo> routers = menuService.getRouters(loginUser.getUserId(), isAdmin);
        return Result.success(routers);
    }

}
