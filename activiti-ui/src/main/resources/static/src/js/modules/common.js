/**
 * Name:utils.js
 * Author:Van
 * E-mail:zheng_jinfan@126.com
 * Website:http://kit.zhengjinfan.cn/
 * LICENSE:MIT
 */
layui.define(['layer','laytpl','form','ht_ajax','ht_config'], function(exports) {
    var $ = layui.jquery,
        layer = layui.layer,
        form = layui.form,
        laytpl = layui.laytpl,
        config = layui.ht_config,
        _modName = 'myutil';
    //统一验证添加
    form.verify({
        /**
         * 验证key值的唯一性,需要标识id isId = true
         */
        //value：表单的值、item：表单的DOM对象
        identify: function(value, item){
            var id = $("input[isId=true]").val();
            var flag = false;
            var msg = '';
            if(!new RegExp("^[a-zA-Z0-9_.\u4e00-\u9fa5\\s·]+$").test(value)){
                return '不能有特殊字符';
            }
          //  if(id == undefined || id == ''){
                $.ajax({
                    cache : true,
                    type : "GET",
                    url : '/rule/service/check/key',
                    data : {key:value,
                        type:$(item).attr("identifyType"),
                        other:$(".other").val()
                        , id:id},// 你的formid

                    async : false,
                    dataType:'json',
                    error : function(request) {
                        //alert("Connection error");
                    },
                    success : function(da) {
                        if (da.code != 0) {
                            flag = true;
                            $(item).focus();
                            msg = da.msg;
                        }
                    }
                });
                if(flag)
                    return msg;
           // }
        },
        name: function(value, item){ //value：表单的值、item：表单的DOM对象
            if(!new RegExp("^[a-zA-Z0-9_.\u4e00-\u9fa5\\s·]+$").test(value)){
                return '不能有特殊字符';
            }
            if(/^\d+$/.test(value)){
                return '不能全为数字';
            }
        },
        max: function(value, item){ //value：表单的值、item：表单的DOM对象
            if(!new RegExp("^[a-zA-Z0-9_.\u4e00-\u9fa5\\s·]+$").test(value)){
                return '不能有特殊字符';
            }
            if(/(^\_)|(\__)|(\_+$)/.test(value)){
                return '首尾不能出现下划线\'_\'';
            }
            if(/^\d+$/.test(value)){
                return '不能全为数字';
            }
        }
    });

    var myUtil = {
        v: '1.0.3',
        baseSerive:'/rule/service',
        //业务相关
        business:{
            select:{
                name:'businessId',
                id:'businessId'
            },
            init_url:'/rule/service/business/getAll',
            init_html: function () {
                return     '      <div class="layui-input-inline">\n' +
                    '                    <select name="'+myUtil.business.select.name+'"  lay-filter="business_select" lay-search="" id="'+myUtil.business.select.id+'" lay-verify="required">\n' +
                    '                        <option value="">选择业务线</option>\n' +
                    '                    </select>\n' +
                    '                </div>'
            },
            init_html2: function (id) {

                return ' <select name="'+myUtil.business.select.name+'" lay-filter="business_select" lay-search="" id="'+id+'" lay-verify="required">\n' +
                    '    <option value="">选择业务线</option>\n' +
                    '    </select>';
            },
            /**
             * 显示下拉框
             * @param businessId 业务id
             * @param obj 放入的地方
             */
            init:function (businessId,obj,selectId) {
                $.get(myUtil.business.init_url,function(data){
                    if(data.code == '0'){
                        if(selectId == '' || selectId == undefined){
                            selectId = myUtil.business.select.id;
                        }
                        var h = myUtil.business.init_html2(selectId);
                        //初始化
                        $(obj).html(h);
                        var result = data.data;
                        for(var i = 0; i<result.length;i++){
                            var ischeck = '';
                            //选中的设置
                            if(result[i].businessId == businessId){
                                ischeck = 'selected="true"';
                            }
                            var option = '<option value="'+result[i].businessId+'" '+ischeck+' >'+result[i].businessName+'</option>';
                            $(obj).find("#"+selectId).append(option);
                        }
                        form.render('select');
                        form.on('select(business_select)', function(data){
                            myUtil.business.selectBack(data);
                        });
                    }
                },'json');
            },
            selectBack:function (data) {
                console.log(data);
            }
        },

    };
    exports('myutil', myUtil);
});