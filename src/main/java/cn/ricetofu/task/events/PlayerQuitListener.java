package cn.ricetofu.task.events;

import cn.ricetofu.task.core.task.TaskManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void playerQuitListener(PlayerQuitEvent event){
        String uuid = event.getPlayer().getUniqueId().toString();
        //保存该玩家的任务数据，并且删除该玩家在内存中的数据，这样save()就只会保存在线玩家的数据，不会花很多时间
        TaskManager.savePlayerDataByIdAndRemoveCache(uuid);
    }
}
