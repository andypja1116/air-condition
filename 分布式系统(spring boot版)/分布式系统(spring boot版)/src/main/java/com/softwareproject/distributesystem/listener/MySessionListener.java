package com.softwareproject.distributesystem.listener;

import com.softwareproject.distributesystem.controller.LoginController;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@Controller
public class MySessionListener implements HttpSessionListener {

    @Override
    public synchronized void sessionCreated(HttpSessionEvent httpSessionEvent) {
        // 设置最大非活跃时间 单位秒 同时还需要设置session cookie 的有效时间（设置为同等长度） 才能生效
        httpSessionEvent.getSession().setMaxInactiveInterval(86400);
    }

    @Override
    public synchronized void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        HttpSession session = httpSessionEvent.getSession();
        Object ObjType = httpSessionEvent.getSession().getAttribute("type");
        if (ObjType != null) {                                             // 有登陆的
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
                String userName = (String)httpSessionEvent.getSession().getAttribute("userName");
                for (int i = 0; i < LoginController.usersOnline.size(); i++) {
                    String temp = LoginController.usersOnline.get(i);
                    if (temp.equals(userName)) {
                        LoginController.usersOnline.remove(temp);
                        break;
                    }
                }
            }
        }
    }
}
