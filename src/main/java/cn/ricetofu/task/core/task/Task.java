package cn.ricetofu.task.core.task;


import cn.ricetofu.task.core.task.config.TaskArgs;
import cn.ricetofu.task.core.task.config.TaskInfo;
import cn.ricetofu.task.util.WeightShuffle;
import lombok.Data;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;


/**
 * @Author: RiceTofu123
 * @Date: 2023-01-22
 * @Discription: 任务实体，包含任务的基本信息和这个任务的参数
 * */
@Data
public class Task{

    //任务的原始信息参数
    private TaskInfo taskInfo;

    //任务的数据参数
    private TaskArgs args;

    //任务的完成情况参数
    private Integer finish;

    public Task(TaskInfo taskInfo){
        this.taskInfo = taskInfo;
        //根据任务的类型来确定实例化时args里面的数据类型
        args = WeightShuffle.getRandomArgsByWeight(taskInfo.getArgs());
        finish = 0;
    }

    /**
     * 判断当前任务是否已经被完成
     * @return 是否被完成
     * */
    public boolean isFinish(){
        return finish>=args.getAmount();
    }

    /**
     * 获取当前任务的显示物品
     * */
    public Material getDisplayItem(){
        // TODO
        // 变量解析
        return Material.matchMaterial(taskInfo.getDisplay());
    }

    /**
     * 获取当前任务的显示名称(变量解析后)
     * */
    public String getParsedName(){
        // TODO
        // 变量解析
        return taskInfo.getName();
    }

    /**
     * 获取当前任务的显示Lore(变量解析后)
     * */
    public List<String> getParsedLore(){
        // TODO
        // 变量解析
        return taskInfo.getLore();
    }


}
