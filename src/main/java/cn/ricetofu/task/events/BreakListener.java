package cn.ricetofu.task.events;

import cn.ricetofu.task.core.data.PlayerData;
import cn.ricetofu.task.core.task.Task;
import cn.ricetofu.task.core.task.TaskManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-25
 * @Discription: break(方块破坏)事件监听
 * */
public class BreakListener implements Listener {

    @EventHandler
    public void breakListener(BlockBreakEvent event){
        PlayerData playerData = TaskManager.getPlayerDataById(event.getPlayer().getUniqueId().toString());
        for (Task task : playerData.getTasks()) {
            if (task.isFinish()) continue;//任务已完成
            if (!task.getTaskInfo().getType().equals("break")) continue;//任务类型不匹配
            if(!task.getArgs().getItem().equals("")){//是否需要匹配方块类型
                Material material = Material.matchMaterial(task.getArgs().getItem());
                if(material.equals(event.getBlock().getType()))task.setFinish(task.getFinish()+1);//类型匹配
            }else  task.setFinish(task.getFinish()+1);


            if (task.isFinish()) TaskManager.finish_one(event.getPlayer().getUniqueId().toString(), task.getTaskInfo().getId());//任务完成调用
        }
    }
}
