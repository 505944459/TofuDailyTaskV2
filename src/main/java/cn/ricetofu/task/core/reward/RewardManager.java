package cn.ricetofu.task.core.reward;


import cn.ricetofu.task.config.Config;
import cn.ricetofu.task.config.Message;
import cn.ricetofu.task.core.data.PlayerData;
import cn.ricetofu.task.core.task.TaskManager;
import cn.ricetofu.task.util.DateFormatter;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-24
 * @Discription: 奖励管理类
 * */
public class RewardManager {

    //奖励信息列表
    @Getter
    private static final List<RewardInfo> rewardInfoList = new ArrayList<>();

    /**
     * 玩家领取每日任务奖励
     * @param player_id 玩家uuid
     * */
    public static void getDailyTaskRewards(String player_id){

        //用于执行命令的对象
        Server server = Bukkit.getServer();
        Player player = server.getPlayer(UUID.fromString(player_id));

        //更新获取奖励的时间
        PlayerData playerData = TaskManager.getPlayerDataById(player_id);
        playerData.setLast_reward_date(DateFormatter.format(new Date()));//更新上次收到奖励的时间

        //循环遍历奖励项
        for (RewardInfo rewardInfo : rewardInfoList) {

            //检测玩家是否有权限获取这个奖励
            if(rewardInfo.getPermission()!=null&&!rewardInfo.getPermission().equals("")){
                if(!player.hasPermission(rewardInfo.getPermission()))continue;//不满足权限
            }

            String command = rewardInfo.getCommand();//需要执行的指令原始字符串
            String message = rewardInfo.getMessage();//执行完成后发送给玩家的信息

            //随机这个奖励的参数(如果这个奖励有配置的话)
            if(rewardInfo.getRandom()!=null&&rewardInfo.getRandom().size()!=0){
                String s = getRandomArg(rewardInfo.getRandom()).split(" ")[0];
                command = command.replace("%random%",s);//参数替换
                message = message.replace("%random%",s);//参数替换
            }

            command = command.replace("%player%",player.getName()); //参数替换

            //指令执行
            boolean b = server.dispatchCommand(Bukkit.getConsoleSender(), command);
            //发送信息
            player.sendMessage(Message.getPrefix()+message);
        }
        player.sendMessage(Message.getGet_daily_rewards());//获取了奖励的提示
    }

    /**
     * 获取一个随机的指令参数，根据权重
     * @param args 指令参数
     * @return 获取的随机参数
     * */
    private static String getRandomArg(List<String> args){
        int total = 0;
        for (String arg : args) {
            total += Integer.parseInt(arg.split(" ")[1]);
        }
        int i = new Random().nextInt(total + 1);
        int now = 0;
        for (String arg:args){
            now += Integer.parseInt(arg.split(" ")[1]);
            if(now>=i)return arg;
        }
        return null;
    }

}
