package cn.ricetofu.task;

import cn.ricetofu.task.config.Config;
import cn.ricetofu.task.config.Message;
import cn.ricetofu.task.core.data.LocalDataManager;
import cn.ricetofu.task.core.data.MysqlDataManager;
import cn.ricetofu.task.core.task.TaskLoader;
import cn.ricetofu.task.core.task.TaskManager;
import cn.ricetofu.task.events.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;


/**
 * @Author: RiceTofu123
 * @Date: 2023-01-25
 * @Discription: 插件类，配置文件的加载和事件的注册从这里开始
 * */
public final class TofuDailyTask extends JavaPlugin {

    public static final String prefix = "§a[§bTofuDailyTask§a]§f:";//命令行输出前缀

    public static File dataFolder;//插件配置文件夹根目录

    public static final Logger logger = Bukkit.getLogger();//日志记录对象


    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();//用来计算插件加载耗时的
        logger.info(prefix+"欢迎使用:TofuDailyTask 2.0");

        //一些默认文件夹的创建操作等……
        saveDefaultConfig();//保存默认的config.yml配置文件
        dataFolder = getDataFolder();//目录根



        //加载config配置文件
        logger.info(prefix+"正在加载: config.yml");
        if (!Config.init(YamlConfiguration.loadConfiguration(new File(dataFolder,"config.yml")))) {
            //加载失败则退出
            logger.severe(prefix+"config.yml加载失败");
            Bukkit.getPluginManager().disablePlugin(this);//卸载插件
            return;
        }
        logger.info(prefix+"config.yml加载成功");

        //加载消息文件
        logger.info(prefix+"正在加载:"+Config.getLang_file());
        if (!Message.init(YamlConfiguration.loadConfiguration(new File(dataFolder, Config.getLang_file())))) {
            //加载失败则退出
            logger.severe(prefix+Config.getLang_file()+"加载失败");
            Bukkit.getPluginManager().disablePlugin(this);//卸载插件
            return;
        }
        logger.info(prefix+Config.getLang_file()+"加载成功");

        //初始化数据管理器和任务管理器
        logger.info(prefix+"初始化任务管理器和数据管理器");
        if(Config.getData_type().equals("mysql")){
            logger.info(prefix+"检测到mysql配置,尝试初始化数据库服务");
            MysqlDataManager mysqlDataManager = new MysqlDataManager();
            if(mysqlDataManager.init()){
                logger.info(prefix+"数据库服务初始成功,使用数据库数据模式");
                TaskManager.init(mysqlDataManager);
            }else {
                logger.warning(prefix+"数据库服务初始失败,默认使用本地数据模式");
                LocalDataManager localDataManager = new LocalDataManager();
                localDataManager.init();
                TaskManager.init(localDataManager);
            }
        }else {
            //使用本地数据模式
            logger.info(prefix+"使用本地数据模式");
            LocalDataManager localDataManager = new LocalDataManager();
            localDataManager.init();
            TaskManager.init(localDataManager);
        }
        logger.info(prefix+"初始化成功");

        //加载任务文件
        logger.info(prefix+"正在加载:"+Config.getTask_dir()+"下的文件");
        if(!TaskLoader.loadAll(new File(dataFolder,Config.getTask_dir()))){
            //加载失败则退出
            logger.severe(prefix+Config.getTask_dir()+"加载失败");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        logger.info(prefix+Config.getTask_dir()+"加载成功");


        //进行事件注册
        logger.info(prefix+"进行事件和指令的注册");
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(),this);//玩家加入事件
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(),this);//玩家退出事件
        //任务事件
        Bukkit.getPluginManager().registerEvents(new BreakListener(),this);//方块破坏事件(方块破坏任务)
        Bukkit.getPluginManager().registerEvents(new GetListener(),this);//掉落物拾取事件(物品获取任务)
        Bukkit.getPluginManager().registerEvents(new ThrowListener(),this);//玩家扔出物品时间(物品获取任务，用于防止自放自拾的刷任务行为)

        //进行指令注册
        // TODO 代码



        logger.info(prefix+"插件加载成功,耗时:"+(System.currentTimeMillis()-start)+"ms");
    }

    @Override
    public void onDisable() {
        //玩家数据的保存操作



    }
}
