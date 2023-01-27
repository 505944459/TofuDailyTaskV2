package cn.ricetofu.task.events;

import cn.ricetofu.task.core.data.PlayerData;
import cn.ricetofu.task.core.task.Task;
import cn.ricetofu.task.core.task.TaskManager;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-27
 * @Discription: mm怪物死亡监听，对mm实体击杀任务的支持
 * */
public class MythicMobDeathListener implements Listener {

    @EventHandler
    public void mythicMobDeathListener(MythicMobDeathEvent event){
        LivingEntity killer = event.getKiller(); //获取实体的击杀者
        if(!(killer instanceof Player))return; //不是玩家，则不继续判断
        Player player = (Player)killer;
        String internalName = event.getMobType().getInternalName(); //获取击杀的实体的名字，这里获取的或许是mm配置文件里面给实体的id
        PlayerData playerData = TaskManager.getPlayerDataById(player.getUniqueId().toString());
        for (Task task : playerData.getTasks()) {
            if(!task.isFinish()&&task.getTaskInfo().getType().equals("kill")){
                if(!task.getArgs().getEntity().startsWith("mm:"))return;//不是mm怪物
                if(internalName.equals(task.getArgs().getEntity().substring(3))){
                    task.setFinish(task.getFinish()+1);   //进度更新
                    if(task.isFinish())TaskManager.finish_one(player.getUniqueId().toString(),task.getTaskInfo().getId());//任务完成
                }
            }

        }

    }

}
