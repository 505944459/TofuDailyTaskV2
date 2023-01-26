package cn.ricetofu.task.core.task;


import cn.ricetofu.task.TofuDailyTask;
import cn.ricetofu.task.core.task.config.TaskInfo;
import cn.ricetofu.task.core.task.loader.JsonTaskLoader;
import cn.ricetofu.task.core.task.loader.YamlTaskLoader;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.HashMap;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-21
 * @Discription: 任务加载器接口
 * */
public interface TaskLoader {

    // 所有的任务加载器类的对象保存， k:支持解析的后缀名 v: 加载器对象
    HashMap<String,TaskLoader> loaders = new HashMap<>();

    /**
     * 加载一个文件夹下面的所有任务配置文件
     * @param file 任务问价所存在的文件夹
     * @return 是否加载成功
     * */
    static boolean loadAll(File file){

        //得到文件夹下面的所有文件
        File[] files = file.listFiles();

        if(files==null||files.length==0){
            Bukkit.getLogger().warning(TofuDailyTask.prefix+"指定文件夹下没有找到任务文本文件");
            return true;
        }

        for (File f : files) {
            //如果不是文件夹才继续判断
            if(f.isDirectory())continue;
            //获取该文件的后缀名
            String name = f.getName();
            int i = name.lastIndexOf(".");
            String last_name = name.substring(i);
            if(last_name.equals(".json")){
                new JsonTaskLoader().load(f);
            }else if(last_name.equals(".yml")){
                new YamlTaskLoader().load(f);
            }else {
                //无法解析的文件格式
                Bukkit.getLogger().warning(TofuDailyTask.prefix+"无法解析文本文档:"+name+",原因: 不支持的格式");
            }
        }
        return true;
    }


    /**
     * 加载一个指定任务文件夹中的任务
     * @param file 任务文件对象
     * @return 是否加载成功，如果文件不存在或者yml格式错误可能会导致返回false呢，任务参数错误并不会导致返回false
     * */
    boolean load(File file);

    /**
     * 判断一个任务信息是否是正确的
     * @param taskInfo 任务信息
     * @return 是否是正确的
     * */
    default boolean isRightTaskInfo(TaskInfo taskInfo){
        // TODO 代码
        switch (taskInfo.getType()){
            case "break":{


                break;
            }

        }

        return true;
    }
}
