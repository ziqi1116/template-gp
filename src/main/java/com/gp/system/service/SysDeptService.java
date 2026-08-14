package com.gp.system.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.exception.BusinessException;
import com.gp.system.domain.SysDept;
import com.gp.system.mapper.SysDeptMapper;
import org.springframework.stereotype.Service;

@Service
public class SysDeptService extends ServiceImpl<SysDeptMapper, SysDept> {

    public List<SysDept> listAll() {
        return list();
    }

    public List<SysDept> listDeptTree() {
        List<SysDept> all = list();
        return buildDeptTree(all, 0L);
    }

    public List<SysDept> listDeptTreeExcludeChild(Long deptId) {
        List<SysDept> all = list();
        List<Long> excludeIds = new ArrayList<>();
        excludeChild(all, deptId, excludeIds);
        List<SysDept> filtered = all.stream()
                .filter(d -> !excludeIds.contains(d.getId()))
                .collect(Collectors.toList());
        return buildDeptTree(filtered, 0L);
    }

    private void excludeChild(List<SysDept> all, Long deptId, List<Long> excludeIds) {
        excludeIds.add(deptId);
        for (SysDept d : all) {
            if (deptId.equals(d.getParentId())) {
                excludeChild(all, d.getId(), excludeIds);
            }
        }
    }

    private List<SysDept> buildDeptTree(List<SysDept> depts, Long parentId) {
        List<SysDept> tree = new ArrayList<>();
        for (SysDept dept : depts) {
            if (!parentId.equals(dept.getParentId())) {
                continue;
            }
            tree.add(dept);
        }
        return tree;
    }

    public boolean hasChildByDeptId(Long deptId) {
        return this.lambdaQuery().eq(SysDept::getParentId, deptId).count() > 0;
    }

    public void checkDeptAllowed(Long deptId) {
        SysDept dept = getById(deptId);
        if (dept != null && "admin".equals(dept.getLeader())) {
            throw new BusinessException("不允许删除该部门");
        }
    }

}
