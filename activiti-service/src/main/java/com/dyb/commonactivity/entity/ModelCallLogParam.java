package com.dyb.commonactivity.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
/**
 * <p>
 * 
 * </p>
 *
 * @author dyb
 * @since 2018-05-31
 */
@ApiModel
@TableName("act_model_call_log_param")
public class ModelCallLogParam extends Model<ModelCallLogParam> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@ApiModelProperty(required= true,value = "主键")
	private Long id;
    /**
     * 外键id
     */
	@TableField("foreign_id")
	@ApiModelProperty(required= true,value = "外键id")
	private String foreignId;
    /**
     * 模型参数
     */
	@ApiModelProperty(required= true,value = "模型参数")
	private String datas;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getForeignId() {
		return foreignId;
	}

	public void setForeignId(String foreignId) {
		this.foreignId = foreignId;
	}

	public String getDatas() {
		return datas;
	}

	public void setDatas(String datas) {
		this.datas = datas;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "ModelCallLogParam{" +
			"id=" + id +
			", foreignId=" + foreignId +
			", datas=" + datas +
			"}";
	}
}
