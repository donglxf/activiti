package com.example.commonactivitiui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class ModelController {

    /**
     * 首页
     * @return
     */
    @RequestMapping(value = "indexDev",method = RequestMethod.GET)
    public String index(){
        return "index-dev";
    }

    /**
     * 模型新增
     * @return
     */
    @RequestMapping(value = "/addView", method = RequestMethod.GET)
    public String addView() {
        return "model/modelAdd";
    }

    /**
     * 模型编辑页面
     * @return
     */
    @RequestMapping(value = "/modelEdit", method = RequestMethod.GET)
    public String modelEdit() {
        return "modeler";
    }

    /**
     * 模型编辑页面
     * @return
     */
    @RequestMapping(value = "/modelIndex", method = RequestMethod.GET)
    public String modelIndex() {
        return "model/modelIndex";
    }

}
