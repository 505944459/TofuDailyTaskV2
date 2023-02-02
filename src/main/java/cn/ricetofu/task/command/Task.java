package cn.ricetofu.task.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.ricetofu.task.core.gui.DefaultGUI.openInv;

/**
 * @Author: RiceTofu123
 * @Date: 2023-02-02
 * @Discription: 命令处理
 * */
public class Task implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            //异步处理箱子打开事件
            new Thread(() -> openInv(((Player) sender))).start();
            return true;
        }
        return false;
    }
}
