package com.ht.commonactivity.mapper;


import com.ht.commonactivity.common.mapper.SuperMapper;
import com.ht.commonactivity.entity.ActProcRelease;
import com.ht.commonactivity.entity.ActRuTask;
import com.ht.commonactivity.vo.FindTaskBeanVo;
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
