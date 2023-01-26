package cn.ricetofu.task.config;

import cn.ricetofu.task.TofuDailyTask;
import cn.ricetofu.task.core.reward.RewardInfo;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-24
 * @Discription: 与插件的config.yml文件配置对应类
 * */
@Data
public class Config {
    //插件的版本号
    @Getter
    private static String version;
    //插件的语言文件夹
    @Getter
    private static String lang_file;
    //插件的任务配置文件夹
    @Getter
    private static String task_dir;
    //每日任务的小任务个数
    @Getter
    private static Integer daily_tasks;
    //每日任务的小任务允许更换的次数
    @Getter
    private static Integer change_times;
    //奖励
    @Getter
    private static List<RewardInfo> rewards;
    //数据库的存储类型
    @Getter
    private static String data_type;
    //Mysql数据库配置信息
    @Getter
    private static String mysql_host;//主机地址
    @Getter
    private static String mysql_database;//数据库的名称
    @Getter
    private static String mysql_username;//数据库的用户名
    @Getter
    private static String mysql_password;//数据库的密码

    /**
     * 配置初始化
     * @param config 配置文件对象
     * @return 是否成功
     * */
    public static boolean init(YamlConfiguration config){
        String prefix = TofuDailyTask.prefix;//消息前缀
        Logger logger = Bukkit.getLogger();//logger对象

        if(!config.contains("version")){
            logger.warning(prefix+"缺少配置参数:version,已填入默认值: unknown");
            version = "unknown";
        }else version = config.getString("version");

        if(!config.contains("lang_file")){
            logger.severe(prefix+"缺少配置参数:lang_file");
            return false;
        }else {
            lang_file = config.getString("lang_file");
            if(!new File(TofuDailyTask.dataFolder,lang_file).exists()){
                logger.severe(prefix+"找不到lang_file配置路径下的文件!");
                return false;
            }
        }

        if(!config.contains("task_dir")){
            logger.severe(prefix+"缺少配置参数:task_dir");
            return false;
        }else {
            task_dir = config.getString("task_dir");
            File file = new File(TofuDailyTask.dataFolder, task_dir);
            if(!file.exists()||!file.isDirectory()){
                logger.severe(prefix+"找不到task_dir配置路径下的文件夹!");
                return false;
            }
        }

        if(!config.contains("daily_tasks")){
            logger.warning(prefix+"缺少配置参数:daily_tasks,已自动使用默认值: 3");
            daily_tasks = 3;
        }else daily_tasks = config.getInt("daily_tasks");

        if(!config.contains("change_times")){
            logger.warning(prefix+"缺少配置参数:change_times,已自动使用默认值: 3");
            change_times = 3;
        }

        if(!config.contains("rewards")){
            logger.severe(prefix+"缺少配置:rewards");
            return false;
        }else if(!loadRewards(config.getConfigurationSection("rewards"))){
            logger.severe(prefix+"rewards配置加载失败");
            return false;
        }

        if(!config.contains("data_type")){
            logger.warning(prefix+"缺少配置:data_type,已自动填入默认值: local");
        }else {
            data_type = config.getString("data_type");
            if(!data_type.equals("local")&&!data_type.equals("mysql")){
                logger.warning(prefix+"data_type参数值错误,应该为:local或mysql,已自动使用默认值:local");
                data_type = "local";
            }
        }

        boolean load = true;
        if(data_type.equals("mysql")){
            if(!config.contains("mysql_host")||!config.contains("mysql_database")||!config.contains("mysql_username")||!config.contains("mysql_password")){
                load = false;
            }
            if(load){
                mysql_host = config.getString("mysql_host");
                mysql_database = config.getString("mysql_database");
                mysql_username = config.getString("mysql_username");
                mysql_password = config.getString("mysql_password");
            }
        }
        if(!load){
            logger.warning(prefix+"mysql配置错误?已自动使用local模式");
            data_type = "local";
        }
        return true;
    }

    /**
     * 加载config.yml里的rewards配置
     * @param section 配置文件对象
     * @return 是否加载成功
     * */
    private static boolean loadRewards(ConfigurationSection section){

        /*
        * TODO 代码完善
        * */

        Set<String> keys = section.getKeys(false);

        for (String key : keys) {
            ConfigurationSection reward = section.getConfigurationSection(key);
        }

        return true;
    }
}
