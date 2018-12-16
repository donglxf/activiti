package com.ht.commonactivity.mapper;

import com.ht.commonactivity.entity.ModelCallLog;
import com.ht.commonactivity.common.mapper.SuperMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author dyb
 * @since 2018-05-30
 */
public interface ModelCallLogMapper extends SuperMapper<ModelCallLog> {
    /**
     * 档案状态统计
     * @return
     */
    public List<Map<String, Object>> indexLine();

    public List<Map<String, Object>> indexBar(String date);
}
