package cn.ricetofu.task.core.task.config;

import cn.ricetofu.task.core.Weighable;
import lombok.Data;

import java.util.List;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-21
 * @Discription: 任务信息类，与配置文件对应
 * */
@Data
public class TaskInfo implements Weighable {
    //任务的id
    private String id;

    //任务的类型
    private String type;

    //任务的名字
    private String name;

    //展示的物品id
    private String display;

    //任务的lore
    private List<String> lore;

    //任务的权重
    private Integer weight;

    //是否被启用
    private Boolean enable;

    //任务的配置参数
    private List<TaskArgs> args;

    public int getWeight() {
        return weight;
    }
}
