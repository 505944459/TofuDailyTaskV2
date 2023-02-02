package cn.ricetofu.task.events;

import cn.ricetofu.task.core.data.PlayerData;
import cn.ricetofu.task.core.task.Task;
import cn.ricetofu.task.core.task.TaskManager;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class FishListener implements Listener {

    @EventHandler
    public void fishListener(PlayerFishEvent event){
        if (!event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) return; //没有捕获鱼
        if(!event.getCaught().getType().equals(EntityType.DROPPED_ITEM))return;//不是掉落物
        Player player = event.getPlayer();
        PlayerData playerData = TaskManager.getPlayerDataById(player.getUniqueId().toString());
        for (Task task : playerData.getTasks()) {
            if(task.isFinish())continue;//任务已完成
            if(!task.getTaskInfo().getType().equals("fish"))continue;//类型不匹配
            //是否需要判断类型
            if(task.getArgs().getItem().equals("")){
                //不需要
                task.setFinish(task.getFinish()+1);
            }else {
                //需要
                Material material = Material.matchMaterial(task.getArgs().getItem());//需要的物品
                Item caught = (Item) event.getCaught();
                if(caught.getItemStack().getType().equals(material))task.setFinish(task.getFinish()+1);
            }
            //判断任务是否完成
            if(task.isFinish())TaskManager.finish_one(player.getUniqueId().toString(),task.getTaskInfo().getId());
        }
    }

}
