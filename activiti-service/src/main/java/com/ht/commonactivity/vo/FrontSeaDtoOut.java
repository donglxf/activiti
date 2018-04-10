package com.ht.commonactivity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 描述：前海输出
 */
@Data
@ApiModel
public class FrontSeaDtoOut {

	@ApiModelProperty(value = "数据状态，请参考数据状态")
	private String dataStatus;

	@ApiModelProperty(value = "证件号码")
	private String idNo;

	@ApiModelProperty(value = "证件类型")
	private String idType;

	@ApiModelProperty(value = "姓名")
	private String name;

	@ApiModelProperty(value = "序列号")
	private String seqNo;

	@ApiModelProperty(value = "来源代码,请参考来源代码")
	private String sourceId;

	@ApiModelProperty(value = "风险得分,10 - 45")
	private String rskScore;

	@ApiModelProperty(value = "风险标记,请参考风险标记")
	private String rskMark;


}