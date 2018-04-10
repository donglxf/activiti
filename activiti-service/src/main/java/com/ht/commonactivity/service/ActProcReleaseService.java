package com.ht.commonactivity.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.ht.commonactivity.common.ModelParamter;
import com.ht.commonactivity.common.RpcDeployResult;
import com.ht.commonactivity.common.RpcModelReleaseInfo;
import com.ht.commonactivity.common.RpcStartParamter;
import com.ht.commonactivity.common.result.Result;
import com.ht.commonactivity.common.service.BaseService;
import com.ht.commonactivity.entity.ActProcRelease;
import com.ht.commonactivity.vo.ModelStartVo;

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
