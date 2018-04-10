/*
 * 文件名 Result.java
 * 版权 Copyright 2017 团贷网
 * 创建人 谭荣巧
 * 创建时间 2017年11月20日 下午3:40:50 
 */
package com.ht.commonactivity.common.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Rest接口请求结果对象<br>
 * 
 * @author 谭荣巧
 * @date 2017年11月20日 下午3:40:50
 * @since cams-common 1.0-SNAPSHOT
 */
@ApiModel
public class Result<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 错误代码
	 */
	@ApiModelProperty(required = true, value = "错误代码")
	private int code;
	/**
	 * 错误描述
	 */
	@ApiModelProperty(required = true, value = "错误描述")
	private String msg;

	/**
	 * 记录总数"
	 */
	@ApiModelProperty(required = true, value = "记录总数")
	private int count;

	/**
	 * 传递给请求者的数据
	 */
	@ApiModelProperty(value = "传递给请求者的数据")
	private T data;





	public Result() {
		super();
	}

	/**
	 * 构造请求成功的结果对象<br>
	 * 
	 * @author 谭荣巧
	 * @date 2017年11月20日 下午3:47:10
	 * @return 请求的结果对象
	 * @since cams-common 1.0-SNAPSHOT
	 */
	public static <T> Result<T> success() {
		Result<T> result = new Result<T>();
		result.setCode(0);
		result.setMsg("请求成功！");
		return result;
	}

	/**
	 * 构造请求成功的结果对象<br>
	 * 
	 * @author 谭荣巧
	 * @date 2017年11月20日 下午3:47:36
	 * @param data
	 *            返回的数据集
	 * @return 请求的结果对象
	 * @since cams-common 1.0-SNAPSHOT
	 */
	public static <T> Result<T> success(T data) {
		Result<T> result = new Result<T>();
		result.setCode(0);
		result.setMsg("请求成功！");
		result.setData(data);
		return result;
	}

	/**
	 * 构造请求失败的结果对象<br>
	 * 
	 * @author 谭荣巧
	 * @date 2017年11月20日 下午3:48:01
	 * @param code
	 *            结果代码
	 * @param msg
	 *            结果描述
	 * @return 请求的结果对象
	 * @since cams-common 1.0-SNAPSHOT
	 */
	public static <T> Result<T> error(int code, String msg) {
		Result<T> result = new Result<T>();
		result.setCode(code);
		result.setMsg(msg);
		return result;
	}

	public static <T> Result<T> build(int code, String msg, T data,int count) {
		Result<T> result = new Result<T>();
		result.setCode(code);
		result.setMsg(msg);
		result.setData(data);
		result.setCount(count);
		return result;
	}

	/**
	 * 构造结果对象<br>
	 * 
	 * @author 谭荣巧
	 * @date 2017年11月20日 下午3:48:01
	 * @param code
	 *            结果代码
	 * @param msg
	 *            结果描述
	 * @param data
	 *            返回的数据集
	 * @return 请求的结果对象
	 * @since cams-common 1.0-SNAPSHOT
	 */
	public static <T> Result<T> build(int code, String msg, T data) {
		Result<T> result = new Result<T>();
		result.setCode(code);
		result.setMsg(msg);
		result.setData(data);
		return result;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg
	 *            the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * @return the data
	 */
	public T getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(T data) {
		this.data = data;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
