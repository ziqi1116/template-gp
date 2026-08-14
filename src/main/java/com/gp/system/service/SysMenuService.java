package com.gp.system.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.exception.BusinessException;
import com.gp.system.domain.SysMenu;
import com.gp.system.domain.RouterVo;
import com.gp.system.mapper.SysMenuMapper;
import org.springframework.stereotype.Service;

@Service
public class SysMenuService extends ServiceImpl<SysMenuMapper, SysMenu> {

    public List<SysMenu> listAll() {
        return list();
    }

    public List<SysMenu> listMenuTree() {
        List<SysMenu> all = list();
        return buildMenuTree(all, 0L);
    }

    public List<SysMenu> listMenuTreeByUserId(Long userId) {
        List<SysMenu> menus = this.baseMapper.selectMenusByUserId(userId);
        return buildMenuTree(menus, 0L);
    }

    public List<RouterVo> getRouters(Long userId, boolean isAdmin) {
        List<SysMenu> menus = isAdmin
                ? this.baseMapper.selectMenuTreeAll()
                : this.baseMapper.selectMenusByUserId(userId);
        return buildRouters(menus, 0L);
    }

    private List<SysMenu> buildMenuTree(List<SysMenu> menus, Long parentId) {
        List<SysMenu> tree = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (!parentId.equals(menu.getParentId())) {
                continue;
            }
            tree.add(menu);
        }
        return tree;
    }

    private List<RouterVo> buildRouters(List<SysMenu> menus, Long parentId) {
        List<RouterVo> routers = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (!parentId.equals(menu.getParentId())) {
                continue;
            }

            RouterVo router = new RouterVo();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(capitalize(menu.getPath()));
            router.setPath(menu.getParentId() == 0L ? "/" + menu.getPath() : menu.getPath());
            router.setComponent(getComponent(menu));
            router.setMeta(new RouterVo.MetaVo(menu.getMenuName(), menu.getIcon()));

            List<RouterVo> children = buildRouters(menus, menu.getId());

            if (menu.getParentId() == 0L && "C".equals(menu.getMenuType())) {
                RouterVo child = new RouterVo();
                child.setPath("index");
                child.setComponent(menu.getComponent() != null && !menu.getComponent().isEmpty()
                        ? menu.getComponent() : menu.getPath() + "/index");
                child.setMeta(new RouterVo.MetaVo(menu.getMenuName(), menu.getIcon()));
                List<RouterVo> childList = new ArrayList<>();
                childList.add(child);
                childList.addAll(children);
                router.setChildren(childList);
            } else if (!children.isEmpty()) {
                router.setChildren(children);
            }
            routers.add(router);
        }
        return routers;
    }

    private String getComponent(SysMenu menu) {
        if (menu.getParentId() == 0L && ("M".equals(menu.getMenuType()) || "C".equals(menu.getMenuType()))) {
            return "Layout";
        }
        if (menu.getComponent() == null || menu.getComponent().isEmpty()) {
            return "ParentView";
        }
        return menu.getComponent();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public boolean hasChildByMenuId(Long menuId) {
        return this.lambdaQuery().eq(SysMenu::getParentId, menuId).count() > 0;
    }

    public void checkMenuAllowed(Long menuId) {
        SysMenu menu = getById(menuId);
        if (menu != null && "M".equals(menu.getMenuType())) {
            long count = this.lambdaQuery().eq(SysMenu::getParentId, menuId).count();
            if (count > 0) {
                throw new BusinessException("存在子菜单,不允许删除");
            }
        }
    }

    public List<Long> selectMenuIdsByRoleId(Long roleId) {
        return this.baseMapper.selectMenuIdsByRoleId(roleId);
    }

    public int batchRoleMenu(Long roleId, List<Long> menuIds) {
        return this.baseMapper.batchRoleMenu(roleId, menuIds);
    }

    public int deleteRoleMenuByRoleId(Long roleId) {
        return this.baseMapper.deleteRoleMenuByRoleId(roleId);
    }

}
