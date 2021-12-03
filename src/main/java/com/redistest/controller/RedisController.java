package com.redistest.controller;

import com.redistest.entity.Stu;
import com.redistest.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author songchao
 * @date 2021/12/3 16:33
 */

@RestController
@RequestMapping(value = "/redis")
public class RedisController {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取对应键的值，redisTemplate操作
     * @param key
     * @return
     */
    @RequestMapping(value = "get")
    public Object getValue(String key){
        Object value = redisTemplate.opsForValue().get(key);
        return value;
    }

    /**
     * 添加键值
     * @param key
     * @param value
     * @return
     */
    @RequestMapping(value = "set")
    public boolean set(String key, String value){
        return redisUtil.set(key, value);
    }

    /**
     * 添加List对象数据到redis中
     * @return
     */
    @RequestMapping(value = "/setList")
    public boolean setList(){
        List<Object> stuList = new ArrayList<>();

        Stu stu = new Stu(1,"张三",18,"软件部");
        Stu stu1 = new Stu(2,"王五",18,"软件部");
        Stu stu2 = new Stu(3,"李四",18,"财务部");

        stuList.add(stu);
        stuList.add(stu1);
        stuList.add(stu2);
        return redisUtil.lSetList("stu", stuList);
    }

    /**
     * 获取全部数据
     * @return
     */
    @RequestMapping(value = "getList")
    public Object getList(){
        return redisUtil.lGet("stu", 0, -1);
    }

}

