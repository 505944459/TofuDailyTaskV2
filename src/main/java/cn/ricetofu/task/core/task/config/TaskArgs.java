package cn.ricetofu.task.core.task.config;

import cn.ricetofu.task.core.Weighable;
import lombok.Data;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-27
 * @Discription: 任务参数的实体类
 * */
@Data
public class TaskArgs implements Weighable {

    //展示名称,任务判断和这个属性无任何管理,仅仅是用于描述整个配置参数的名字
    private String name = "";

    //物品类型匹配
    private String item = "";

    //实体类型匹配
    private String entity = "";

    //该任务需要完成的数量/次数
    private Integer amount = -1;

    //该参数的随机权重
    private Integer weight = 0;

    public int getWeight() {
        return weight;
    }
}
