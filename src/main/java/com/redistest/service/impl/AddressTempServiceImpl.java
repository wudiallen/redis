package com.redistest.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.redistest.entity.Address;
import com.redistest.entity.AddressTemp;
import com.redistest.mapper.AddressMapper;
import com.redistest.mapper.AddressTempMapper;
import com.redistest.service.AddressService;
import com.redistest.service.AddressTempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author songchao
 * @date 2021/12/4 12:50
 */
@Service
public class AddressTempServiceImpl extends ServiceImpl<AddressTempMapper, AddressTemp> implements AddressTempService {

    @Autowired
    AddressTempMapper addressTempMapper;

    @Override
    public List<AddressTemp> selectFromResource() {
        return addressTempMapper.selectFromResource();
    }

    @Override
    public void trancate() {
        addressTempMapper.trancate();
    }
}
