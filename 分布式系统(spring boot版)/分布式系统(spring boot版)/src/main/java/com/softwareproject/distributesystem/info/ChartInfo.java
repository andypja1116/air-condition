package com.softwareproject.distributesystem.info;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class ChartInfo {

    private int RoomID;//房间号

    private double cost;//总花费

    private int openFlag;//记录前一次空调开关状态

    private int openCnt;//开关次数

    private Map<Integer, Integer> mostUseTemp = new ConcurrentHashMap<Integer, Integer>();

    private Map<Integer, Integer> mostUseSpeed = new ConcurrentHashMap<Integer, Integer>();

    private int mostTemp;//最多使用温度

    private int mostTempCnt;

    private int mostSpeedCnt;

    private int mostSpeed;//最多使用风速

    private int achieveTempTime;//达到目标温度总时间

    private int scheduleCnt;//被调度次数

    private int billCnt;//多少人入住该房间

    public ChartInfo(int RoomId) {
        this.RoomID = RoomId;
        this.cost = 0;
        this.openFlag = 0;
        this.openCnt = 0;
        this.mostTemp = 0;
        this.mostTempCnt = 0;
        this.mostSpeedCnt = 0;
        this.mostSpeed = 0;
        this.achieveTempTime = 0;
        this.scheduleCnt = 0;
        this.billCnt = 0;
    }

}
