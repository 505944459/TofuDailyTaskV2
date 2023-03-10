package cn.ricetofu.task.core.task;

import cn.ricetofu.task.config.Config;
import cn.ricetofu.task.config.Message;
import cn.ricetofu.task.core.data.PlayerData;
import cn.ricetofu.task.core.data.DataManager;
import cn.ricetofu.task.core.reward.RewardManager;
import cn.ricetofu.task.core.task.config.TaskInfo;
import cn.ricetofu.task.util.DateFormatter;
import cn.ricetofu.task.util.WeightShuffle;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Logger;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-21
 * @Discription: 主操作类
 * */
@Data
public class TaskManager {
    //数据管理器对象
    private static DataManager dataManager;

    //所有成功加载的任务信息的保存表
    @Getter
    private static Map<String, TaskInfo> allTaskInfoMap = new HashMap<>();

    //日志对象
    private static Logger logger = Bukkit.getLogger();

    /**
     * 主管理类的初始化方法
     * @param data 数据管理器对象
     * @return 初始化是否成功
     * */
    public static boolean init(DataManager data){
        dataManager = data;



        return true;
    }

    /**
     * 玩家领取每日任务的方法
     * @param player_id 玩家uuid
     * @return 是否成功
     * */
    public static boolean getDailyTask(String player_id){

        PlayerData playerData = dataManager.getOne(player_id);
        if(playerData.isReceivedToday())return false;
        Collection<TaskInfo> values = allTaskInfoMap.values();

        //随机获取指定个数的任务
        List<TaskInfo> random = WeightShuffle.getRandomTaskInfoByWeight(values, Config.getDaily_tasks());

        List<Task> tasks = new LinkedList<>();
        for (TaskInfo taskInfo : random) {
            tasks.add(new Task(taskInfo));//任务对象获取
        }
        playerData.setTasks(tasks);//设置每日任务
        playerData.setLast_receive_date(DateFormatter.format(new Date()));//设置上次收到的时间

        return true;
    }

    /**
     * 玩家完成一个任务后的方法调用
     * @param player_id 玩家的uuid
     * @param task_id 任务的id
     * */
    public static void finish_one(String player_id,String task_id){
        //发送提示消息
        Player player = Bukkit.getPlayer(UUID.fromString(player_id));
        TaskInfo taskInfo = allTaskInfoMap.get(task_id);
        player.sendMessage(Message.getOne_task_finished().replace("%name%",taskInfo.getName()));

        //保存一遍玩家信息
        savePlayerDataById(player_id);

        //检查玩家是否已经完成了所有的任务
        PlayerData playerData = getPlayerDataById(player_id);
        boolean finish = true;
        for (Task task : playerData.getTasks()) {
            if(!task.isFinish()){
                finish = false;
                break;
            }
        }
        //已经完成了所有任务
        if(finish){
            //保存完成时间
            playerData.setLast_finish_date(DateFormatter.format(new Date()));

            player.sendMessage(Message.getAll_task_finished());//玩家提示
            if(Config.getAuto_reward()) RewardManager.getDailyTaskRewards(player_id);//自动领取奖励
        }
    }

    /**
     * 获取一个玩家数据对象
     * @param player_id 玩家的uuid
     * @return 玩家数据对象
     * */
    public static PlayerData getPlayerDataById(String player_id){
        PlayerData playerData = dataManager.getOne(player_id);

        // TODO 是否需要接取任务……
        if(playerData.getTasks()==null||!playerData.getLast_reward_date().equals(DateFormatter.format(new Date()))){
            //任务列表为空，接取任务
            boolean dailyTask = TaskManager.getDailyTask(player_id);
            if(dailyTask)Bukkit.getPlayer(UUID.fromString(player_id)).sendMessage(Message.getGet_daily_tasks());
        }

        return playerData;
    }

    /**
     * 保存一个玩家的数据
     * @param player_id 玩家的uuid
     * @return 是否成功
     * */
    public static boolean savePlayerDataById(String player_id){
        return dataManager.saveOne(player_id,false);
    }

    /**
     * 保存一个玩家的数据并移除数据在内存中的缓存
     * @param player_id 玩家的uuid
     * @return 是否成功
     * */
    public static boolean savePlayerDataByIdAndRemoveCache(String player_id){
        return dataManager.saveOne(player_id,true);
    }
}
