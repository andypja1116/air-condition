package com.softwareproject.distributesystem.info;

import lombok.Data;

@Data
public class CustomerInfo {

    // 模式 0 制冷 1 制热

    public static final int COLD_MODE = 0;

    public static final int HEAT_MODE = 1;

    private String userName;

    private int currentSpeed;

    private double currentCost;

    private int currentTemp;

    private int desTemp;

    private int mode;

    public CustomerInfo(String userName) {
        this.userName = userName;
        this.currentSpeed = 0;
        this.currentCost = 0.0;
        this.currentTemp = 30;
        this.desTemp = 26;
        this.mode = CustomerInfo.COLD_MODE;
    }
}
