package com.group3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author group3
 * @date 2024/04
 */
@Controller
public class ErrorController {

    @RequestMapping(value = "/error2")
    public String error() {
        return "error";
    }

}
