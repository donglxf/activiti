package com.ht.commonactivity.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author zhangzhen
 * @since 2018-01-17
 */
@Data
@ApiModel
@TableName("risk_model_sence")
public class ModelSence extends Model<ModelSence> {

    private static final long serialVersionUID = 1L;


    /**
     * 返回结果用
     */
//    @TableField(exist = false)
//    private List<VariableBind> data;

    /**
     * 模型名，返回结果用
     */
    @TableField(exist = false)
    private String sceneName;

    /**
     * 主键
     */
    @TableId("id")
    @ApiModelProperty(required = true, value = "主键")
    private Long id;
    /**
     * 模型定义ID
     */
    @TableField("model_procdef_id")
    @ApiModelProperty(required = true, value = "模型定义ID")
    private String modelProcdefId;
    /**
     * 決策版本流水号
     */
    @TableField("sence_version_id")
    @ApiModelProperty(required = true, value = "決策版本流水号")
    private Long senceVersionId;
    /**
     * 是否生效：0-有效，1-无效
     */
    @TableField("is_effect")
    @ApiModelProperty(required = true, value = "是否生效：0-有效，1-无效")
    private String isEffect;
    /**
     * 创建时间
     */
    @TableField("create_time")
    @ApiModelProperty(required = true, value = "创建时间")
    private Date createTime;
    /**
     * 创建用户
     */
    @TableField("create_user")
    @ApiModelProperty(required = true, value = "创建用户")
    private String createUser;


    private transient String senceCode;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
