package com.redistest;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.redistest.entity.Address;
import com.redistest.entity.Stu;
import com.redistest.mapper.AddressMapper;
import com.redistest.mapper.AddressTempMapper;
import com.redistest.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SpringBootTest
class MybatisTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    AddressMapper addressMapper;

    @Autowired
    AddressTempMapper addressTempMapper;

    @Test
    void save() {
        List<Address> addresses = addressMapper.selectList(new EntityWrapper<>());
        addresses.forEach(e->{
            System.out.println(e);
        });

    }
}
