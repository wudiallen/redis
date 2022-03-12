package com.redistest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.redistest.entity.Address;
import com.redistest.entity.AddressTemp;
import com.redistest.entity.Stu;
import com.redistest.mapper.AddressMapper;
import com.redistest.mapper.AddressTempMapper;
import com.redistest.service.AddressService;
import com.redistest.service.AddressTempService;
import com.redistest.util.RedisUtil;
import org.apache.ibatis.javassist.expr.NewArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import sun.util.resources.cldr.kk.LocaleNames_kk;

import javax.sound.midi.Soundbank;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
class MybatisTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Autowired
    AddressMapper addressMapper;

    @Autowired
    AddressService addressService;

    @Autowired
    AddressTempService addressTempService;

    @Test
    void testUpdate() {
        Address address = new Address();
        address.getList().add("1");
        System.out.println(address);
    }

    private void check(List<Address> list, Integer errorNum) {
        for (Address address : list) {
            address.setId(1);
            errorNum += 1;
        }
    }

    @Test
    void makeRecords() {
        long start = System.currentTimeMillis();
//        for (int i = 0; i < 10000; i++) {
//            AddressTemp temp = new AddressTemp(null, "小董" + i, i, "备注" + i, "21", "地址", LocalDateTime.now());
//            addressTempService.insert(temp);
//        }

        List<AddressTemp> list = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            list.add(new AddressTemp(null, "小董" + i, i, "备注" + i, "21", "地址", LocalDateTime.now()));
        }
        addressTempService.insertBatch(list);
        long end = System.currentTimeMillis();
        System.out.println("------------------------" + (end - start) + "------------------------");
    }

    @Test
    void save() {
        long start = System.currentTimeMillis();

        //1 从源表中查出数据
        List<AddressTemp> addressTempsFromResource = addressTempService.selectFromResource();
        System.out.println("------------------从源表中查出数据----------");

        //2 加工数据   添加到临时表中   （模拟，uid统一加1）
        addressTempsFromResource.forEach(e -> {
            e.setUid(e.getUid() + 1);
        });

        addressTempService.insertBatch(addressTempsFromResource, addressTempsFromResource.size()); //添加到临时表中
        System.out.println("--------------加入临时表成功");
        //3 将临时表中的数据存入redis
        //3.1 取出临时表内容
        String addressTempskey = "addressTempskey";
        List<AddressTemp> addressTemps = addressTempService.selectList(new EntityWrapper<>());
        addressTemps.forEach(e -> {
            e.setId(null);
        });
        //存入redis
        redisTemplate.opsForSet().add(addressTempskey, JSONArray.toJSONString(addressTemps));

        //3.2 取出目的表内容
        String addresseskey = "addresseskey";
        List<Address> addresses = addressService.selectList(new EntityWrapper<>());
        addresses.forEach(e -> {
            e.setId(null);
        });
        System.out.println("+++++++++++++++++++++++++++++++++++++");
        addressTemps.forEach(e -> {
            System.out.println(e.toString());
        });
        addresses.forEach(e -> {
            System.out.println(e.toString());
        });
        //存入redis
        redisTemplate.opsForSet().add(addresseskey, JSONArray.toJSONString(addresses));

        //4  作对比找出差异
        Set<Object> difference = redisTemplate.opsForSet().difference(addressTempskey, addresseskey);
        String substring = difference.toString().substring(1, difference.toString().length() - 1);
        System.out.println("------------------找差异结束-------------------");

        //4.1 将差异记录从json串转化为对象
        List<Address> differenceList = JSONArray.parseArray(substring, Address.class);
        if (null != differenceList && differenceList.size() >= 0)
            differenceList.forEach(e -> {
                System.out.println(e);
            });
        System.out.println("------------------转化结束-------------------");

        //5  将差异更新入目的表
        if (null == differenceList || differenceList.size() <= 0) {
            System.out.println("------------------无差异需要同步-------------------");
        } else {
            List<Address> updateList = new ArrayList<>();
            List<Integer> existIdList = new ArrayList<>();
            List<Address> insertList = new ArrayList<>();
            //1找到需要更新的记录
            //2找到需要添加的记录
            differenceList.forEach(e -> {
                Integer id = addressService.selectId(e.getBname());
                if (null != id) {
                    e.setId(id);
                    existIdList.add(id); //加入已存在的id集合
                    updateList.add(e);  //要更新的集合
                }
                insertList.add(e);//要插入的集合
            });

            //找到所有id
            List<Integer> allIdList = addressService.selectIdList();
            //3目的表中 删除 除了要更新的记录， 更新数据 ， 再插入需要插入的记录
            //删除数据
            List<Integer> deleteIdList = allIdList.stream().filter(e -> !existIdList.contains(e)).collect(Collectors.toList());
            if (null != deleteIdList && deleteIdList.size() > 0) {
                addressService.deleteBatchIds(deleteIdList);
            }
            if (null != updateList && updateList.size() > 0) {
                addressService.updateBatchById(updateList);//更新数据
            }
            //插入数据
            System.out.println("-----------------------插入数据------------------------");
            addressService.insertBatch(insertList, insertList.size());
            System.out.println("------------------差异同步完成----" + differenceList.size() + "条记录---------------");
        }
        //6  清除缓存
        redisTemplate.delete(addresseskey);
        redisTemplate.delete(addressTempskey);
        System.out.println("-----------------清空redis缓存完成----------------------");
        addressTempService.trancate();
        System.out.println("-----------------清空临时表完成----------------------");

        long end = System.currentTimeMillis();
        System.out.println("共耗时" + (end - start));
    }

    /**
     * 测试不加入临时表
     */
    @Test
    void save2() {
        String addressTempskey = "{aaa}addressTempskey";
        String addresseskey = "{aaa}addresseskey";
        redisTemplate.delete(addresseskey);
        redisTemplate.delete(addressTempskey);

        long start = System.currentTimeMillis();
        //1 从源表中查出数据
        List<AddressTemp> addressTempsFromResource = addressTempService.selectFromResource();
        System.out.println("------------------从源表中查出数据----------");
        //3 将临时表中的数据存入redis
        //3.1 取出临时表内容

        //存入redis
        //3.2 取出目的表内容
        List<Address> addresses = addressService.selectList(new EntityWrapper<>());
        addresses.forEach(e -> {
            e.setId(null);
        });
        //存入redis
        addressTempsFromResource.forEach(e -> {
            redisTemplate.opsForSet().add(addressTempskey, JSON.toJSONString(e));
        });
        addresses.forEach(e -> {
            redisTemplate.opsForSet().add(addresseskey, JSON.toJSONString(e));
        });
        //4  作对比找出差异
        Set<Object> difference = redisTemplate.opsForSet().difference(addressTempskey, addresseskey);
        System.out.println("------------------找差异结束-------------------");

        //4.1 将差异记录从json串转化为对象
        List<Address> differenceList = JSONArray.parseArray(difference.toString(), Address.class);
        if (null != differenceList && differenceList.size() >= 0)
            differenceList.forEach(e -> {
                System.out.println(e);
            });
        System.out.println("------------------转化结束-------------------");
        //删除数据
        List<Integer> existIdList = new ArrayList<>();
        addressTempsFromResource.forEach(e -> {
            Integer id = addressMapper.findIdByName(e.getBname());
            if (null != id) {
                existIdList.add(id);
            }
        });
        if (null != existIdList && existIdList.size() > 0) {
            addressMapper.deleteNotInExistId(existIdList);
        }
        //5  将差异更新入目的表
        if (null == differenceList || differenceList.size() <= 0) {
            System.out.println("------------------无差异需要同步-------------------");
        } else {
            List<Address> updateList = new ArrayList<>();
            List<Address> insertList = new ArrayList<>();
            //1找到需要更新的记录
            //2找到需要添加的记录
            differenceList.forEach(e -> {
                Integer id = addressService.selectId(e.getBname());
                if (null != id) {
                    e.setId(id);
                    updateList.add(e);
                } else {
                    insertList.add(e);
                }
            });
            //3目的表中 删除 除了要更新的记录， 更新数据 ， 再插入需要插入的记录

//            if (insertList.size() > 0){
//                addressService.insertBatch(insertList);
//            }
            System.out.println("----------------------------------更新");
            addressMapper.updateList(differenceList);
//            if (updateList.size()>0){
//                addressMapper.updateList(updateList);
//            }
            //插入数据
            System.out.println("-----------------------插入数据------------------------");
            System.out.println("------------------差异同步完成----" + differenceList.size() + "条记录---------------");
        }
        //6  清除缓存
        redisTemplate.delete(addresseskey);
        redisTemplate.delete(addressTempskey);
        System.out.println("-----------------清空redis缓存完成----------------------");
        System.out.println("-----------------清空临时表完成----------------------");

        long end = System.currentTimeMillis();
        System.out.println("共耗时" + (end - start));
    }

    @Test
    void testInsertRedis() {
        long start = System.currentTimeMillis();

        //1 从源表中查出数据
        List<AddressTemp> addressTempsFromResource = addressTempService.selectFromResource();
        System.out.println("------------------从源表中查出数据----------");

        String addressTempskey = "addressTempskey";

        //存入redis
        redisTemplate.opsForSet().add(addressTempskey, JSONArray.toJSONString(addressTempsFromResource));

    }

    @Test
    void test() {
        Stu stu = new Stu(1, "1", 1, "1");

        List<Stu> list1 = new ArrayList<>();
        list1.add(stu);
        list1.forEach(e -> {
            redisTemplate.opsForSet().add("{compare}key1", JSON.toJSONString(e));
        });

        Stu stu1 = new Stu(2, "1", 1, "1");
        List<Stu> list2 = new ArrayList<>();
        list2.add(stu1);
        list2.forEach(e -> {
            redisTemplate.opsForSet().add("{compare}key2", JSON.toJSONString(e));
        });

        Set<Object> difference = redisTemplate.opsForSet().difference("{compare}key1", "{compare}key2");
        System.out.println(difference);
        System.out.println(difference.toString());
        List<Stu> list = JSONArray.parseArray(difference.toString(), Stu.class);
        if (null == list || list.size() <= 0) {
            System.out.println("差异集合为空");
        } else {
            list.forEach(e -> {
                System.out.println(e);
            });
        }
    }

    @Test
    void test1() {
        String s = "123";
        if (null != s) {
            System.out.println("不是null");
        } else {
            System.out.println("是null");
        }
    }

}
