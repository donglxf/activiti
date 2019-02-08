package com.dyb.commonactivity.mapper;


import com.dyb.commonactivity.common.mapper.SuperMapper;
import com.dyb.commonactivity.vo.FindTaskBeanVo;
import com.dyb.commonactivity.entity.ActProcRelease;
import com.dyb.commonactivity.entity.ActRuTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhangzhen
 * @since 2018-01-16
 */
public interface ActProcReleaseMapper extends SuperMapper<ActProcRelease> {

    public List<ActProcRelease> getModelLastedVersion(Map<String, Object> paramter);

    public List<ActRuTask> findTaskByAssigneeOrGroup(@Param("findTaskVo") FindTaskBeanVo vo);

}
