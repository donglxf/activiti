package com.example.commonactivity.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.example.commonactivity.common.ModelParamter;
import com.example.commonactivity.common.RpcDeployResult;
import com.example.commonactivity.common.RpcModelReleaseInfo;
import com.example.commonactivity.common.RpcStartParamter;
import com.example.commonactivity.common.result.Result;
import com.example.commonactivity.common.service.BaseService;
import com.example.commonactivity.entity.ActProcRelease;
import com.example.commonactivity.vo.ModelStartVo;

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
