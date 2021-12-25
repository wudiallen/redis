package com.redistest.service;

import com.baomidou.mybatisplus.service.IService;
import com.redistest.entity.Address;

import java.util.List;

/**
 * @author songchao
 * @date 2021/12/4 12:34
 */
public interface AddressService extends IService<Address> {
    Integer selectId(String bname);

    List<Integer> selectIdList();
}
