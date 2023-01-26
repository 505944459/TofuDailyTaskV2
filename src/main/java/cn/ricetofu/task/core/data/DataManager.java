package cn.ricetofu.task.core.data;

import java.util.Map;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-21
 * @Discription: 数据管理接口
 * */
public interface DataManager {

    /**
     * 数据管理器的初始化方法，在每次启动/重启服务器时都会调用一次
     * @return 是否初始化成功
     * */
    boolean init();

    /**
     * 获取所有玩家的数据(不论是否在线)，这个一般会直接进行IO读取，可能会对服务器性能有较大影响
     * @return 获取到的数据表，可能会为null???
     * */
    Map<String, PlayerData> get();

    /**
     * 获取一个玩家的数据
     * @param player_id 玩家的uuid
     * @return 一个玩家的数据对象，如果不存在会新建一个返回
     * */
    PlayerData getOne(String player_id);

    /**
     * 保存目前所有的玩家数据
     * @return 是否成功
     * */
    boolean save();

    /**
     * 保存一个玩家的数据
     * @param player_id  玩家的uuid
     * @param remove 是否在内存中删除它
     * @return 是否成功
     * */
    boolean saveOne(String player_id,boolean remove);

    /**
     * 按照指定的数据对象保存一个玩家数据
     * @param data 玩家数据对象
     * @return 是否成功
     * */
    boolean saveOne(PlayerData data);
}
