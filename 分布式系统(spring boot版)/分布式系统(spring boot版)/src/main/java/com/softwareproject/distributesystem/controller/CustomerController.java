package com.softwareproject.distributesystem.controller;

import com.softwareproject.distributesystem.info.BillInfo;
import com.softwareproject.distributesystem.info.ChartInfo;
import com.softwareproject.distributesystem.info.CustomerInfo;
import com.softwareproject.distributesystem.server.WebSocketServer;
import com.softwareproject.distributesystem.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class CustomerController {

    @Autowired
    CustomerService customerService;

    // 客户发出入住请求
    @ResponseBody
    @PostMapping("/checkIn")
    public String check_in(@RequestParam(name = "userName") String userName) {
        // 给 receptionist 发请求
        WebSocketServer receptionistServer = WebSocketServer.webSocketMap.get("receptionist");
        if (null == receptionistServer) {  // 前台不在线
            return "failed";
        } else {
            // 发送请求
            String Info = "{\"type\":\"checkIn\", \"user\":\"" + userName + "\"}";   // 提示:发送给前端的字符串如果表示json格式数据, 键名必须用双引号, 值如果是字符串，也必须用双引号
            try {
                receptionistServer.send_message(Info);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "ok";
        }
    }

    // 客户入住后将session的状态更新
    @ResponseBody
    @PostMapping("/updateCheckIn")
    public String update_check_in(HttpServletRequest request, @RequestParam(name="roomId")int roomId) {
        request.getSession().setAttribute("state", LoginController.USER_CHECKED_IN);
        request.getSession().setAttribute("roomId", roomId);
        return "ok";
    }

    @ResponseBody
    @PostMapping("/sendWindSpeedInfo")
    public String send_wind_speed_info(HttpServletRequest request, @RequestParam(name="speed") int speed,
                                       @RequestParam(name="destTemp") int desTemp, @RequestParam(name="mode") int mode) {
        String userName = (String) request.getSession().getAttribute("userName");
        CustomerInfo info = CustomerService.get_customer_info_from_room_info(userName);
        ChartInfo chartInfo = CustomerService.get_chart_info_from_room_info(userName);
        if (null == info) {     // 退房了
            return "{\"currentTemp\": 30, \"cost\": 0.0}";
        } else {                  // 没退房
            return customerService.update_customer_info(speed, desTemp, mode, info, chartInfo);
        }

        /*
        if (CustomerService.customerInfoMap.containsKey(userName)) { // 没退房
            CustomerInfo info = CustomerService.customerInfoMap.get(userName);
            info.setCurrentSpeed(speed);
            info.setCurrentCost(info.getCurrentCost() + 0.05 * speed);
            return info.getCurrentCost();
        } else {                                                       // 已经退房了
            return 0.0;
        }
         */
    }
    /*
    // new func
    // 客户定时发送温度信息
    @ResponseBody
    @PostMapping("/sendTempInfo")
    public double send_temp_info(HttpServletRequest request, @RequestParam(name = "temp") int temp) {
        String userName = (String) request.getSession().getAttribute("userName");
        if (CustomerService.customerInfoMap.containsKey(userName)) { // 没退房
            CustomerInfo info = CustomerService.customerInfoMap.get(userName);
            info.setDesTemp(temp);
            int curTemp = info.getCurrentTemp();
            int desTemp = info.getDesTemp();
            if (info.getMode() == 0) {//制冷
                if (curTemp < desTemp) {
                    info.setDesTemp(info.getCurrentTemp());
                    info.setCurrentCost(info.getCurrentCost());
                    info.setCurrentSpeed(0);
                } else if ((curTemp - desTemp) < 5) {
                    info.setDesTemp(curTemp - 1);
                    info.setCurrentCost(info.getCurrentCost() + 0.05 * (info.getCurrentTemp()) - temp);
                } else if ((curTemp - desTemp) < 10) {
                    info.setDesTemp(curTemp - 2);
                    info.setCurrentCost(info.getCurrentCost() + 0.15 * (info.getCurrentTemp()) - temp);
                } else {
                    info.setDesTemp(curTemp - 3);
                    info.setCurrentCost(info.getCurrentCost() + 0.25 * (info.getCurrentTemp()) - temp);
                }
            } else {//制热
                if (curTemp > desTemp) {
                    info.setDesTemp(info.getCurrentTemp());
                    info.setCurrentCost(info.getCurrentCost());
                    info.setCurrentSpeed(0);
                } else if ((desTemp - curTemp) < 5) {
                    info.setDesTemp(curTemp + 1);
                    info.setCurrentCost(info.getCurrentCost() - 0.05 * (info.getCurrentTemp()) - temp);
                } else if ((desTemp - curTemp) < 10) {
                    info.setDesTemp(curTemp + 2);
                    info.setCurrentCost(info.getCurrentCost() - 0.15 * (info.getCurrentTemp()) - temp);
                } else {
                    info.setDesTemp(curTemp + 3);
                    info.setCurrentCost(info.getCurrentCost() - 0.25 * (info.getCurrentTemp()) - temp);
                }
            }
            return info.getCurrentCost();
        } else {                                                       // 已经退房了
            return 0.0;
        }
    }
     */

    /*
    // new func
    // 客户定时发送模式信息
    @ResponseBody
    @PostMapping("/sendModeInfo")
    public double send_mode_info(HttpServletRequest request, @RequestParam(name = "speed") int mode) {
        String userName = (String) request.getSession().getAttribute("userName");
        if (CustomerService.customerInfoMap.containsKey(userName)) { // 没退房
            CustomerInfo info = CustomerService.customerInfoMap.get(userName);
            info.setMode(mode);
        }
        return 0.0;
    }
     */


    // 客户确认退房更新状态
    @ResponseBody
    @PostMapping("/updateCheckOut")
    public String confirm_settle_account(HttpServletRequest request) {
        request.getSession().setAttribute("state", LoginController.USER_UNCHECKED_IN);
        request.getSession().removeAttribute("roomId");
        return "ok";
    }

    // 客户主动退房
    @ResponseBody
    @PostMapping("/customerCheckout")
    public String customer_checkout(HttpServletRequest request) {
        String userName = (String)request.getSession().getAttribute("userName");
        ChartInfo chartInfo = CustomerService.get_chart_info_from_room_info(userName);
        chartInfo.setBillCnt(chartInfo.getBillCnt() + 1);

        CustomerInfo info = CustomerService.get_customer_info_from_room_info(userName);
        // 记录账单信息
        CustomerService.billInfoList.add(new BillInfo(userName, info.getCurrentCost()));

        customerService.customer_check_out(userName);

        request.getSession().setAttribute("state", LoginController.USER_UNCHECKED_IN);
        request.getSession().removeAttribute("roomId");
        return "ok";
    }
}
