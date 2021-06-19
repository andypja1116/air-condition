package com.softwareproject.distributesystem.controller;

import com.softwareproject.distributesystem.info.CustomerInfo;
import com.softwareproject.distributesystem.info.RoomInfo;
import com.softwareproject.distributesystem.server.WebSocketServer;
import com.softwareproject.distributesystem.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
public class ControllsController {

/*
    @ResponseBody
    @PostMapping("/queryCustomerWindSpeed")
    List<CustomerInfo> query_customer_wind_speed() {
        return new ArrayList<CustomerInfo>(CustomerService.customerInfoMap.values());
    }
*/

    @ResponseBody
    @PostMapping("/queryCustomerInfo")
    List<RoomInfo> query_customer_temp() {
        return CustomerService.roomInfo;
    }



    @ResponseBody
    @PostMapping("/alterCustomerWindSpeed")
    String alter_customer_wind_speed(@RequestParam(name="userName")String userName, @RequestParam(name="targetWindSpeed")
                                     int windSpeed) {
        CustomerInfo info = CustomerService.get_customer_info_from_room_info(userName);
        if (null != info) {   // 还没退
            info.setCurrentSpeed(windSpeed);

            WebSocketServer customer_server = WebSocketServer.webSocketMap.get(userName);
            if (null == customer_server) {
                System.out.println("目标用户当前不在线");
            }
            else {
                String Info = "{\"type\":\"alterWindSpeed\", \"speed\":" + String.valueOf(windSpeed) + "}";
                try {
                    customer_server.send_message(Info);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "ok";
        }
        else {
            return "failed";
        }
    }

    @ResponseBody
    @PostMapping("/alterCustomerTemp")
    String alter_customer_temp(@RequestParam(name="userName")String userName, @RequestParam(name="destTemp")
            int temp) {
        CustomerInfo info = CustomerService.get_customer_info_from_room_info(userName);
        if (null != info) {   // 还没退
            info.setDesTemp(temp);

            WebSocketServer customer_server = WebSocketServer.webSocketMap.get(userName);
            if (null == customer_server) {
                System.out.println("目标用户当前不在线");
            }
            else {
                String Info = "{\"type\":\"alterTemp\", \"temp\":" + String.valueOf(temp) + "}";
                try {
                    customer_server.send_message(Info);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "ok";
        }
        else {
            return "failed";
        }
    }

}
