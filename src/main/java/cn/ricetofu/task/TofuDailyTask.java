package cn.ricetofu.task;

import cn.ricetofu.task.command.Get;
import cn.ricetofu.task.command.List;
import cn.ricetofu.task.command.Reward;
import cn.ricetofu.task.command.Task;
import cn.ricetofu.task.config.Config;
import cn.ricetofu.task.config.Message;
import cn.ricetofu.task.core.data.LocalDataManager;
import cn.ricetofu.task.core.data.MysqlDataManager;
import cn.ricetofu.task.core.gui.DefaultGUI;
import cn.ricetofu.task.core.task.TaskLoader;
import cn.ricetofu.task.core.task.TaskManager;
import cn.ricetofu.task.events.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
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

    private static Timer timer;//刷新每日任务的定时任务

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();//用来计算插件加载耗时的
        logger.info(prefix+"欢迎使用:TofuDailyTask 2.0");

        //一些默认文件夹的创建操作等……
        saveDefaultConfig();//保存默认的config.yml配置文件
        dataFolder = getDataFolder();//目录根
        //数据文件夹
        File data = new File(dataFolder, "data");
        if(!data.exists())data.mkdirs();
        //任务文件夹
        File task = new File(dataFolder,"task");
        if(!task.exists()) {
            task.mkdirs();
            //输出默认的任务文件
            InputStream in = getResource("tasks.yml");
            File file = new File(task, "tasks.yml");
            try {
                if(!file.exists())file.createNewFile();
            }catch (Exception e){};
            try {
                FileOutputStream out = new FileOutputStream(file);
                byte[] buf = new byte[1024*10];
                int n;
                while ((n=in.read(buf))>0){
                    out.write(buf,0,n);
                }
                out.close();
                in.close();
            } catch (FileNotFoundException e) {} catch (IOException e) {}
        }
        //lang文件夹
        File lang = new File(dataFolder,"lang");
        if(!lang.exists()) {
            lang.mkdirs();
            //输出中文文件
            InputStream in = getResource("chinese-simple.yml");
            File file = new File(lang, "chinese-simple.yml");
            try {
                if(!file.exists())file.createNewFile();
            }catch (Exception e){};
            try {
                FileOutputStream out = new FileOutputStream(file);
                byte[] buf = new byte[1024*10];
                int n;
                while ((n=in.read(buf))>0){
                    out.write(buf,0,n);
                }
                out.close();
                in.close();
            } catch (FileNotFoundException e) {} catch (IOException e) {}
        }



        //加载config配置文件
        if (!Config.init(YamlConfiguration.loadConfiguration(new File(dataFolder,"config.yml")))) {
            //加载失败则退出
            logger.severe(prefix+"config.yml加载失败");
            Bukkit.getPluginManager().disablePlugin(this);//卸载插件
            return;
        }

        //加载消息文件
        if (!Message.init(YamlConfiguration.loadConfiguration(new File(dataFolder, Config.getLang_file())))) {
            //加载失败则退出
            logger.severe(prefix+Config.getLang_file()+"加载失败");
            Bukkit.getPluginManager().disablePlugin(this);//卸载插件
            return;
        }

        //初始化数据管理器和任务管理器
        logger.info(prefix+"初始化任务和玩家数据");
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
        //普通事件
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(),this);//玩家加入事件
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(),this);//玩家退出事件
        //任务事件
        Bukkit.getPluginManager().registerEvents(new BreakListener(),this);//方块破坏事件(方块破坏任务)
        Bukkit.getPluginManager().registerEvents(new GetListener(),this);//掉落物拾取事件(物品获取任务)
        Bukkit.getPluginManager().registerEvents(new PlaceListener(),this);//物品放置事件(物品放置任务)
        Bukkit.getPluginManager().registerEvents(new ThrowListener(),this);//玩家扔出物品事件(物品获取任务，用于防止自放自拾的刷任务行为)
        Bukkit.getPluginManager().registerEvents(new EntityDeathListener(),this);//实体死亡事件(实体击杀任务)
        if(Bukkit.getPluginManager().getPlugin("MythicMobs")!=null){
            //存在mm插件，开启对mm实体的特殊支持
            logger.info(prefix+"检测到MythicMobs插件,成功支持mm实体");
            Bukkit.getPluginManager().registerEvents(new MythicMobDeathListener(),this);
        }
        Bukkit.getPluginManager().registerEvents(new CraftListener(),this);//物品合成事件(物品合成任务)
        Bukkit.getPluginManager().registerEvents(new EatListener(),this);//物品使用事件(物品使用任务)
        //GUI
        Bukkit.getPluginManager().registerEvents(new DefaultGUI(),this);//GUI相关事件监听

        //进行指令注册
        Bukkit.getPluginCommand("task").setExecutor(new Task());//task指令，用于打开一个GUI
        Bukkit.getPluginCommand("task list").setExecutor(new List());//list指令，用于显示任务列表
        Bukkit.getPluginCommand("task get").setExecutor(new Get());//get指令，用于获取一个每日任务
        Bukkit.getPluginCommand("task reward").setExecutor(new Reward());//reward指令，用于获取一次每日任务奖励

        //进行权限注册


        //刷新每日任务的定时任务
        Date today = null;
        try {
            today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date())+" 00:41:00");
            today = new Date(today.getTime() + 1000*60*60*24);
        } catch (ParseException e) {
            logger.severe(prefix+"定时任务的时间解析出现了一个错误!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                long l = System.currentTimeMillis();
                logger.info(prefix+"开始每日任务刷洗……");
                //一个一个玩家来，步骤为先删除数据，再重新获取一次
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                for (Player player : players) {
                    if(player.isOnline()){
                        TaskManager.savePlayerDataByIdAndRemoveCache(player.getUniqueId().toString());//保存并删除玩家数据
                        TaskManager.getDailyTask(player.getUniqueId().toString());//重新获取一个玩家的数据(重新接取每日任务)
                    }
                }
                logger.info(prefix+"刷新完成,耗时:"+(System.currentTimeMillis()-l)+"ms");
            }
        };

        timer = new Timer();
        timer.schedule(timerTask,today.getTime() - new Date().getTime() + 1000*2,1000*60*60*24);//每日凌晨刷新，间隔24h


        logger.info(prefix+"插件加载成功,耗时:"+(System.currentTimeMillis()-start)+"ms");
    }

    @Override
    public void onDisable() {
        //定时任务关闭
        timer.cancel();
        //玩家数据的保存操作
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) TaskManager.savePlayerDataByIdAndRemoveCache(onlinePlayer.getUniqueId().toString());
        logger.info(prefix+"插件卸载成功~");
    }
}
