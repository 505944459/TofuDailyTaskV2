package cn.ricetofu.task.util;

import cn.ricetofu.task.TofuDailyTask;
import cn.ricetofu.task.core.Weighable;
import cn.ricetofu.task.core.task.config.TaskArgs;
import cn.ricetofu.task.core.task.config.TaskInfo;
import org.bukkit.Bukkit;

import java.util.*;

public class WeightShuffle {

    /**
     * 根据权重随机获取一个参数对象
     * @param list 需要随机的参数列表
     * @return 返回的列表
     * */
    public static TaskArgs getRandomArgsByWeight(List<TaskArgs> list){
        if(list==null||list.size()==0)return null;
        //结果列表
        TaskArgs result = null;
        List<TaskArgs> had = new LinkedList<>(list); //复制列表
        //先按照权重排序，权重越小的会越靠前
        had.sort(Comparator.comparingInt(Weighable::getWeight));
        int total = 0;//总共的权重和
        for (Weighable weighable : had) total += weighable.getWeight();//计算权重和

        int amount = 1;
        while (amount-->0){
            int i = new Random().nextInt(total)+1;//获取一个随机数
            int now = 0;//找寻该数匹配的任务
            for (int j = 0; j < had.size(); j++) {
                now += had.get(j).getWeight();
                if(now>=i){
                    result = had.get(j);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 随机获取指定个数的任务信息对象
     * @param list 原始信息列表
     * @param amount 数量
     * @return 返回的任务列表
     * */
    public static List<TaskInfo> getRandomTaskInfoByWeight(Collection<TaskInfo> list,int amount){
        if(list==null||list.size()==0||amount==0)return new ArrayList<>();
        //返回的结果表
        List<TaskInfo> result = new ArrayList<>();
        List<TaskInfo> had = new LinkedList<>(list);
        //排除没有开启的任务
        for (int i = 0; i < had.size(); i++) {
            if(!had.get(i).getEnable()){
                had.remove(i);
                i--;
            }
        }
        //amount参数校验
        if(amount>had.size()){
            Bukkit.getLogger().warning(TofuDailyTask.prefix+"有效的任务个数为:"+had.size()+",而配置的任务个数为:"+amount+",是否配置错误?");
            amount = had.size();
        }

        //先按照权重排序，权重越小的会越靠前
        had.sort(Comparator.comparingInt(Weighable::getWeight));
        int total = 0;//总共的权重和
        for (Weighable weighable : had) total += weighable.getWeight();//计算权重和
        while (amount-->0){
            int i = new Random().nextInt(total)+1;//获取随机数
            int now = 0;//找寻匹配任务
            for (int j = 0; j < had.size(); j++) {
                now += had.get(j).getWeight();
                if(now>=i){
                    result.add(had.get(j));
                    total-=had.get(j).getWeight();//更新总权重
                    had.remove(j);//删除这个元素
                    break;
                }
            }
        }
        return result;
    }

}
