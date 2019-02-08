package com.dyb.commonactivity.service;

import com.dyb.commonactivity.common.service.BaseService;
import com.dyb.commonactivity.entity.ModelCallLog;

import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author dyb
 * @since 2018-07-23
 */
public interface ModelCallLogService extends BaseService<ModelCallLog> {
    /**
     * 首页折线图
     * @param paramMap
     * @return
     */
    public Map<String, Object> indexLine(Map<String, Object> paramMap);

    public Map<String, Object> indexBar(Map<String, Object> paramMap);
}
