package cn.ricetofu.task.events;

import cn.ricetofu.task.config.Message;
import cn.ricetofu.task.core.data.PlayerData;
import cn.ricetofu.task.core.task.TaskManager;
import cn.ricetofu.task.util.DateFormatter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-22
 * @Discription: 玩家加入服务器事件监听
 * */
public class PlayerJoinListener implements Listener {

    @EventHandler
    public void playerJoin(PlayerJoinEvent event){
        UUID uuid = event.getPlayer().getUniqueId();
        TaskManager.getPlayerDataById(uuid.toString());
    }

}
