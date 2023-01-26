package cn.ricetofu.task.core.task;

import lombok.Getter;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-22
 * @Discription: 任务类型枚举
 * */
public enum TaskType {

    BREAK("break"),//方块/物品破坏任务
    ;

    @Getter
    private String type_name;
    TaskType(String type_name) {
        this.type_name = type_name;
    }
}
