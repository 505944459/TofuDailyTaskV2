package cn.ricetofu.task.core.task.config;

import cn.ricetofu.task.core.Weighable;
import lombok.Data;


@Data
public class TaskArgs implements Weighable {

    //展示名称,任务判断和这个属性无任何管理,仅仅是用于描述整个配置参数的名字
    private String name;

    //物品类型匹配
    private String item;

    //实体类型匹配
    private String entity;

    //物品name强匹配
    private String item_name_equals;

    //物品name弱匹配
    private String item_name_match;

    //物品lore强匹配
    private String item_lore_equals;

    //物品lore弱匹配
    private String item_lore_match;

    //实体名字强匹配
    private String entity_name_equals;

    //实体名字弱匹配
    private String entity_name_match;

    //该任务需要完成的数量/次数
    private Integer amount;

    //该参数的随机权重
    private Integer weight;

    public int getWeight() {
        return weight;
    }
}
