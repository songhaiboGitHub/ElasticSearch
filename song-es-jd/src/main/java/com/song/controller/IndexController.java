package com.song.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author songhaibo
 * @create 2020-09-23 5:58 下午
 */
@Controller
public class IndexController {

    @GetMapping({"/","/index"})
    public String index(){
        return "index";
    }
}
