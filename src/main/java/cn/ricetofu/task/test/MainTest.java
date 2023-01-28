package cn.ricetofu.task.test;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class MainTest {
    public static void main(String[] args) {
        //Player player = Bukkit.getPlayer(UUID.randomUUID());
        Player player = Bukkit.getPlayer("Test");//首先根据玩家的id或者uuid获取一个玩家对象
        PlayerInventory inventory = player.getInventory();//获得玩家的库存对象
        ItemStack[] armorContents = inventory.getContents();//获取库存中的所有物品
        for (ItemStack armorContent : armorContents) {//遍历库存里面的所有物品
            if(armorContent.getType().equals(Material.ROTTEN_FLESH)){//进行物品匹配，如果物品为腐肉则删除
                int amount = armorContent.getAmount();//这就是腐肉的数量啦~
                inventory.remove(armorContent);//删除物品
            }
        }
    }
}
