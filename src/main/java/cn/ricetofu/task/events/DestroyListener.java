package cn.ricetofu.task.events;

import cn.ricetofu.task.core.data.PlayerData;
import cn.ricetofu.task.core.task.Task;
import cn.ricetofu.task.core.task.TaskManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemBreakEvent;

/**
 * @Author: RiceTofu123
 * @DateL 2023-02-02
 * @Discription: 物品损坏监听
 * */
public class DestroyListener implements Listener {

    @EventHandler
    public void destroyListener(PlayerItemBreakEvent event){
        Player player = event.getPlayer();
        PlayerData playerData = TaskManager.getPlayerDataById(player.getUniqueId().toString());
        for (Task task : playerData.getTasks()) {
            if(task.isFinish())continue;//任务已完成
            if(!task.getTaskInfo().getType().equals("destroy"))continue;//类型不匹配
            //判断是否需要类型匹配
            if(task.getArgs().getItem().equals("")){
                //不需要
                task.setFinish(task.getFinish()+1);
            }else {
                //需要
                if (event.getBrokenItem().getType().equals(Material.matchMaterial(task.getArgs().getItem()))) {
                    task.setFinish(task.getFinish()+1);
                }
            }
            //判断任务是否完成
            if(task.isFinish())TaskManager.finish_one(player.getUniqueId().toString(),task.getTaskInfo().getId());
        }
    }
}
