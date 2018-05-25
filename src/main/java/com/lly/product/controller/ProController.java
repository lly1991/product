package com.lly.product.controller;

import com.lly.product.config.GirlConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lly
 * @Description TODO
 * @date 2018/5/25上午10:30
 * @since JDK1.8
 */
@RequestMapping("/product")
@RestController
public class ProController
{

    @RequestMapping("/info")
    public String produc(){

        return "info";
    }

    @Autowired
    private GirlConfig girlConfig;
    @GetMapping("/getGirl")
    public String getConfig(){
        return  girlConfig.getName()+"=="+girlConfig.getAge();
    }
}
