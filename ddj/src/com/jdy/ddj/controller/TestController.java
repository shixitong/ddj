package com.jdy.ddj.controller;


import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Tianding
 */
@Controller
@RequestMapping("/ddj/test/")
public class TestController {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(TestController.class);


    /**
     * @return clientGrid.jsp 页面
     */
    @RequestMapping("index.do")
    public String index(){
        return "/form/stationinfoForm";
    }


}

