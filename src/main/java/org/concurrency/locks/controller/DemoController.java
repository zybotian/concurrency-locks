package org.concurrency.locks.controller;

import com.alibaba.fastjson.JSON;

import org.concurrency.locks.biz.DemoBiz;
import org.concurrency.locks.model.Demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

/**
 * @author tianbo
 * @date 2018-11-27
 */

@Controller
@Slf4j
@RequestMapping("/demo/v201811281013")
public class DemoController {

    @Autowired
    private DemoBiz demoBiz;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String demoPost(@RequestBody Demo demo) {
        log.info("do post, param:{}", demo);
        if (demo != null) {
            demoBiz.insert(demo);
        }
        return JSON.toJSONString(demo);
    }

    @RequestMapping(path = "/get", method = RequestMethod.GET)
    @ResponseBody
    public String demoGet() {
        return JSON.toJSONString(demoBiz.findNewestCreated());
    }
}
