package cn.ricetofu.task.core.data;

import cn.ricetofu.task.core.task.Task;
import cn.ricetofu.task.util.DateFormatter;
import lombok.Data;

import java.util.Date;
import java.util.List;


/**
 * @Author: RiceTofu123
 * @Date: 2023-01-22
 * @Discription: 玩家数据实体
 * */
@Data
public class PlayerData {

    //玩家的uuid
    private String uuid;

    //玩家的任务累计完成次数
    private Integer finished_times = 0;

    //玩家的任务目前的连续完成次数
    private Integer continuity_times = 0;

    //玩家上次收到任务的时间字符串
    private String last_receive_date = "2002-12-12";

    //玩家上次完成任务的时间字符串
    private String last_finish_date = "2002-12-12";

    //玩家上次获得每日任务奖励的时间
    private String last_reward_date = "2002-12-12";

    //玩家任务数据表
    private List<Task> tasks = null;

    /**
     * 当前玩家是否已经完成今日任务
     * @return 是否已经完成
     * */
    public boolean isFinishedToday(){
        if (DateFormatter.format(new Date()).equals(last_finish_date)) return true;
        return false;
    }

    /**
     * 当前玩家是否已经收到今日任务
     * @return 是否已经收到
     * */
    public boolean isReceivedToday(){
        if(DateFormatter.format(new Date()).equals(last_receive_date)) return true;
        return false;
    }

    /**
     * 当前玩家是否已经领取每日任务奖励
     * @return 是否领取每日任务奖励
     * */
    public boolean isRewardToday(){
        if(DateFormatter.format(new Date()).equals(last_reward_date))return true;
        return false;
    }
}
