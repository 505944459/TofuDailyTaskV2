package cn.ricetofu.task.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.*;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-21
 * @Discription: 玩家物品扔出监听，并且会记录扔出物品实体的UUID，防止刷GET任务
 * */
public class ThrowListener implements Listener {

    public static final Map<String, Set<String>> dropItemsSet = new HashMap<>();

    @EventHandler
    public void throwListener(PlayerDropItemEvent event){
        String uuid = event.getPlayer().getUniqueId().toString();
        String item_uuid = event.getItemDrop().getUniqueId().toString();
        Set<String> strings = dropItemsSet.get(uuid);
        if(strings==null){
            strings = new HashSet<>();
            strings.add(item_uuid);
            dropItemsSet.put(uuid,strings);
        }else {
            //容量过大的话则清空一次
            if(strings.size()>= 1024 * 100)strings.clear();
            //添加当前这个item的uuid
            strings.add(item_uuid);

        }
    }

}
