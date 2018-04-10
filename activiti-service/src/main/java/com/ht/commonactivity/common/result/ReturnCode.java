package com.ht.commonactivity.common.result;


/**
    * 统一返回定义
    * 
    * 实现带有抽象方法的枚举
    * 
    * @author zhangpeng
    *
    */
public enum ReturnCode {
		
	/**执行成功*/
	SUCCESS("执行成功",1),
	NORESULT("没有更多了哦",-2),
	ERROR("系统错误",-500),
	TELL_ISEXSIT("电话已被注册",-10),
	TELL_TOMORE("发送验证码过于频繁",-101),
	TELL_CODE_ERROR("验证码输入错误",-102),
	NOLOGIN("未登录",-1),
	ERROR_TOKEN("指令验证失败",-5),
	ERROR_PASS("密码输入错误！或用户不存在",-2),
	ERROR_LOGIN("用戶被禁用",-3),
	;
	private int value;
	private String text;
	private ReturnCode(String text,int value) {
		this.value = value;
		this.text = text;
	}
	public String getText(){
		return text;
	}
	public int getValue() {
		return value;
	}
	public boolean isRest() {
		return false;
	}
	/**
	 * <p>Title: 根据状态值获取</p>
	 * @param index
	 * @return
	 * @author hunter
	 * @date  2017-9-27 上午11:32:10
	 */
	public static ReturnCode getReturnCode(int index) {
        for (ReturnCode c : ReturnCode.values()) {
            if (c.getValue() == index) {
                return c;
            }
        }
        return null;
    }
}

   