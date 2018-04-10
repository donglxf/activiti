package com.ht.commonactivity.mapper;

import com.ht.commonactivity.common.mapper.SuperMapper;
import com.ht.commonactivity.entity.ActExcuteTask;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhangzhen
 * @since 2018-01-19
 */
public interface ActExcuteTaskMapper extends SuperMapper<ActExcuteTask> {
    /**
     * 计算规则平均耗时
     * @return
     */
    List<Map<String,Object>> getModelAvgTime(Map<String, Object> obj);

    /**
     * 获取规则执行信息
     * @return
     */
    Map<String,Object> getModelExecInfo(Map<String, Object> obj);

//    List<ActExcuteTaskVo> verficationTaskPage(Pagination page, VerficationModelVo verficationModelVo);
}
