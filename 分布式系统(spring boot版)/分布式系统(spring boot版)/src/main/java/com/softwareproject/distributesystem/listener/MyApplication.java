package com.softwareproject.distributesystem.listener;


import com.softwareproject.distributesystem.info.ChartInfo;
import com.softwareproject.distributesystem.info.RoomInfo;
import com.softwareproject.distributesystem.service.CustomerService;
import org.springframework.stereotype.Controller;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

@Controller
public class MyApplication implements ServletContextListener {

    // application 初始化时的操作
    @Override
    public void contextInitialized(ServletContextEvent event) {
        for (int i = 1; i <= 10; i++) {
            CustomerService.roomInfo.add(new RoomInfo(i));
            CustomerService.chartInfo.add(new ChartInfo(i));   // 创建报表信息
        }
    }
}
