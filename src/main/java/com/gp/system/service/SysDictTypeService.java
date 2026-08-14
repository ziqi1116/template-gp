package com.gp.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.system.domain.SysDictType;
import com.gp.system.mapper.SysDictTypeMapper;
import org.springframework.stereotype.Service;

@Service
public class SysDictTypeService extends ServiceImpl<SysDictTypeMapper, SysDictType> {

    public List<SysDictType> listAll() {
        return list();
    }

    public boolean checkDictTypeUnique(SysDictType dictType) {
        Long dictTypeId = dictType.getId() == null ? -1L : dictType.getId();
        SysDictType info = this.lambdaQuery()
                .eq(SysDictType::getDictType, dictType.getDictType())
                .one();
        return info == null || info.getId().equals(dictTypeId);
    }

}