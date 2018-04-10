package com.ht.commonactivity.common.service;

import com.baomidou.mybatisplus.service.IService;

;

public interface BaseService<T> extends IService<T>{
    /**
     * 验证key值的唯一性,返回true表示已存在
     * @param key
     * @return
     */
    boolean checkKey(String key, String other, Long id);

}