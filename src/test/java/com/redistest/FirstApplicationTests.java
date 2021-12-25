package com.redistest;

import com.alibaba.fastjson.JSON;
import com.redistest.entity.Stu;
import com.redistest.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JsonbTester;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SpringBootTest
class FirstApplicationTests {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    RedisUtil redisUtil;

    @Test
    void save() {

        Stu stu1 = new Stu(1, "小董", 2, "开发");
        Stu stu2 = new Stu(1, "小董", 2, "开发");

        //向redis中添加数据
        redisTemplate.opsForSet().add("stu1", JSON.toJSON(stu1));
        redisTemplate.opsForSet().add("stu2", JSON.toJSON(stu2));
        Set<Object> list = redisTemplate.opsForSet().members("stu1");
        //根据键值取出数据
        //  Long aLong = redisTemplate.opsForSet().differenceAndStore("stu1", "setKey2", "no");
    }

    @Test
    void selectForStu() {
        Set<Object> list = redisTemplate.opsForSet().members("stu2");
        List<Stu> stuList = new ArrayList<>();
        list.forEach(e -> {
            Stu stu = JSON.parseObject(e.toString(), Stu.class);
            stuList.add(stu);
        });
        stuList.forEach(e -> {
            System.out.println(e.toString());
        });

    }

    @Test
    void stu3() {
        Stu stu3 = new Stu(3, "小董", 1, "开发");
        //向redis中添加数据
        redisTemplate.opsForSet().add("stu3", JSON.toJSON(stu3));
        Set<Object> list = redisTemplate.opsForSet().members("stu1");
        //根据键值取出数据
        //  Long aLong = redisTemplate.opsForSet().differenceAndStore("stu1", "setKey2", "no");
    }

    @Test
    void delete() {
        redisTemplate.delete("stu1");
        redisTemplate.delete("stu2");
        redisTemplate.delete("stu3");
    }

    /**
     * 测试   differenceAndStore命令
     */
    @Test
    void TestDifferenceAndScore() {
        Long aLong = redisTemplate.opsForSet().differenceAndStore("stu1", "stu2", "stu3");
        System.out.println(aLong);
    }

    @Test
    void TestDifferenceAndScore1() {
        Set<Object> difference = redisTemplate.opsForSet().difference("stu1", "stu2");
        difference.forEach(e -> {
            System.out.println(e);
        });
    }

}
