package cn.ricetofu.task.events;

import cn.ricetofu.task.core.data.PlayerData;
import cn.ricetofu.task.core.task.Task;
import cn.ricetofu.task.core.task.TaskManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-30
 * @Discription: 食用/饮用任务事件监听
 * */
public class EatListener implements Listener {

    @EventHandler
    public void eatListener(PlayerItemConsumeEvent event){
        PlayerData playerData = TaskManager.getPlayerDataById(event.getPlayer().getUniqueId().toString());
        for (Task task : playerData.getTasks()) {
            if(task.isFinish())continue;
            if(!task.getTaskInfo().getType().equals("eat"))continue;
            if(!task.getArgs().getItem().equals("")){//是否需要进行类型匹配
                Material material = Material.matchMaterial(task.getArgs().getItem());
                if(material.equals(event.getItem().getType())){
                    task.setFinish(task.getFinish()+1);
                }
            }else task.setFinish(task.getFinish()+1);

            //判断是否已经完成
            if(task.isFinish())TaskManager.finish_one(event.getPlayer().getUniqueId().toString(),task.getTaskInfo().getId());
        }

    }

}
