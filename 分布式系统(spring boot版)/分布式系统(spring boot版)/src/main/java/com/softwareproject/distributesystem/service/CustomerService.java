package com.softwareproject.distributesystem.service;

import com.softwareproject.distributesystem.info.BillInfo;
import com.softwareproject.distributesystem.info.ChartInfo;
import com.softwareproject.distributesystem.info.CustomerInfo;
import com.softwareproject.distributesystem.info.RoomInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CustomerService {

    // 当前入住的用户的信息 弃用
    // public static Map<String, CustomerInfo> customerInfoMap = new ConcurrentHashMap<String, CustomerInfo>();

    // 所有房间的信息表 (新版)
    public static List<RoomInfo> roomInfo = new ArrayList<RoomInfo>();
    public static List<ChartInfo> chartInfo = new ArrayList<ChartInfo>();


    // 找入住的用户信息
    public static CustomerInfo get_customer_info_from_room_info(String userName) {
        CustomerInfo info = null;
        for (int i = 0; i < CustomerService.roomInfo.size(); i++) {
            RoomInfo temp = CustomerService.roomInfo.get(i);
            if (null != temp.getInfo()) {
                if (temp.getInfo().getUserName().equals(userName)) {    // 客户有入住信息
                    info = temp.getInfo();
                    break;
                }
            }
        }
        return info;
    }

    public static ChartInfo get_chart_info_from_room_info(String userName) {
        int roomID = 0;
        for (int i = 0; i < CustomerService.roomInfo.size(); ++i) {
            RoomInfo temp = CustomerService.roomInfo.get(i);
            CustomerInfo customerInfo = temp.getInfo();
            if (null != customerInfo) {
                if (temp.getInfo().getUserName().equals(userName)) {
                    roomID = temp.getRoomId();
                }
            }
        }
        return CustomerService.chartInfo.get(roomID);
    }

    // 根据前端发送的数据计算并更新当前温度和费用
    // 并更新房间费油
    public String update_customer_info(int speed, int desTemp, int mode, CustomerInfo info, ChartInfo chartInfo) {
        int lastDesTemp = info.getDesTemp();
        int lastSpeed = info.getCurrentSpeed();
        int lastMode = info.getMode();
//        int schedule = 0;
        if (lastDesTemp != desTemp || lastSpeed != speed || lastMode != mode) {
            chartInfo.setScheduleCnt(chartInfo.getScheduleCnt() + 1);
//            schedule = 1
        }


        int curTemp = info.getCurrentTemp();
        info.setMode(mode);
        info.setDesTemp(desTemp);
        info.setCurrentSpeed(speed);
        if(chartInfo.getMostUseSpeed().get(speed) == null)
        {
            chartInfo.getMostUseSpeed().put(speed,0);
        }
        int diff = chartInfo.getMostUseSpeed().get(speed) + 1;
        chartInfo.getMostUseSpeed().put(speed, diff);
        if (diff >= chartInfo.getMostSpeedCnt()) {
            chartInfo.setMostSpeed(speed);
            chartInfo.setMostTempCnt(chartInfo.getMostSpeedCnt() + 1);
        }

        if(chartInfo.getMostUseTemp().get(desTemp) == null)
        {
            chartInfo.getMostUseTemp().put(desTemp,0);
        }
        diff = chartInfo.getMostUseTemp().get(desTemp) + 1;
        chartInfo.getMostUseTemp().put(desTemp, diff);
        if (diff >= chartInfo.getMostTempCnt()) {
            chartInfo.setMostTemp(desTemp);
            chartInfo.setMostTempCnt(chartInfo.getMostTempCnt() + 1);
        }

        if (0 == speed) {   // 空调没开
            if (chartInfo.getOpenFlag() != 0) {
                chartInfo.setOpenCnt(chartInfo.getOpenCnt() + 1);
                chartInfo.setOpenFlag(0);
            }
        } else {
            chartInfo.setOpenFlag(1);
            if (CustomerInfo.COLD_MODE == mode) {      // 制冷
                if (curTemp <= desTemp) {
                    info.setDesTemp(info.getCurrentTemp());
//                    info.setCurrentSpeed(0);
                    chartInfo.setAchieveTempTime(chartInfo.getAchieveTempTime() + 1);
                } else {
                    int differ = curTemp - desTemp;
                    int degree = differ >= 10 ? 3 : differ >= 5 ? 2 : 1;
                    info.setCurrentTemp(curTemp - speed);
                    if (curTemp - speed < desTemp) {
                        info.setCurrentTemp(desTemp);
                    }
                    double cost = (0.1 * degree - 0.05) * (differ) + speed * 0.05;
                    info.setCurrentCost(info.getCurrentCost() + cost);
                    chartInfo.setCost(chartInfo.getCost() + cost);
                }
            } else {                                  // 制热
                if (curTemp >= desTemp) {
                    info.setDesTemp(info.getCurrentTemp());
//                    info.setCurrentSpeed(0);
                    chartInfo.setAchieveTempTime(chartInfo.getAchieveTempTime() + 1);
                } else {
                    int differ = desTemp - curTemp;
                    int degree = differ >= 10 ? 3 : differ >= 5 ? 2 : 1;
                    info.setCurrentTemp(curTemp + speed);
                    if (curTemp + speed > desTemp) {
                        info.setCurrentTemp(desTemp);
                    }
                    double cost = (0.1 * degree - 0.05) * (differ) + speed * 0.05;
                    info.setCurrentCost(info.getCurrentCost() + cost);
                    chartInfo.setCost(chartInfo.getCost() + cost);
                }
            }

        }

        return "{\"currentTemp\": " + String.valueOf(info.getCurrentTemp()) + ", \"cost\":" +
                String.valueOf(info.getCurrentCost()) + "}";
    }

    // 用户退房
    public boolean customer_check_out(String userName) {
        for (int i = 0; i < CustomerService.roomInfo.size(); i++) {
            RoomInfo temp = CustomerService.roomInfo.get(i);
            if (null != temp.getInfo()) {
                if (temp.getInfo().getUserName().equals(userName)) {    // 客户有入住信息
                    temp.setInfo(null);
                    return true;
                }
            }
        }
        return false;
    }

    // 账单信息
    public static List<BillInfo> billInfoList = new ArrayList<BillInfo>();
}
