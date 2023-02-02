package cn.ricetofu.task.events;

import cn.ricetofu.task.core.data.PlayerData;
import cn.ricetofu.task.core.task.Task;
import cn.ricetofu.task.core.task.TaskManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

/**
 * @Author: RiceTofu123
 * @Date: 2023-02-02
 * @Discription: 附魔事件监听
 * */
public class EnchantListener implements Listener {

    @EventHandler
    public void enchantListener(EnchantItemEvent event){
        Player player = event.getEnchanter();
        PlayerData playerData = TaskManager.getPlayerDataById(player.getUniqueId().toString());
        for (Task task : playerData.getTasks()) {
            if(task.isFinish())continue;//任务已完成
            if(!task.getTaskInfo().getType().equals("enchant"))continue;//类型不匹配
            //是否需要判断类型
            if(task.getArgs().getItem().equals("")){
                //不需要
                task.setFinish(task.getFinish()+1);
            }else {
                //需要
                if (event.getItem().getType().equals(Material.matchMaterial(task.getArgs().getItem()))) {
                    task.setFinish(task.getFinish()+1);
                }
            }
            //判断任务是否完成
            if(task.isFinish())TaskManager.finish_one(player.getUniqueId().toString(),task.getTaskInfo().getId());
        }
    }
}
