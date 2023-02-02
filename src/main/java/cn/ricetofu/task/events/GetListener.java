package cn.ricetofu.task.events;

import cn.ricetofu.task.core.data.PlayerData;
import cn.ricetofu.task.core.task.Task;
import cn.ricetofu.task.core.task.TaskManager;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.Set;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-27
 * @Discription: 物品拾取事件监听
 * */
public class GetListener implements Listener {

    @EventHandler
    public void getListener(EntityPickupItemEvent event){
        LivingEntity entity = event.getEntity();
        if(!(entity instanceof Player))return;//拾取物品的实体不为玩家
        Player player = (Player) entity;//拾取物品的玩家
        Item item = event.getItem();//拾取的物品
        Set<String> strings = ThrowListener.dropItemsSet.get(player.getUniqueId().toString());
        if(strings!=null&&strings.size()!=0){
            //不为空，则扔过东西
            if(strings.contains(item.getUniqueId().toString())){
                //拾取的是自己扔的东西，删除并返回
                strings.remove(item.getUniqueId().toString());
                return;
            }
        }
        //不是的话则判断类型是否和任务参数中的匹配
        PlayerData playerData = TaskManager.getPlayerDataById(player.getUniqueId().toString());
        for (Task task : playerData.getTasks()) {
            if(!task.isFinish()&&task.getTaskInfo().getType().equals("get")){
                if(!task.getArgs().getItem().equals("")) {
                    Material material = Material.matchMaterial(task.getArgs().getItem());
                    if (!material.equals(item.getItemStack().getType())) return;//类型不匹配，则返回
                    //更新完成数量，再判断一次是否完成任务
                    task.setFinish(task.getFinish() + item.getItemStack().getAmount());
                }else task.setFinish(task.getFinish() + item.getItemStack().getAmount());
                if(task.isFinish())TaskManager.finish_one(player.getUniqueId().toString(),task.getTaskInfo().getId());//完成任务
            }
        }
    }
}
