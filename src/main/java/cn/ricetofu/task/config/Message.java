package cn.ricetofu.task.config;

import cn.ricetofu.task.TofuDailyTask;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.logging.Logger;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-24
 * @Discription: 与插件的语言文件配置对应的实体类
 * */
@Data
public class Message {
    //消息前缀
    @Getter
    private static String prefix;
    //玩家收到每日任务
    @Getter
    private static String get_daily_tasks;
    //玩家完成了一个小的每日任务
    @Getter
    private static String one_task_finished;
    //玩家完成了所有的每日任务
    @Getter
    private static String all_task_finished;
    //玩家领取每日任务的奖励
    @Getter
    private static String get_daily_rewards;

    private static Logger logger = Bukkit.getLogger();

    public static boolean init(YamlConfiguration config){

        if(!config.contains("prefix")){
            logger.warning(TofuDailyTask.prefix+"prefix属性缺少,已使用默认值");
            prefix = "§a[§b每日任务§a]§f：";
        }else prefix = config.getString("prefix");

        if(!config.contains("get_daily_tasks")){
            logger.warning(TofuDailyTask.prefix+"get_daily_tasks属性缺少,已使用默认值");
            get_daily_tasks = prefix+"已经为你自动领取每日任务~输入/task即可查看详情";
        }else get_daily_tasks = prefix+config.getString("get_daily_tasks");

        if(!config.contains("one_task_finished")){
            logger.warning(TofuDailyTask.prefix+"one_task_finished属性缺少,已使用默认值");
            one_task_finished = prefix+"你成功完成了:%name%";
        }else one_task_finished = prefix+config.getString("one_task_finished");

        if(!config.contains("all_task_finished")){
            logger.warning(TofuDailyTask.prefix+"all_task_finished属性缺少,已使用默认值");
            all_task_finished = prefix+"你成功完成了所有的每日任务，赶快领取奖励叭~";
        }else all_task_finished = prefix+config.getString("all_task_finished");

        if(!config.contains("get_daily_rewards")){
            logger.warning(TofuDailyTask.prefix+"get_daily_rewards属性缺少,已使用默认值");
            get_daily_rewards = prefix+"你成功领取了每日任务奖励，明天记得再来哦~";
        }else get_daily_rewards = prefix+config.getString("get_daily_rewards");

        return true;
    }
}
