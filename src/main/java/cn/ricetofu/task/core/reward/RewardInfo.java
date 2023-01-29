package cn.ricetofu.task.core.reward;

import lombok.Data;

import java.util.List;


/**
 * @Author: RiceTofu123
 * @Date: 2023-01-29
 * @Discription: 奖励实体类
 * */
@Data
public class RewardInfo {

    private String id;

    private String name;

    private String message;

    private String command;

    private List<String> random;
}
