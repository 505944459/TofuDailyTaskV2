package cn.ricetofu.task.core.task.loader;


import cn.ricetofu.task.TofuDailyTask;
import cn.ricetofu.task.core.task.TaskManager;
import cn.ricetofu.task.core.task.config.TaskInfo;
import cn.ricetofu.task.core.task.TaskLoader;
import com.google.gson.Gson;
import org.bukkit.Bukkit;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-22
 * @Discription: json任务加载器
 * */
public class JsonTaskLoader implements TaskLoader {

    private Logger logger = Bukkit.getLogger();
    private String prefix = TofuDailyTask.prefix;
    private Gson json = new Gson();

    @Override
    public boolean load(File file) {
        //文件不存在
        if(!file.exists()){
            try {
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                InputStream in = TofuDailyTask.getPlugin(TofuDailyTask.class).getResource("task/tasks.json");
                byte[] buf = new byte[1024];
                int n;
                while ((n=in.read(buf))>0){
                    out.write(buf,0,n);
                    out.flush();
                }
                in.close();
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // json解析
        TaskInfo[] infos = null;
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);//呜呜呜，不这样设置编码会乱码捏~~(花了15分钟解决)
            infos = json.fromJson(reader, TaskInfo[].class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        if(infos==null)return false;

        //转换为链表
        LinkedList<TaskInfo> list = new LinkedList<>(Arrays.asList(infos));
        int total = list.size();
        int fail = 0;

        //进行任务的配置正确性判断
        for (int i = 0; i < list.size(); i++) {
            if(!isRightTaskInfo(list.get(i))){
                //任务配置不正确则删除
                list.remove(i);
                fail++;
                i--;
            }
        }

        for (TaskInfo taskInfo : list) {
            TaskManager.getAllTaskInfoMap().put(taskInfo.getId(),taskInfo);//将任务添加入表
        }

        logger.info(prefix+"在:"+file.getName()+"中加载成功了:"+(total-fail)+"个任务,失败了:"+fail+"个");
        return true;
    }
}
