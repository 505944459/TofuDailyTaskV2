package cn.ricetofu.task.events;

import cn.ricetofu.task.core.data.PlayerData;
import cn.ricetofu.task.core.task.Task;
import cn.ricetofu.task.core.task.TaskManager;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-30
 * @Discription: 合成任务监听
 * */
public class CraftListener implements Listener {

    @EventHandler
    public void craftListener(CraftItemEvent event){
        HumanEntity whoClicked = event.getWhoClicked();//谁触发的这个事件
        PlayerData playerData = TaskManager.getPlayerDataById(((Player) whoClicked).getPlayer().getUniqueId().toString());
        for (Task task : playerData.getTasks()) {
            if(task.isFinish())continue;//已完成的任务
            if(!task.getTaskInfo().getType().equals("craft"))continue;//类型不匹配的任务
            String entity = task.getArgs().getEntity();
            if(!entity.equals("")){
                Material material = Material.matchMaterial(entity);
                if(material.equals(event.getCurrentItem().getType())){
                    task.setFinish(task.getFinish()+event.getCurrentItem().getAmount());//更新数量
                }
            }else {
                task.setFinish(task.getFinish()+event.getCurrentItem().getAmount());//更新数量
            }
            //任务是否完成
            if(task.isFinish())TaskManager.finish_one(((Player) whoClicked).getPlayer().getUniqueId().toString(),task.getTaskInfo().getId());
        }
    }

}
