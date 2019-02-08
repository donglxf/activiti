package com.dyb.commonactivity.mapper;

import com.dyb.commonactivity.common.mapper.SuperMapper;
import com.dyb.commonactivity.entity.ModelCallLog;

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
