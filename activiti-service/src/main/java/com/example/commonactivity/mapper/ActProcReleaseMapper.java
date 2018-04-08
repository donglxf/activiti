package com.example.commonactivity.mapper;


import com.example.commonactivity.common.mapper.SuperMapper;
import com.example.commonactivity.entity.ActProcRelease;
import com.example.commonactivity.entity.ActRuTask;
import com.example.commonactivity.vo.FindTaskBeanVo;
import org.activiti.engine.task.Task;
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
