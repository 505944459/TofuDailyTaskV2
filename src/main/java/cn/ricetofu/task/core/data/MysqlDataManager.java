package cn.ricetofu.task.core.data;

import java.util.Map;

public class MysqlDataManager implements DataManager{
    @Override
    public boolean init() {
        return false;
    }

    @Override
    public Map<String, PlayerData> get() {
        return null;
    }

    @Override
    public PlayerData getOne(String player_id) {
        return null;
    }

    @Override
    public boolean save() {
        return false;
    }

    @Override
    public boolean saveOne(String player_id,boolean remove) {
        return false;
    }

    @Override
    public boolean saveOne(PlayerData data) {
        return false;
    }
}
