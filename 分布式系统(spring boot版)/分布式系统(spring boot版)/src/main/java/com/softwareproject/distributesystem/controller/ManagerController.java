package com.softwareproject.distributesystem.controller;

import com.softwareproject.distributesystem.info.BillInfo;
import com.softwareproject.distributesystem.info.ChartInfo;
import com.softwareproject.distributesystem.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ManagerController {

    @ResponseBody
    @PostMapping("/updateBill")
    public List<ChartInfo> updateBill() {
        return CustomerService.chartInfo;
    }
}
