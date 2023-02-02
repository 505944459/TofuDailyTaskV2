package cn.ricetofu.task.events;

import cn.ricetofu.task.core.data.PlayerData;
import cn.ricetofu.task.core.task.Task;
import cn.ricetofu.task.core.task.TaskManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-30
 * @Discription: 方块放置的监听
 * */
public class PlaceListener implements Listener {

    @EventHandler
    public void placeListener(BlockPlaceEvent event){
        PlayerData playerData = TaskManager.getPlayerDataById(event.getPlayer().getUniqueId().toString());
        for (Task task : playerData.getTasks()) {
            if (task.isFinish()) continue;//任务已完成
            if (!task.getTaskInfo().getType().equals("place")) continue;//任务类型不匹配
            if(!task.getArgs().getItem().equals("")) {
                if (Material.matchMaterial(task.getArgs().getItem()).equals(event.getBlock().getType())) task.setFinish(task.getFinish() + 1);//更新完成计数
            }
            else task.setFinish(task.getFinish() + 1);//更新完成计数
            if (task.isFinish()) TaskManager.finish_one(event.getPlayer().getUniqueId().toString(), task.getTaskInfo().getId());//任务完成调用
        }

    }
}
