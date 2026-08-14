package com.gp.system.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.system.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    SysUser selectUserByUserName(@Param("userName") String userName);

    List<SysUser> selectUserList(@Param("user") SysUser user);

    int insertUserRole(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    int deleteUserRoleByUserId(@Param("userId") Long userId);

}
