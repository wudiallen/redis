package com.redistest.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.redistest.entity.Address;
import com.redistest.mapper.AddressMapper;
import com.redistest.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author songchao
 * @date 2021/12/4 12:50
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Autowired
    AddressMapper addressMapper;

    @Override
    public Integer selectId(String bname) {
        return addressMapper.selectId(bname);
    }

    @Override
    public List<Integer> selectIdList() {
        return addressMapper.selectIdList();
    }
}
