package com.redistest.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author songchao
 * @date 2021/12/4 10:28
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@TableName("address")
public class Address implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    @TableField("bname")
    private String bname;
    @TableField("uid")
    private Integer uid;
    @TableField("name")
    private String name;
    @TableField("phone")
    private String phone;
    @TableField("address")
    private String address;
    @TableField("date")
    private LocalDateTime date;



}
