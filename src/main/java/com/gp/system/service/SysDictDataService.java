package com.gp.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.system.domain.SysDictData;
import com.gp.system.mapper.SysDictDataMapper;
import org.springframework.stereotype.Service;

@Service
public class SysDictDataService extends ServiceImpl<SysDictDataMapper, SysDictData> {

    public List<SysDictData> listByDictType(String dictType) {
        return baseMapper.selectByDictType(dictType);
    }

    public void deleteByDictType(String dictType) {
        this.lambdaUpdate()
                .eq(SysDictData::getDictType, dictType)
                .remove();
    }

}