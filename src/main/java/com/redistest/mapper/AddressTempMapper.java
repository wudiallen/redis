package com.redistest.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.redistest.entity.Address;
import com.redistest.entity.AddressTemp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author songchao
 * @date 2021/12/4 10:43
 */
@Mapper
@Repository
public interface AddressTempMapper extends BaseMapper<AddressTemp> {

    //    @Select("select id as id,beizhuname as bname ,uid as uid ,name as name, phone as phone,address as address from address_resource")
//    List<AddressTemp> selectFromResource();
    @Select("select  beizhuname as bname ,uid as uid ,name as name, phone as phone,address as address ,date as date from address_resource")
    List<AddressTemp> selectFromResource();

    @Update("truncate table address_temp")
    void trancate();
}
