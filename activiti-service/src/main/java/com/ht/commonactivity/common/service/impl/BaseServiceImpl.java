package com.ht.commonactivity.common.service.impl;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ht.commonactivity.common.service.BaseService;

public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<BaseMapper<T>, T> implements BaseService<T> {
    public static long creUser= 111;
    @Override
    public boolean checkKey(String key,String other,Long id) {
        return true;
    }
}
