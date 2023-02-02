package cn.ricetofu.task.core.gui;

import cn.ricetofu.task.config.Config;
import cn.ricetofu.task.core.data.PlayerData;
import cn.ricetofu.task.core.reward.RewardInfo;
import cn.ricetofu.task.core.task.Task;
import cn.ricetofu.task.core.task.TaskManager;
import cn.ricetofu.task.util.DateFormatter;
import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @Author: RiceTofu123
 * @Date: 2023-01-29
 * @Discription: 玩家GUI相关的指令/事件等
 * */
public class DefaultGUI implements Listener {


    public static void openInv(Player player){
        PlayerData playerData = TaskManager.getPlayerDataById(player.getUniqueId().toString());//获取玩家数据对象
        //创建一个 6*9 大小的箱子
        Inventory inventory = Bukkit.createInventory(player, 6 * 9, "§a[§b每日任务§a]§f");

        //先绘制一波边界
        ItemStack glassPane = XMaterial.SLIME_BLOCK.parseItem();
        ItemMeta itemMeta = glassPane.getItemMeta();
        itemMeta.setDisplayName("§f边界线");
        glassPane.setItemMeta(itemMeta);
        //放置边界
        for (int i = 0; i < 6; i++) for (int j = 0; j < 9; j++) {
                if(i==0||i==5||j==0||j==8){
                    //边界方块
                    inventory.setItem(i*9+j,glassPane);
                }
            }

        //绘制玩家头颅
        ItemStack skull = SkullUtils.getSkull(player.getUniqueId());//玩家头颅
        ItemMeta playerDataMate = skull.getItemMeta();
        playerDataMate.setDisplayName("§a"+player.getName()+"§f");
        List<String> lore = new ArrayList<>();
        lore.add("§f累计完成次数:§a"+playerData.getFinished_times()+"§f");
        lore.add("§f上次完成时间:§a"+playerData.getLast_finish_date()+"§f");
        playerDataMate.setLore(lore);
        skull.setItemMeta(playerDataMate);
        inventory.setItem(0,skull);

        //绘制任务列表
        List<Task> tasks = playerData.getTasks();
        int i = 2;
        int j = 2;
        for (Task task : tasks) {
            ItemStack itemStack = new ItemStack(task.getDisplayItem());
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName("§f"+task.getParsedName());
            List<String> parsedLore = task.getParsedLore();
            lore = new ArrayList<>();
            lore.add("§a-------------------");
            for (String s : parsedLore) {
                lore.add("§f"+s);
            }
            lore.add("§a-------------------");
            String finish = task.isFinish()?"§a已完成":"§c未完成";
            lore.add("完成情况:"+finish);
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
            inventory.setItem(i*9+j,itemStack);
            j++;
            if(j==7){
                j = 2;
                i++;
            }
        }

        //放置奖励领取箱子
        ItemStack stack = new ItemStack(Material.CHEST);
        ItemMeta cheat_meta = stack.getItemMeta();
        cheat_meta.setDisplayName("§a每日任务奖励");
        lore = new ArrayList<>();
        lore.add("§a-------------------");
        lore.add("§f奖励内容:");
        for (RewardInfo reward : Config.getRewards()) {
            lore.add("   §f"+reward.getName());
        }
        lore.add("§a-------------------");
        String state = "§b未知";
        if(playerData.isRewardToday())state = "§a已领取";
        else if(playerData.getLast_finish_date().equals(DateFormatter.format(new Date()))&&!playerData.isRewardToday())state = "§a可领取";
        else state = "§c任务未完成";
        lore.add("§a状态: "+state);
        if(state.equals("§a可领取"))lore.add("§a点击这里来领取你的今日奖励~~~");
        cheat_meta.setLore(lore);
        stack.setItemMeta(cheat_meta);
        inventory.setItem(5*9 + 4,stack);


        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        //保护箱子GUI
        Player player = (Player) event.getWhoClicked();
        InventoryView inv = player.getOpenInventory();
        if(inv.getTitle().equals("§a[§b每日任务§a]§f")){
            event.setCancelled(true);//取消这次点击事件
        }

    }

}
