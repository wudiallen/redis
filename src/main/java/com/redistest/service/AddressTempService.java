package com.redistest.service;

import com.baomidou.mybatisplus.service.IService;
import com.redistest.entity.Address;
import com.redistest.entity.AddressTemp;

import java.util.List;

/**
 * @author songchao
 * @date 2021/12/4 12:34
 */
public interface AddressTempService extends IService<AddressTemp> {
    List<AddressTemp> selectFromResource();
    void trancate();
}
