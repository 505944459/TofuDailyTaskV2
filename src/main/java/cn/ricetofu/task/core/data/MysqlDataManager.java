package cn.ricetofu.task.core.data;

import cn.ricetofu.task.TofuDailyTask;
import cn.ricetofu.task.config.Config;
import cn.ricetofu.task.core.task.Task;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-28
 * @Discription: mysql数据管理器
 * */
public class MysqlDataManager implements DataManager{
    //日志记录对象
    private static Logger logger = Bukkit.getLogger();
    //控制台输出前缀
    private static String prefix = TofuDailyTask.prefix;
    //玩家数据内存缓存表
    private final Map<String,PlayerData> playerDataMap = new HashMap<>();
    //json解析对象
    private static final Gson gson = new Gson();
    //连接对象
    private static Connection connection = null;

    @Override
    public boolean init() {
        //尝试连接mysql数据库
        try {
            Class.forName("com.mysql.jdbc.Driver");//驱动加载
        }catch (ClassNotFoundException e){
            logger.severe(prefix+"找不到MySQL驱动类!");
            return false;
        }

        //连接获取
        String url = "jdbc:mysql://" + Config.getMysql_host()+"/"+ Config.getMysql_database()+"?useSSL=false&characterEncoding=utf-8";
        String username = Config.getMysql_username();
        String password = Config.getMysql_password();
        try {
            connection = DriverManager.getConnection(url,username,password);
        } catch (SQLException e) {
            logger.severe(prefix+"连接到Mysql数据库时出现了错误，配置真的正确吗");
            return false;
        }
        //如果不存在数据表则需要创建一个
        // TODO 数据表创建
        //表创建(如果不存在)
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS `playerdailytaskdata`(\n" +
                    "  `player_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '玩家id',\n" +
                    "  `last_receive_date` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上次收到任务的时间',\n" +
                    "  `last_finished_date` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上次完成任务的时间',\n" +
                    "  `last_reward_date` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上次收到完成奖励的时间',\n" +
                    "  `finished_times` int(11) NULL DEFAULT NULL COMMENT '累计完成的次数',\n" +
                    "  `task` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '对象的json格式，包含完整数据',\n" +
                    "  PRIMARY KEY (`player_id`) USING BTREE\n" +
                    ") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;");
            statement.close();
        }catch (SQLException e){
            logger.severe(prefix+"数据库表创建失败");
            return false;
        }


        //保持Mysql连接的定时任务
        new BukkitRunnable(){
            @Override
            public void run() {
                try {
                    connection.createStatement().execute("SELECT 1");
                } catch (SQLException e) {
                    cancel();//取消这个任务
                    logger.log(Level.WARNING,prefix+"数据库连接貌似断掉了捏~~正在尝试重连……");
                    if(init()){
                        logger.log(Level.INFO,prefix+"数据库重连成功!");
                    }else {
                        logger.log(Level.SEVERE,prefix+"数据库重连失败!!!请检查，为确保运作，已经卸载本插件!!");
                        //插件卸载
                        Bukkit.getPluginManager().disablePlugin(TofuDailyTask.getProvidingPlugin(TofuDailyTask.class));
                    }
                }
            }
        }.runTaskTimerAsynchronously(TofuDailyTask.getProvidingPlugin(TofuDailyTask.class),60*20,60*20);
        return true;
    }

    @Override
    public Map<String, PlayerData> get() {
        return null;
    }

    @Override
    public PlayerData getOne(String player_id) {
        PlayerData playerData = playerDataMap.get(player_id);
        if(playerData!=null)return playerData;//表中有数据则直接返回
        //判断数据库有没有数据，没有数据的话就要插入一条记录咯
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("SELECT * from playerdailytaskdata where player_id = ?");
            statement.setString(1,player_id);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()){
                //没有玩家数据，新建记录
                connection.createStatement().execute("INSERT INTO " +
                        "playerdailytaskdata(`PLAYER_ID`, `LAST_RECEIVE_DATE`, `LAST_FINISHED_DATE`, `LAST_REWARD_DATE`, `FINISHED_TIMES`, `TASK`) VALUES " +
                        "('"+player_id+"','2002-12-12','2002-12-12','2002-12-12',0,'')");
                playerData = new PlayerData();
                playerData.setTasks(null);
                playerData.setUuid(player_id);
                playerData.setFinished_times(0);
                playerData.setLast_receive_date("2002-12-12");
                playerData.setLast_finish_date("2002-12-12");
                playerData.setLast_reward_date("2002-12-12");
            }else {
                playerData = new PlayerData();
                playerData.setUuid(resultSet.getString(1));
                playerData.setLast_receive_date(resultSet.getString(2));
                playerData.setLast_finish_date(resultSet.getString(3));
                playerData.setLast_reward_date(resultSet.getString(4));
                playerData.setFinished_times(resultSet.getInt(5));
                String json_string = resultSet.getString(6);
                List<Task> tasks;
                tasks = gson.fromJson(json_string, new TypeToken<List<Task>>(){}.getType());
                playerData.setTasks(tasks);
            }
        } catch (SQLException e) {
            logger.severe(prefix+"查询玩家:"+player_id+"数据时出现异常");
        } finally {
            try {
                if (!statement.isClosed()) {
                    statement.close();
                }
            } catch (SQLException e) {
            }
        }
        playerDataMap.put(player_id,playerData);
        return playerData;
    }

    @Override
    public boolean save() {
        return false;
    }

    @Override
    public boolean saveOne(String player_id,boolean remove) {
        PlayerData playerData = playerDataMap.get(player_id);
        if(playerData==null)return false; //没有玩家数据如何保存?
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("UPDATE playerdailytaskdata set " +
                    "last_receive_date = ?," +
                    "last_finished_date = ?," +
                    "last_reward_date = ?," +
                    "finished_times = ?," +
                    "task = ? where player_id = ?");
            statement.setString(1,playerData.getLast_receive_date());
            statement.setString(2,playerData.getLast_finish_date());
            statement.setString(3,playerData.getLast_reward_date());
            statement.setInt(4,playerData.getFinished_times());
            String s = gson.toJson(playerData.getTasks());
            statement.setString(5, s);
            statement.setString(6,player_id);
            statement.execute();
        }catch (SQLException e){
            logger.severe(prefix+"保存玩家:"+player_id+"数据时出现异常");
        }finally {
            try {
                if (!statement.isClosed()) {
                    statement.close();
                }
            } catch (SQLException e) {
            }
        }
        if(remove)playerDataMap.remove(player_id);//删除内存缓存
        return true;
    }

    @Override
    public boolean saveOne(PlayerData data) {
        return false;
    }
}
