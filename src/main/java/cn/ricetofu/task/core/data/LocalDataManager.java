package cn.ricetofu.task.core.data;

import cn.ricetofu.task.TofuDailyTask;
import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-22
 * @Discription: 本地数据管理器
 * */
public class LocalDataManager implements DataManager {

    //玩家数据内存缓存表
    private final Map<String,PlayerData> playerDataMap = new HashMap<>();

    //json解析对象
    private final Gson json = new Gson();

    //玩家数据文件夹
    private final File dir = new File(TofuDailyTask.dataFolder,"/data");

    @Override
    public boolean init() {
        //玩家数据文件夹创建
        if(!dir.exists())dir.mkdirs();//不存在则创建文件夹
        return true;
    }

    @Override
    public Map<String, PlayerData> get() {
        return null;
    }

    @Override
    public PlayerData getOne(String player_id) {
        PlayerData playerData = playerDataMap.get(player_id);
        //内存中有数据则直接返回
        if(playerData!=null)return playerData;
        //从本地数据文件中读取
        File player_data = new File(dir,player_id+".json");
        if(!player_data.exists()){
            //玩家数据不存在，则代表该玩家是个新用户玩家
            playerData = new PlayerData();
            playerData.setUuid(player_id);
        }else {
            try {
                //FileReader fileReader = new FileReader(player_data);
                InputStreamReader reader = new InputStreamReader(new FileInputStream(player_data), StandardCharsets.UTF_8);
                playerData = json.fromJson(reader, PlayerData.class);
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //数据存在内存里
        playerDataMap.put(player_id,playerData);
        return playerData;
    }

    @Override
    public boolean save() {
        return false;
    }

    @Override
    public boolean saveOne(String player_id,boolean remove) {
        //从内存获取这个数据
        PlayerData playerData = playerDataMap.get(player_id);
        if(playerData==null)return false;//内存没有在这个数据？
        File player_data_file = new File(dir,player_id+".json");
        if(!player_data_file.exists()) {
            try {
                player_data_file.createNewFile();//文件不存在则创建一个
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String s = json.toJson(playerData);
        try {
            //BufferedWriter writer = new BufferedWriter(new FileWriter(player_data_file));
            OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(player_data_file.toPath()), StandardCharsets.UTF_8);
            writer.write(s);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(remove)playerDataMap.remove(player_id);//在内存中删除该玩家的信息
        return true;
    }

    @Override
    public boolean saveOne(PlayerData data) {
        //在内存中更新这个玩家数据
        playerDataMap.put(data.getUuid(),data);
        return saveOne(data.getUuid(),false);
    }
}
