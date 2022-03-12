package com.redistest.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.redistest.entity.Address;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author songchao
 * @date 2021/12/4 10:43
 */
@Mapper
@Repository
public interface AddressMapper extends BaseMapper<Address> {

    @Update("<script>" +
            "update address set uid =#{a.uid},name =#{a.name},phone =#{a.phone},address =#{a.address} where  bname =#{a.bname}"+
            "</script>")
    Integer updateByBname(@Param("a") Address address);

    @Select("select id from  address where bname = #{bname}")
    Integer selectId(@Param("bname") String bname);

    @Select("select id from address")
    List<Integer> selectIdList();

    @Select("select id from address where bname = #{bname}")
    Integer findIdByName(@Param("bname") String bname);

    @Delete("<script>  " +
            "delete from address where id not in " +
            "<foreach  collection='ids' item='id' open='(' close=')' separator=',' > #{id}</foreach>" +
            " </script>")
    void deleteNotInExistId(@Param("ids") List<Integer> existIdList);

//    @Update("<script>" +
//            "<foreach  collection='lists' item='list'  separator=';' > " +
//            "update address set bname=#{list.bname},uid=#{list.uid},name=#{list.name},phone=#{list.phone},address=#{list.address},date=#{list.date} where id = #{list.id}" +
//            "</foreach>" +
//            "</script>")
    @Update("<script>" +
            "replace into address(id,bname,uid,name,phone,address,date) values" +
            "<foreach  collection='lists' item='list'  separator=',' > " +
            " (#{list.id},#{list.bname},#{list.uid},#{list.name},#{list.phone},#{list.address},#{list.date})" +
            "</foreach>" +
            "</script>")
    void updateList(@Param("lists") List<Address> updateAndInsertList);

    @Select("select id ,name from address where name like concat('%',#{name},'%')")
    Address selectByName(@Param("name") String name);
}
