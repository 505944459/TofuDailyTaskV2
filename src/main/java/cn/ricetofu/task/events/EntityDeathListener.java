package cn.ricetofu.task.events;

import cn.ricetofu.task.core.data.PlayerData;
import cn.ricetofu.task.core.task.Task;
import cn.ricetofu.task.core.task.TaskManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {

    @EventHandler
    public void entityDeathListener(EntityDeathEvent event){
        Player player = event.getEntity().getKiller();//击杀者
        EntityType type = event.getEntity().getType();//实体的类型
        PlayerData playerData = TaskManager.getPlayerDataById(player.getUniqueId().toString());
        for (Task task : playerData.getTasks()) {
            if(task.isFinish())return;//任务已完成
            if(!task.getTaskInfo().getType().equals("kill"))return;//类型不匹配
            if(task.getArgs().getEntity().startsWith("mm:"))return;//mm类型怪物
            String entity = task.getArgs().getEntity();
            EntityType entityType = EntityType.valueOf(entity);
            if(entityType==null){
                entityType = EntityType.fromName(entity);
                if(entityType==null) EntityType.fromId(Integer.parseInt(entity));
            }

            if(type.equals(entityType)){
                //类型匹配
                task.setFinish(task.getFinish()+1);
                //判断是否完成
                if (task.isFinish()) TaskManager.finish_one(player.getUniqueId().toString(),task.getTaskInfo().getId());//任务完成一个
            }
        }
    }

}
