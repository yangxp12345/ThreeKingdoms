package org.yang.business.constant;

import lombok.Getter;

@Getter
public enum RunEnum {
    WAIT("未运行"),
    RUN("运行中"),
    SUCCESS("运行完成");
    final private String name;

    RunEnum(String name) {
        this.name = name;
    }
}
