package cn.stylefeng.guns.core.interceptor;

import cn.stylefeng.guns.modular.system.service.UserService;
import javafx.geometry.VPos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;

/**
 * @Description
 * @Author: LZH <liangzhihui@supcon.com>
 * @Date: 2021/5/14
 */
@Controller
public class TestController {
    @Autowired
    private UserService userService;

    @GetMapping("/test")
    public void test(){
        System.out.println();
    }
}
