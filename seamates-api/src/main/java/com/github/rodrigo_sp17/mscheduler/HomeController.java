package com.github.rodrigo_sp17.mscheduler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping(value = {"/", "/privacy", "/terms", "/login", "/logout", "/signup", "/changePassword", "/loginSuccess", "/socialSignup"})
    public String index() {
        return "/index.html";
    }
}
