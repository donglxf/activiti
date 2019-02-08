package com.dyb.commonactivity.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.dyb.commonactivity.common.ModelParamter;
import com.dyb.commonactivity.common.RpcDeployResult;
import com.dyb.commonactivity.common.RpcModelReleaseInfo;
import com.dyb.commonactivity.common.RpcStartParamter;
import com.dyb.commonactivity.vo.ModelStartVo;
import com.dyb.commonactivity.common.result.Result;
import com.dyb.commonactivity.common.service.BaseService;
import com.dyb.commonactivity.entity.ActProcRelease;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhangzhen
 * @since 2018-01-16
 */
public interface ActProcReleaseService extends BaseService<ActProcRelease> {

    Page<ActProcRelease> queryProcReleaseForPage(Page<ActProcRelease> page, ActProcRelease actProcRelease);

    Result<RpcDeployResult> proceDeploy(ModelParamter paramter, String userId);

    String startProcess(RpcStartParamter rpcStartParamter, String userId);

    String startModel(ModelStartVo modelStartVo, String userId);

//    Long startInputValidateProcess(RpcStartParamter paramter, String userId)throws Exception;
//
//    Long startBatchValidateProcess(RpcStartParamter paramter, String userId)throws Exception;

    RpcModelReleaseInfo convertRpcActExcuteTask(ActProcRelease release);
}
