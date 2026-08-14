package com.gp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessTypeEnum {

    OTHER(0, "其他"),
    INSERT(1, "新增"),
    UPDATE(2, "修改"),
    DELETE(3, "删除"),
    EXPORT(4, "导出"),
    IMPORT(5, "导入"),
    CLEAN(6, "清空"),
    GRANT(7, "授权");

    private final Integer code;
    private final String msg;

}
