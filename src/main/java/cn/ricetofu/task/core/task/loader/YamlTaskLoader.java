package cn.ricetofu.task.core.task.loader;

import cn.ricetofu.task.TofuDailyTask;
import cn.ricetofu.task.core.task.TaskLoader;
import cn.ricetofu.task.core.task.TaskManager;
import cn.ricetofu.task.core.task.config.TaskArgs;
import cn.ricetofu.task.core.task.config.TaskInfo;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-25
 * @Discription: yaml任务加载器
 * */
public class YamlTaskLoader implements TaskLoader {

    @Override
    public boolean load(File file) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);//加载yaml文件
        Set<String> keys = configuration.getKeys(false);//获取所有外部键(每个则代表一个任务信息)

        List<TaskInfo> loads = new LinkedList<>();
        for (String key : keys) {

            ConfigurationSection section = configuration.getConfigurationSection(key);
            TaskInfo info = new TaskInfo();
            info.setId(key);
            info.setType(section.getString("type"));
            info.setName(section.getString("name"));
            info.setEnable(section.getBoolean("enable"));
            info.setLore(section.getStringList("lore"));
            info.setWeight(section.getInt("weight"));
            info.setDisplay(section.getString("display"));

            List<TaskArgs> taskArgs = new LinkedList<>();

            for (Object args : section.getList("args")) {

                Map<String,Object> map = (Map<String,Object>)args;
                TaskArgs arg = new TaskArgs();
                if(map.containsKey("name"))arg.setName((String) map.get("name"));
                if(map.containsKey("item"))arg.setItem((String) map.get("item"));
                if(map.containsKey("entity"))arg.setEntity((String) map.get("entity"));
                if(map.containsKey("amount"))arg.setAmount((Integer) map.get("amount"));
                if(map.containsKey("weight"))arg.setWeight((Integer) map.get("weight"));
                taskArgs.add(arg);
            }

            info.setArgs(taskArgs);
            loads.add(info);
        }

        //任务正确性校验
        int success = 0;
        int fail = 0;
        for (int i = 0; i < loads.size(); i++) {
            if(isRightTaskInfo(loads.get(i)))success++;
            else {
                fail++;
                loads.remove(i);
                i--;
            }
        }

        //添加到任务表中
        for (TaskInfo taskInfo : loads) {
            TaskManager.getAllTaskInfoMap().put(taskInfo.getId(),taskInfo);//将任务添加入表
        }

        Bukkit.getLogger().info(TofuDailyTask.prefix +"在:"+file.getName()+"中加载成功了:"+success+"个任务,失败了:"+fail+"个");
        return false;
    }
}
