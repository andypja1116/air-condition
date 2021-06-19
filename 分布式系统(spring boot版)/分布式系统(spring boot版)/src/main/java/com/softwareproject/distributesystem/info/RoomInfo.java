package com.softwareproject.distributesystem.info;

import lombok.Data;


// 房间信息
@Data
public class RoomInfo {

    private int roomId;

    // 聚合
    private CustomerInfo info;

    public RoomInfo(int roomId) {
        this.roomId = roomId;
        info = null;     // 初始化房间的时候没有入住信息
    }
}
