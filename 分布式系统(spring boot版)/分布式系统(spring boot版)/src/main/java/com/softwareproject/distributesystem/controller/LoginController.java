package com.softwareproject.distributesystem.controller;

import com.softwareproject.distributesystem.info.CustomerInfo;
import com.softwareproject.distributesystem.info.RoomInfo;
import com.softwareproject.distributesystem.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LoginController {

    // 这三种管理员只能登录一个, 所以需要一个标识符
    public static boolean is_manager_logged = false;
    public static boolean is_receptionist_logged = false;
    public static boolean is_controller_logged = false;

    // 三种管理员的密码(因为就一个管理员，所以密码直接放在程序中了)
    private static final String MANAGER_PASSWORD = "manager";
    private static final String RECEPTIONIST_PASSWORD = "receptionist";
    private static final String CONTROLLER_PASSWORD = "controller";

    // 类型标识
    public static final int MANAGER_TYPE = 0;
    public static final int RECEPTIONIST_TYPE = 1;
    public static final int CONTROLLER_TYPE = 2;
    public static final int CUSTOMER_TYPE = 3;

    // 用户是否入住
    public static final int USER_UNCHECKED_IN = 0;
    public static final int USER_CHECKED_IN = 1;

    // 已经登录的用户的用户名
    public static List<String> usersOnline = new ArrayList<String>();

    @RequestMapping("/navigation")
    public String navigation(Model model, HttpServletRequest request) {
        Object type = request.getSession().getAttribute("type");
        if (null == type) {      // 未登录
            model.addAttribute("is_manger_logged", is_manager_logged);
            model.addAttribute("is_receptionist_logged", is_receptionist_logged);
            model.addAttribute("is_controller_logged", is_controller_logged);
            return "navigation";
        }
        else {                  // 登录了
            return "redirect:/main";
        }


    }

    // 主界面
    @RequestMapping("/main")
    public String get_main(HttpServletRequest request, Model model) {
        Object ObjType = request.getSession().getAttribute("type");
        if (null == ObjType) {                   // 没登录
            return "redirect:/navigation";
        }
        else if (LoginController.MANAGER_TYPE == (int)ObjType) {       // 经理
            return "managerMain";
        }
        else if (LoginController.RECEPTIONIST_TYPE == (int)ObjType) {  // 前台
            return "receptionistMain";
        }
        else if (LoginController.CONTROLLER_TYPE == (int)ObjType) {    // 管理员
            return "controllerMain";
        }
        else {                                                         // 客户
            String userName = (String)request.getSession().getAttribute("userName");
            CustomerInfo info = CustomerService.get_customer_info_from_room_info(userName);
            // 出来还是null 就是没有入住
            model.addAttribute("info", info);
            /*
            if (1 == (int)request.getSession().getAttribute("state")) {   // 入住了
                String userName = (String)request.getSession().getAttribute("userName");
                double cost = CustomerService.customerInfoMap.get(userName).getCurrentCost();
                model.addAttribute("current_cost", cost);
            }
            else {                                                     // 未入住
                model.addAttribute("current_cost", 0.0);
            }
             */
            return "customerMain";
        }
    }

    // 经理登录
    @ResponseBody
    @PostMapping("/loginManager")
    public String login_manager(HttpServletRequest request, @RequestParam(name="password")String password) {
        if (LoginController.MANAGER_PASSWORD.equals(password)) {     // 密码正确
            request.getSession().setAttribute("type", LoginController.MANAGER_TYPE);
            LoginController.is_manager_logged = true;
            return "ok";
        }
        else {              // 密码错误
            return "failed";
        }
    }

    // 前台登录
    @ResponseBody
    @PostMapping("/loginReceptionist")
    public String login_receptionist(HttpServletRequest request, @RequestParam(name="password")String password) {
        if (LoginController.RECEPTIONIST_PASSWORD.equals(password)) {    // 密码正确
            request.getSession().setAttribute("type", LoginController.RECEPTIONIST_TYPE);
            LoginController.is_receptionist_logged = true;
            return "ok";
        }
        else {
            return "failed";
        }
    }

    // 空调管理员登录
    @ResponseBody
    @PostMapping("/loginController")
    public String login_controller(HttpServletRequest request, @RequestParam(name="password")String password) {
        if (LoginController.CONTROLLER_PASSWORD.equals(password)) {
            request.getSession().setAttribute("type", LoginController.CONTROLLER_TYPE);
            LoginController.is_controller_logged = true;
            return "ok";
        }
        else {
            return "failed";
        }
    }

    // 客户登录
    @ResponseBody
    @PostMapping("/loginCustomer")
    public String login_customer(HttpServletRequest request, @RequestParam(name="userName")String userName) {
        if (usersOnline.contains(userName) && !"manager".equals(userName) && !"receptionist".equals(userName) &&
        !"controller".equals(userName)) {       // 如果重名
            return "failed";
        }
        else {
            request.getSession().setAttribute("userName", userName);
            request.getSession().setAttribute("type", LoginController.CUSTOMER_TYPE);
            request.getSession().setAttribute("state",LoginController.USER_UNCHECKED_IN);
            usersOnline.add(userName);
            return "ok";
        }
    }

    // 退出
    @ResponseBody
    @PostMapping("/logout")
    public String log_out(HttpServletRequest request) {
        // 还原相关标识
        Object ObjType = request.getSession().getAttribute("type");
        if (LoginController.MANAGER_TYPE == (int)ObjType) {            // 经理
            LoginController.is_manager_logged = false;
        }
        else if (LoginController.RECEPTIONIST_TYPE == (int)ObjType) {  // 前台
            LoginController.is_receptionist_logged = false;
        }
        else if (LoginController.CONTROLLER_TYPE == (int)ObjType) {    // 空调管理员
            LoginController.is_controller_logged = false;
        }
        else {
            String userName = (String)request.getSession().getAttribute("userName");
            for (int i = 0; i < usersOnline.size(); i++) {
                String temp = usersOnline.get(i);
                if (temp.equals(userName)) {
                    usersOnline.remove(temp);
                    break;
                }
            }
        }
        // 清空session中的所有信息
        request.getSession().invalidate();
        return "ok";
    }
}
