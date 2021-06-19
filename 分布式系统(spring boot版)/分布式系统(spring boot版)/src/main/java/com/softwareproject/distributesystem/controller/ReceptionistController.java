package com.softwareproject.distributesystem.controller;

import com.softwareproject.distributesystem.info.BillInfo;
import com.softwareproject.distributesystem.info.ChartInfo;
import com.softwareproject.distributesystem.info.CustomerInfo;
import com.softwareproject.distributesystem.info.RoomInfo;
import com.softwareproject.distributesystem.server.WebSocketServer;
import com.softwareproject.distributesystem.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class ReceptionistController {

    @Autowired
    CustomerService customerService;

    // 同意用户入住
    @ResponseBody
    @PostMapping("/confirmCheckIn")
    public String confirm_check_in(@RequestParam(name="customerName")String customerName,
                                   @RequestParam(name="roomId")int roomId) {
        // 先找webSocket
        WebSocketServer customerServer = WebSocketServer.webSocketMap.get(customerName);
        if (null == customerServer) {    // 不在线
            return "failed";
        }
        else {                           // 在线
            // 先看看是不是占用了
            RoomInfo target = null;
            for (int i = 0; i < CustomerService.roomInfo.size(); i ++) {
                RoomInfo temp = CustomerService.roomInfo.get(i);
                if (roomId == temp.getRoomId()) {    // 是目标房间
                    if (null == temp.getInfo()) {    // 空房间
                        target = temp;
                        break;
                    }
                    else {                           // 已经占用
                        return "occupied";
                    }
                }
            }

            // 发确认消息
            String Info = "{\"type\":\"confirmCheckIN\", \"roomId\":" + String.valueOf(roomId) + "}";
            try {
                customerServer.send_message(Info);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 信息添加到内存中
            CustomerInfo  info = new CustomerInfo(customerName);
            target.setInfo(info);
            System.out.println("添加成功");


            // CustomerService.customerInfoMap.put(customerName, new CustomerInfo(customerName));
            return "ok";
        }
    }

    // 更新用户信息
    @ResponseBody
    @PostMapping("/updateCustomerList")
    /*
    public List<CustomerInfo> update_customer_list() {
        return new ArrayList<CustomerInfo>(CustomerService.customerInfoMap.values());
    }
    */
    public List<RoomInfo> update_customer_list() {
        return CustomerService.roomInfo;
    }


    // 结算
    @ResponseBody
    @PostMapping("/settleAccount")
    public String settle_account(@RequestParam(name="customerName")String customerName) {

        CustomerInfo info = CustomerService.get_customer_info_from_room_info(customerName);
        ChartInfo chartInfo = CustomerService.get_chart_info_from_room_info(customerName);
        chartInfo.setBillCnt(chartInfo.getBillCnt() + 1);
        // 记录账单信息
        CustomerService.billInfoList.add(new BillInfo(customerName, info.getCurrentCost()));
        // 通知相关的客户端
        WebSocketServer customer_server = WebSocketServer.webSocketMap.get(customerName);
        if (null == customer_server) {
            return "error";
        }
        else {
            String Info = "{\"type\":\"confirmSettleAccount\"}";
            try {
                customer_server.send_message(Info);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        // 如果经理在线需要通知经理
        WebSocketServer manager_server = WebSocketServer.webSocketMap.get("manager");
        if (null != manager_server) {
            String Info = "{\"type\":\"updateBill\"}";
            try {
                manager_server.send_message(Info);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 从在线用户中移除相关信息
        customerService.customer_check_out(customerName);
        return "ok";
    }

}
