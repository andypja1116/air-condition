package com.softwareproject.distributesystem.info;

import lombok.Data;

@Data
public class BillInfo {

    private String userName;

    private double cost;

    public BillInfo(String userName, double cost) {
        this.userName = userName;
        this.cost = cost;
    }
}
