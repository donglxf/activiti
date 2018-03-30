
/**
 * Name:utils.js
 * Author:Van
 * E-mail:zheng_jinfan@126.com
 * Website:http://kit.zhengjinfan.cn/
 * LICENSE:MIT
 */
layui.define(['layer','form','laytpl','ht_ajax'], function (exports) {
    var $ = layui.jquery,
        layer = layui.layer,
        form = layui.form,
        laytpl = layui.laytpl,
        _modName = 'sceneUtil';
    /*//条件体
    var conditionInfo = {
        //条件拼接
        conditionExpression: '',
        //条件+值 中文描述
        conditionDesc: '',
        val: ''
    };
    //动作体
    var actionValInfo = {
        //动作id参数Id
        actionParamId: '3',
        //值
        paramValue: ''

    };
    var subForm = {
        //条件集合
        conditionInfos: [],
        //动作集合
        actionInfos: [],
    }
    var itemVals = [];
    var itemTexts = [];
    var entityIds = [];*/
    //工具
var myUtil = {
    v: '1.0.3',
    baseSerive:'/rule/service',

    //业务相关
    business:{
        data:[],
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
         * @param type 1实体对象 2动作
         */
        init:function (businessId,obj,type) {
            var selectId = myUtil.business.select.id;
            if( myUtil.business.data == [] || myUtil.business.data.length < 1){
                $.get(myUtil.business.init_url,function(data){
                    if(data.code == '0'){
                        var h = myUtil.business.init_html2(selectId);
                        //初始化
                        $(obj).html(h);
                        var result = data.data;
                        myUtil.business.data = result;
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
                            myUtil.business.selectBack(data,type);
                        });
                    }
                },'json');
            }else{
                var h = myUtil.business.init_html2(selectId);
                //初始化
                $(obj).html(h);
                var result =   myUtil.business.data;
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
                    myUtil.business.selectBack(data,type);
                });
            }

        },
        selectBack:function (data,type) {
            //实体类初始化
            if(type == 1){
                sceneUtil.entitySelectInit(data.value);
            }
            //动作初始化
            else{
                sceneUtil.actionSelectInit(data.value);
            }
            console.log(data);
        }
    },
};
    var sceneUtil = {
            subType:1,
            isEdit:true,
            flag :true,
            v: '1.0.0',
            sceneId:-1,//场景id
            entityImport:[],
            actionImport:[],
            sub:{
                gradeForm:{},
            },
            data:{
                //常量库
                dicBank:[],
                //动作库
                actionBank:[],
                //条件常量
                condition: [
                    {value: "==", text: "等于"},
                    {value: "!=", text: "不等于"},
                    {value: "<", text: "小于"},
                    {value: "<=", text: "小于或等于"},
                    {value: ">", text: "大于"},
                    {value: ">=", text: "大于或等于"},
                    {value: "contains", text: "包含"},
                    {value: "^contains", text: "不包含"},
                    {value: "memberOf", text: "属于"},
                    {value: "^memberOf", text: "不属于"},

                    // {value: "like%", text: "开始以"},
                  //  {value: "%like", text: "结束以"},
                   // {value: "===", text: "忽略"},
                ],
                //数据对象库
                entitysBank:[],
                //变量库
                itemsBank:[],
            },
            //实体类集合
            entitys: [],
            //提交格式设置
            subForm: {},
            //动作默认
            actionTypes: [{value: "3", text: "得分"}],

            /**
             * 根据一个html内容读取出body标签里的文本
             */
            getBodyContent: function (content) {
                var REG_BODY = /<body[^>]*>([\s\S]*)<\/body>/,
                    result = REG_BODY.exec(content);
                if (result && result.length === 2)
                    return result[1];
                return content;
            },
             /**
             * 显示和隐藏新增和删除按钮
             */
            showAddAndDelete:function () {
                var conShowTrIndexMap = {};
                $(".addRow,.deleteRow").hide();
                $("#table tbody tr").each(function (i,e) {
                    var tdFir = $(this).find("td:first");
                    var _key_index = $(tdFir).attr("data-index");
                    conShowTrIndexMap[_key_index]=i;
                });
                 $.each(conShowTrIndexMap,function(key,value){
                     $("#table tbody tr:eq("+value+") td:last").find(".addRow,.deleteRow").show();
                 });
             },
            /**
         * 当前行下添加一行
         * @param t
         */
        addGradeRow: function (t) {
            var maxIndex = 1;

            $(".index").each(function(){
                maxIndex ++;
                // maxIndex ++;
                // var iid = $(this).attr("data-index");
                // if(maxIndex < iid){
                //     maxIndex = iid;
                // }
            });
            // console.log($('#trTpl').find(".ctr").html());
            var tr = $(t).parent().parent().parent();
            tr.after(tr.clone());
            //设置行的disp
            $(tr).next().find("td.index").show();
            var newIndex = 1 + parseInt(maxIndex);
            console.log("new Index "+ newIndex);
            $(tr).next().find("td.index").attr("data-index", newIndex);
            //$(tr).next().find("td.index").attr("rowspan",1);
            //设置index的值
            // $("#table tbody tr:last").find("td.index").show();
            sceneUtil.gradeTrInit( $(tr).next());//优化初始化
            // sceneUtil.rowspan();
            //获取当前行行数
            //$(t).append( $(t).parent().parent().parent().clone(true))
        },
        /**
         * 当前行下添加一行
         * @param t
         */
        addSceneRow: function (t) {
            // console.log($('#trTpl').find(".ctr").html());
            var tr = $(t).parent().parent().parent();
            tr.after(tr.clone());
            //动作修改么
            //设置行的disp
            sceneUtil.sceneTrInit( $(tr).next());//优化初始化
        }
            ,
             /**
             * 删除某一条件
             */
             deleteCon:function (t) {
                 $(t).parent().remove();

             },
        /**
         * 删除某一条件
         */
        deleteAc:function (t) {
           var len =  $(t).parent().parent().parent().children().length;
           if(len == 1){
                sceneUtil.addActionLi(t);
            }
            $(t).parent().parent().remove();
        },
            /**
             * 删除当前行
             * @param t
             */
            deleteRow: function (t) {
                var tr = $(t).parent().parent().parent();
                if(tr.index() == 0){
                    layer.msg("第一行初始化数据不可删除");
                    return;
                }
                var rowspan = $(tr).find("td.index").attr("rowspan");
                if(rowspan > 1){
                    $(tr).next().find("td.index").show();
                }
                $(t).parent().parent().parent().remove();
                sceneUtil.rowspan();
                sceneUtil.showAddAndDelete();
            }
            ,
        deleteSceneRow: function (t) {
            var tr = $(t).parent().parent().parent();
            if(tr.index() == 0){
                layer.msg("第一行初始化数据不可删除");
                return;
            }
            $(t).parent().parent().parent().remove();
        }
        ,



        /**
             * 添加条件列
             */
            addCol: function () {

                $th = $("#tpl").find("th:eq(0)").clone();
                $td = $("#tpl").find("td:eq(0)").clone();
                $col = $("#tpl").find("col:eq(0)").clone();
                var index = $(".contion").length;
                index = index - 2;
                $("#table>colgroup>col:eq(" + index + ")").after($col);
                $("#table>thead>tr>th:eq(" + index + ")").after($th);

                //需要添加多少列
                $("#table>tbody>tr").each(function (i, e) {
                    var td = $("#tpl").find("td:eq(0)").clone();
                    $(e).find("td:eq(" + index + ")").after(td);
                    // $($(e).find("td:eq("+index+")")[0]).after(td);
                });
                init();
            }
            ,
            /**
             * 添加动作列
             */
            addActionCol: function () {

                $th = $("#tpl").find("th:eq(1)").clone();
                $td = $("#tpl").find("td:eq(1)").clone();
                $col = $("#tpl").find("col:eq(1)").clone();
                var index = $("#table thead tr th").length;
                index = index - 2;
                //  index = index - 2;
                $("#table>colgroup>col:eq(" + index + ")").after($col);
                $("#table>thead>tr>th:eq(" + index + ")").after($th);

                //需要添加多少列
                $("#table>tbody>tr").each(function (i, e) {
                    var td = $("#tpl").find("td:eq(1)").clone();
                    $(e).find("td:eq(" + index + ")").after(td);
                    // $($(e).find("td:eq("+index+")")[0]).after(td);
                });
                init();
            },

            /**
             * 添加条件列
             */
            deleteCol: function (t) {
                var td = $(t).parent();
                var index = td.index();
                $("#table>colgroup>col:eq(" + index + ")").remove();

                $("#table>thead>tr>th:eq(" + index + ")").remove();
                $("#table>tbody>tr").each(function (i, e) {
                    var td = $("#tpl").find("td").clone();
                    $(e).find("td:eq(" + index + ")").remove();
                });
            },
        /**
         * 命名项
         * @param t
         */
        reGroupName:function(t){
            var name = $(t).next().text() +"."+$(t).next().next().text();
           var groupNameO =  $(t).parent().parent().parent().prev().find(".groupName");
           $(groupNameO).text(name);
            $(groupNameO).data("value",name);
            $(groupNameO).attr("data-value",name);

        },
            /**
             * 合并
             */
            hb: function () {
                $("#table").rowspan(0); //以第一列合并可用，但是会影响后面的新增，或删除操作
            },
        rowspan:function(){
                var oldIndex = -1;
                var rowspan = 1;
            //合并第一列 的
            $("#table tbody tr td.index").each(function (i) {
                var index = $(this).data("index");
                if(oldIndex == index){
                    rowspan = rowspan + 1;
                    if(rowspan > 1){
                        $(this).hide();
                    }
                }else{
                    //初始化
                    oldIndex = index;
                    rowspan = 1;
                }
                $('#table tbody tr td.index').eq(i-rowspan+1).attr("rowspan",rowspan);
            })
        },
        rowspan4grade:function(){
            var oldIndex = -1;
            var rowspan = 1;
            //合并第一列 的
            $("#tableView tbody tr td.index").each(function (i) {
                var index = $(this).attr("data-index");
                if(oldIndex == index){
                    rowspan = rowspan + 1;
                    if(rowspan > 1){
                        $(this).hide();
                    }
                }else{
                    //初始化
                    oldIndex = index;
                    rowspan = 1;
                }
                $('#tableView tbody tr td.index').eq(i-rowspan+1).attr("rowspan",rowspan);
            })
        },


        /**
         * 添加行内行
         * @param t
         */
        addTypeTr:function (t) {
            // console.log($('#trTpl').find(".ctr").html());
            var tr = $(t).parent().parent();

            var rowspan = $(tr).find("td.index").attr("rowspan");
            var copTr = tr;
            for(var i = 1; i < rowspan;i++){
                copTr = $(copTr).next();
            }
            copTr.after(copTr.clone());
            //合并
            sceneUtil.rowspan();
            sceneUtil.gradeTrInit($(copTr).next());
        },
        addCon:function (t) {
            var h =   $(t).parent().find("ul li:last").html();
            var li = '<li>'+h+'</li>';
            if(h == undefined || h == ''){
               // layer.msg("当前没有数据哦");
                li = '<li>\n' +
                    '                <a href="#" class="entityC" data-value="">对象 &nbsp; </a>.\n' +
                    '                <a href="#" class="itemC" data-value="">变量&nbsp; </a> &nbsp;\n' +
                    '                <a href="#" data-value="" class="con">条件 &nbsp; </a>&nbsp;\n' +
                    '                <a href="#" data-value="" class="val">值&nbsp;&nbsp;</a>\n' +
                    '                <a href="javascript:void(0); " class="deleteCon" onclick="sceneUtil.deleteCon(this)"> <i\n' +
                    '            class="layui-icon">&#x1006;</i></a>\n' +
                    '                </li>';

            }
            $(t).parent().find("ul").append(li);
           // sceneUtil.gradeInit();
            //设置条件值为空
            var a = $(t).parent().find("ul li").last().find(".val");
            a.attr("data-value","");
            a.attr("data-key","");
            a.text("输入");
            a.removeClass("conditionVal");
            a.removeClass("conditionEntity");
            //优化，仅仅设置条件项
            sceneUtil.conditionInit( $(t).parent().find("ul li").last());
        },
        /**
         * 添加动作行
         * @param t
         */
        addActionLi:function (t) {
            var h =  ' <a href="#" class="actionType" data-value="">请选择动作 &nbsp; </a>\n' +
                '                        <div class="param">\n' +
                '                        </div>\n' +
                '                        <div class="actionBar">\n' +
                '                            <a href="javascript:void(0); " title="添加" class="addAct"\n' +
                '                               onclick="sceneUtil.addActionLi(this)"> <i\n' +
                '                                    class="layui-icon">&#xe654;</i></a>\n' +
                '                            <a href="javascript:void(0); " class="deleteCon" title="删除该动作" onclick="sceneUtil.deleteAc(this)"> <i\n' +
                '                                    class="layui-icon">&#x1006;</i></a>\n' +
                '                        </div>';
            var li = '<li>'+h+'</li>';
            $(t).parent().parent().after(li);
            //设置点击事件
            sceneUtil.actionInit( $(t).parent().parent().next());
        },
            /*
             *值的编辑初始化 .val -> $('.val')
             */
            bandOneValInit: function (obj) {
                obj.editable('destroy');
                obj.editable({
                    type: "text",                //编辑框的类型。支持text|textarea|select|date|checklist等
                    title: "值",              //编辑框的标题
                    disabled: false,             //是否禁用编辑
                    emptytext: "空文本",          //空值的默认文本
                    mode: "popup",              //编辑框的模式：支持popup和inline两种模式，默认是popup
                    onblur: "submit",
                    validate: function (value) { //字段验证
                        if (!$.trim(value)) {
                            return '不能为空';
                        }
                        //特殊符号验证
                        var re = /[@#]/g;
                        // var re =/[`~!@#$%^&*_+<>{}\/'[\]]/im;
                        if (re.test(value))
                        {
                            return '不能输入特殊字符';
                        }
                        $(this).attr("data-value", value);
                    }
                });
            },
        /**
         * 导入页面打开
         */
        openImport:function (type) {
            var url = "/activiti/ui/src/html/decision/import_entity.html";
            if(type == 1){

            }else{
                url = "/activiti/ui/src/html/decision/import_action.html";
            }
            //打开导入面板
            $.get(url,function(html){
                layer.open({
                    type: 1,
                    skin: 'layui-layer-rim', //加上边框
                    maxmin: false,
                    title: false,
                    shadeClose: true, // 点击遮罩关闭层
                    area: ['550px', '400px'],
                    content: html,
                    btn: ['完成'],
                    shade:0.1,
                    success: function (layero, index) {
                        if(type == 1){
                           // myUtil.business.init('',$("#businessDiv"));
                            //sceneUtil.entitySelectInit(); 有了业务线
                            //初始化 实体 已导入的数据
                            sceneUtil.initEntitys();
                        }else{
                            //sceneUtil.actionSelectInit();
                            //初始化 实体
                            sceneUtil.initActions();
                        }
                        myUtil.business.init('',$("#businessDiv"),type);
                    }
                });

           });
        },
        /**
         * 导入页面打开,导入评分卡和决策
         */
        openImportDecision:function (type) {
            var url = "/activiti/ui/src/html/decision/import_scene.html";
            if(type == 1){

            }else{
                url = "/activiti/ui/src/html/decision/import_grade.html";
            }
            //打开导入面板
            $.get(url,function(html){
                layer.open({
                    type: 1,
                    skin: 'layui-layer-rim', //加上边框
                    maxmin: false,
                    title: false,
                    shadeClose: true, // 点击遮罩关闭层
                    area: ['550px', '400px'],
                    content: html,
                    btn: ['完成','取消'],
                    shade:0.1,
                    yes:function(index){
                        //初始化数据
                       var sceneId =  active.getCheckData();
                       if(sceneId > 0 ){
                           layer.confirm('导入将清空当前所有规则配置，是否继续', {
                               btn: ['继续','不了'] //按钮
                           }, function(){
                               sceneUtil.isEdit = false;
                               if(type == 1){
                                   sceneUtil.openSceneRuleInit(sceneId);
                               }
                               else {
                                   sceneUtil.openGradeRuleInit(sceneId);
                               }
                           }, function(){
                           });
                           sceneUtil.isEdit = true;
                           layer.close(index);
                       }
                        //初始化数据
                      //  getRuleData(sceneId);
                  }
                });

            });
        },
        /**
         * 打开定义评分卡的弹窗页面
         * @param sceneId
         */
        openGradeRuleInit:function (sceneId) {

            var tableNoDataPt = document.getElementById('table').innerHTML;
            var index =   layer.msg("数据加载中", {icon: 16, time: 10000,anim: -1,fixed: !1});
            $.get('/rule/service/rule/getGradeCardAll',{'sceneId':sceneId},function(data){
                if(data.code == '0'){
                    var result = data.data;
                    var hasWeight = result.hasWeight;
                    var getTpl = tableTp.innerHTML
                        ,view = document.getElementById('table');
                    laytpl(getTpl).render(result, function(html){
                        view.innerHTML = html;
                    });
                    //初始化 实体类的值
                    if(sceneUtil.isEdit){
                        sceneUtil.sceneId = sceneId;
                    }

                    sceneUtil.dataInit.entityBank();
                    sceneUtil.dataInit.actionBank();
                    sceneUtil.gradeInit();
                    //是否有权值
                    if(hasWeight > 0){
                        $("#openQz").attr("checked",true);
                        $("#table tbody tr td .qzdiv").show();
                        form.render('checkbox');
                    }
                }else{
                    $("#table").html(tableNoDataPt);
                    //初始化 实体类的值
                    if(sceneUtil.isEdit){
                        sceneUtil.sceneId = sceneId;
                    }
                    sceneUtil.gradeInit();
                }
                layer.close(index);


            },'json');
        },
        /**
         * 打开定义评分卡的弹窗页面
         * @param sceneId
         */
        openSceneRuleInit:function (sceneId) {

            var tableNoDataPt = document.getElementById('table').innerHTML;
            var index =   layer.msg("数据加载中", {icon: 16, time: 10000,anim: -1,fixed: !1});
            $.get('/rule/service/rule/getAll',{'sceneId':sceneId},function(data){
                if(data.code == '0'){
                    var result = data.data;
                    var hasWeight = result.hasWeight;
                    var getTpl = tableTp.innerHTML
                        ,view = document.getElementById('table');
                    laytpl(getTpl).render(result, function(html){
                        view.innerHTML = html;
                    });
                    //初始化 实体类的值
                    if(sceneUtil.isEdit){
                        sceneUtil.sceneId = sceneId;
                    }
                    sceneUtil.dataInit.entityBank();
                    sceneUtil.dataInit.actionBank();
                    sceneUtil.sceneInit();
                }else{
                    $("#table").html(tableNoDataPt);
                    //初始化 实体类的值
                    if(sceneUtil.isEdit){
                        sceneUtil.sceneId = sceneId;
                    }
                    sceneUtil.sceneInit();
                }
                layer.close(index);


            },'json');
        },
        /***
         * 品牌下拉初始化
         * @param value
         * @param list
         */
        entitySelectInit:function(businessId){

            //if(sceneUtil.entityImport == [] || sceneUtil.entityImport.length == 0){

                $.get("/rule/service/entityInfo/getEntitysAll",{"businessId":businessId},function(data){
                    var result = {list : data.data
                    }
                    var getTpl = tableTps.innerHTML
                        ,view = document.getElementById('entitSelectId');
                    laytpl(getTpl).render(result, function(html){
                        view.innerHTML = html;
                    });
                    sceneUtil.entityImport = data.data;
                    form.render('select');
                },'json');
            /*}else{
                var result = {list : sceneUtil.entityImport
                }
                var getTpl = tableTps.innerHTML
                    ,view = document.getElementById('entitSelectId');
                laytpl(getTpl).render(result, function(html){
                    view.innerHTML = html;
                });
                form.render('select');
            }*/
    },
        /***
         * 动作下拉初始化
         * @param value
         * @param list
         */
        actionSelectInit:function(businessId){

           // if(sceneUtil.actionImport == [] || sceneUtil.actionImport.length == 0){

                $.get("/rule/service/actionInfo/getAll",{"businessId":businessId},function(data){
                    var result = {list : data.data
                    }
                    var getTpl = actionTableTps.innerHTML
                        ,view = document.getElementById('actionSelectId');
                    laytpl(getTpl).render(result, function(html){
                        view.innerHTML = html;
                    });
                    sceneUtil.actionImport = data.data;
                    form.render('select');
                },'json');
        /*    }else{
                var result = {list : sceneUtil.actionImport
                }
                var getTpl = actionTableTps.innerHTML
                    ,view = document.getElementById('actionSelectId');
                laytpl(getTpl).render(result, function(html){
                    view.innerHTML = html;
                });
                form.render('select');
            }*/
        },
        /**
         * 动态导入一条数据
         */
        addEntitys:function () {
            var entityVal = $("#entitSelectId").val();
            var  entity = {};
            var flag = true;
            //判断是否已经被加过
            sceneUtil.data.entitysBank.forEach(function(element){
                if(entityVal == element.value){
                    flag = false;
                    return;
                }
            });
            if(entityVal == '' || entityVal == undefined){
                layer.msg("还没有选择哦");
                return false;
            }
            if(!flag){
                layer.msg("不可重复添加");
                return false;
            }
            //导入数据
            for(var i = 0; i < sceneUtil.entityImport.length;i++){
                if(entityVal == sceneUtil.entityImport[i].value){
                    entity = sceneUtil.entityImport[i];
                    console.log(entity);
                    break;
                }
            }
            //设置数据
            sceneUtil.data.entitysBank.push(entity);
            var h = '<li>\n' +
                '                <div class="layui-form-item">\n' +
                '                    <label class="layui-form-label">对象名</label>\n' +
                '                    <div class="layui-input-inline">\n' +
                '                        <input type="text" name="" data-value="'+entity.value+'" value="'+entity.text+'" readonly="readonly" autocomplete="off"\n' +
                '                               class="layui-input">\n' +
                '                    </div>\n' +
                '                    <div class="layui-form-mid layui-word-aux">\n' +
                '                        <a class="layui-icon edel" onclick="sceneUtil.deleteEntity(this,'+entity.value+')" title="删除" href="javascript:void(0)" style="font-size: 18px">&#xe640;</a>\n' +
                '                    </div>\n' +
                '                </div>\n' +
                '            </li>';
            $("#entityUiDiv").append(h);
            form.render('select');
            //实体对象绑定
            sceneUtil.bandSelectValInit_entity($(".entityC"),sceneUtil.data.entitysBank,"选择对象");
            //新方法
            $(".actionEntity").each(function(){
                var $a = $(this);
                sceneUtil.actionParam.paramVaEntityBack($a);
            });


        },
        /**
         * 添加动作
         */
        addActions:function () {
            var actionVal = $("#actionSelectId").val();
            var  action = {};
            var flag = true;
            //判重复
            sceneUtil.data.actionBank.forEach(function(element){
                if(actionVal == element.value){
                    flag = false;
                    return;
                }
            });
            if(actionVal == '' || actionVal == undefined){
                layer.msg("还没有选择哦");
                return false;
            }
            if(!flag){
                layer.msg("不可重复添加");
                return false;
            }
            //获取当前的动作体
            for(var i = 0; i < sceneUtil.actionImport.length;i++){
                if(actionVal == sceneUtil.actionImport[i].value){
                    action = sceneUtil.actionImport[i];
                    break;
                }
            }
            //动作库添加
            sceneUtil.data.actionBank.push(action);
            //重新初始动作下拉
            //绑定动作
            sceneUtil.bandSelectValInit_action($(".actionType"),sceneUtil.data.actionBank,"选择动作");
            var h = '<li>\n' +
                '                <div class="layui-form-item">\n' +
                '                    <label class="layui-form-label">动作名</label>\n' +
                '                    <div class="layui-input-inline">\n' +
                '                        <input type="text" name="" data-value="'+action.value+'" value="'+action.text+'" readonly="readonly" autocomplete="off"\n' +
                '                               class="layui-input">\n' +
                '                    </div>\n' +
                '                    <div class="layui-form-mid layui-word-aux">\n' +
                '                        <a class="layui-icon edel" onclick="sceneUtil.deleteAction(this,'+action.value+')" title="删除" href="javascript:void(0)" style="font-size: 18px">&#xe640;</a>\n' +
                '                    </div>\n' +
                '                </div>\n' +
                '            </li>';
            $("#actionUiDiv").append(h);
            form.render('select');
        },
        /**
         * 添加实体对象
         */
        initEntitys:function () {
            $("#entityUiDiv").html("");
            sceneUtil.data.entitysBank.forEach(function(entity){
                    var h = '<li>\n' +
                        '                <div class="layui-form-item">\n' +
                        '                    <label class="layui-form-label">对象名</label>\n' +
                        '                    <div class="layui-input-inline">\n' +
                        '                        <input type="text" name="" data-value="'+entity.value+'" value="'+entity.text+'" readonly="readonly" autocomplete="off"\n' +
                        '                               class="layui-input">\n' +
                        '                    </div>\n' +
                        '                    <div class="layui-form-mid layui-word-aux">\n' +
                        '                        <a class="layui-icon edel" onclick="sceneUtil.deleteEntity(this,'+entity.value+')" title="删除" href="javascript:void(0)" style="font-size: 18px">&#xe640;</a>\n' +
                        '                    </div>\n' +
                        '                </div>\n' +
                        '            </li>';
                    $("#entityUiDiv").append(h);
            });
            form.render('select');
        },
        /**
         *初始化动作库
         */
        initActions:function () {
            $("#actionUiDiv").html("");
            sceneUtil.data.actionBank.forEach(function(action){
                    var h = '<li>\n' +
                        '                <div class="layui-form-item">\n' +
                        '                    <label class="layui-form-label">动作名</label>\n' +
                        '                    <div class="layui-input-inline">\n' +
                        '                        <input type="text" name="" data-value="'+action.value+'" value="'+action.text+'" readonly="readonly" autocomplete="off"\n' +
                        '                               class="layui-input">\n' +
                        '                    </div>\n' +
                        '                    <div class="layui-form-mid layui-word-aux">\n' +
                        '                        <a class="layui-icon edel" onclick="sceneUtil.deleteAction(this,'+action.value+')" title="删除" href="javascript:void(0)" style="font-size: 18px">&#xe640;</a>\n' +
                        '                    </div>\n' +
                        '                </div>\n' +
                        '            </li>';
                    $("#actionUiDiv").append(h);
            });
            form.render('select');
        },
        /**
         * 删除某实体类元素
         * @param t
         * @param v
             */
        deleteEntity:function (t,v) {
            var result = [];
            var f = false;
            //查看是否有引用
            $(".entityC").each(function(){
                var vv = $(this).attr("data-value");
                if( vv == v ){
                    f = true;
                }
            });
            //无法删除
            if(f){
                layer.msg("该对象正在被使用，无法删除");
                return;
            }
            sceneUtil.data.entitysBank.forEach(function(element){
                if(v != element.value){
                    result.push(element);
                }
            });

            sceneUtil.data.entitysBank = result;
            $(t).parent().parent().parent().remove();
        },
        /**
         * 删除某动作类元素
         * @param t
         * @param v
         */
        deleteAction:function (t,v) {
            var result = [];
            var f = false;
            //查看是否有引用
            $(".actionType").each(function(){
                var vv = $(this).attr("data-value");
                if( vv == v ){
                    f = true;
                }
            });
            //无法删除
            if(f){
                layer.msg("该对象正在被使用，无法删除");
                return;
            }

            sceneUtil.data.actionBank.forEach(function(element){
                if(v != element.value){
                    result.push(element);
                }
            });
            sceneUtil.data.actionBank = result;
            $(t).parent().parent().parent().remove();
        },
        /**
         * 一级下拉
         * @param obj 对象
         * @param data 数据
         * @param text 标题
         */
        bandSelectValInit: function (obj,data,text,callback) {
            //摧毁
            obj.editable('destroy');
            obj.editable({
                type: "select",              //编辑框的类型。支持text|textarea|select|date|checklist等
                // value:'2',
                source: data,
                title: text,           //编辑框的标题
                disabled: false,           //是否禁用编辑
                // emptytext: "选择对象",       //空值的默认文本
                mode: "popup",            //编辑框的模式：支持popup和inline两种模式，默认是popup
                onblur: "submit",
                validate: callback,
            });
        },
        /**
         * 一级下拉
         * @param obj 对象
         * @param data 数据
         * @param text 标题
         */
        bandSelectValInit_entity: function (obj,data,text) {
            //摧毁
            obj.editable('destroy');
            obj.editable({
                type: "select",              //编辑框的类型。支持text|textarea|select|date|checklist等
                // value:'2',
                source: data,
                title: text,           //编辑框的标题
                disabled: false,           //是否禁用编辑
                // emptytext: "选择对象",       //空值的默认文本
                mode: "popup",            //编辑框的模式：支持popup和inline两种模式，默认是popup
                onblur: "submit",
                validate: function(value){
                    if (!$.trim(value)) {
                        return '不能为空';
                    }
                    $(this).attr("data-value", value);
                    $(this).parent().find(".itemC").text("请选择");
                    $(this).parent().find(".itemC").attr("data-value", "");
                    sceneUtil.bandSelectValInit_item(this,sceneUtil.data.entitysBank,value);
                    //触发变量的选择
                }
            });
        },
        /**
         * 一级下拉
         * @param obj 对象
         * @param data 数据
         * @param text 标题
         */
        bandSelectValInit_action: function (obj,data,text) {
            //摧毁
            obj.editable('destroy');
            obj.editable({
                type: "select",              //编辑框的类型。支持text|textarea|select|date|checklist等
                // value:'2',
                source: data,
                title: text,           //编辑框的标题
                disabled: false,           //是否禁用编辑
                // emptytext: "选择对象",       //空值的默认文本
                mode: "popup",            //编辑框的模式：支持popup和inline两种模式，默认是popup
                onblur: "submit",
                validate: function(value){
                    if (!$.trim(value)) {
                        return '不能为空';
                    }
                    $(this).attr("data-value",value);
                    //触发变量的选择
                    sceneUtil.actionParam.setParamsHtmls(value,$(this).parent());
                }
            });
        },
        /**
         * 动作参数设置
         */
        actionParam:{
            actionParamData:[],
            paramValue:-1,
            paramVaEntityBack:function (a_obj) {
                $(a_obj).editable('destroy');
                //新方法
                $(a_obj).submenu({
                    data: sceneUtil.data.entitysBank,
                    callback: function (obj, value) {
                        $(a_obj).attr("data-value",value);
                        $(a_obj).attr("data-key", $(obj).attr("key"));
                        $(a_obj).text($(obj).attr("ptext")+"."+ $(obj).text());
                        //console.log(obj, value);
                    }
                });

            },
            //获取参数集合
            getParamsDatas:function (actionId) {

                for(var i = 0;i < sceneUtil.data.actionBank.length;i++){
                    if(actionId ==  sceneUtil.data.actionBank[i].value){
                        sceneUtil.actionParam.actionParamData = sceneUtil.data.actionBank[i].paramInfoList;
                        break;
                    }
                }

            },
            //获取html代码串，并且赋值
           setParamsHtmls:function(actionId,li){
                //得到参数集合
               sceneUtil.actionParam.getParamsDatas(actionId);
               console.log(sceneUtil.actionParam.actionParamData);
               $(li).find(".param").html("");
               var html = '';
               for(var i = 0;i < sceneUtil.actionParam.actionParamData.length;i++){
                   var param = sceneUtil.actionParam.actionParamData[i];
                   if(i == 0){
                       html += '(<a href="#" paramId="'+param.value+'" class="actionVal" data-value="">'+param.text+'</a>';
                   }else{
                       html += ',<a href="#" paramId="'+param.value+'" class="actionVal" data-value="">'+param.text+'</a>';
                   }
               }
               html += ')';
               $(li).find(".param").html(html);
               //设置下拉事件
               $(li).find(".param .actionVal").each(function(){
                   var actionValA = $(this);
                $(this).submenu({
                    data: [{value:1,text:'输入值'},{value:2,text:'选择变量'}],type:1,
                    callback: function (obj, value) {
                        $(actionValA).editable('destroy');
                        if(value == 1){
                            //清除原来的事件
                            $(actionValA).unbind("click");
                            //加载新的
                            sceneUtil.bandOneValInit( $(actionValA));
                            $(actionValA).addClass("actionVal");
                            $(actionValA).attr("data-value","");
                            $(actionValA).text("请输入");

                        }else{
                            $(actionValA).attr("data-value","");
                            $(actionValA).text("请选择变量");
                            $(actionValA).addClass("actionEntity");
                            sceneUtil.actionParam.paramVaEntityBack(actionValA);

                        }

                        console.log(obj, value);
                    }
                });
               });
            }

        },
        /**
         * 条件值 输入框重新设置
         */
            //获取html代码串，并且赋值
        conditionValInput:function(obj){
                //得到参数集合
                //设置下拉事件
                    var actionValA = $(obj);
                    $(obj).submenu({
                        data: [{value:1,text:'输入值'},{value:2,text:'选择变量'}],type:1,
                        callback: function (obj, value) {
                            $(actionValA).editable('destroy');
                            if(value == 1){
                                //清除原来的事件
                                $(actionValA).unbind("click");
                                //加载新的
                                sceneUtil.bandOneValInit( $(actionValA));
                                $(actionValA).addClass("conditionVal");
                                $(actionValA).attr("data-value","");
                                $(actionValA).text("请输入");
                            }else{
                                $(actionValA).attr("data-value","");
                                $(actionValA).text("请选择变量");
                                $(actionValA).addClass("conditionEntity");
                                sceneUtil.actionParam.paramVaEntityBack(actionValA);
                            }
                            console.log(obj, value);
                        }
                    });
        },
        /**
         * 重置变量集合
         * @param entityId
         * @param t
         */
        bandSelectValInit_item: function(obj,entitys,entityId) {
            var items = [];
            for(var i=0;i<entitys.length;i++){
                var enid = entitys[i].value;
                if(enid == entityId){
                    items = entitys[i].sons;
                    break;
                }
            }

            $(obj).parent().find(".itemC").editable('destroy');
            //变量
            $(obj).parent().find(".itemC").editable({
                type: "select",              //编辑框的类型。支持text|textarea|select|date|checklist等
                source: items,
                title: "变量名",           //编辑框的标题
                disabled: false,           //是否禁用编辑
                emptytext: "选择变量",       //空值的默认文本
                mode: "popup",            //编辑框的模式：支持popup和inline两种模式，默认是popup
                onblur:"submit",
                validate: function (value) { //字段验证
                    if (!$.trim(value)) {
                        return '不能为空';
                    }
                    $(this).attr("data-value",value);
                    //重置
                    $(obj).parent().find(".val").text("输入  ");
                    $(obj).parent().find(".val").data("value","");
                    //设置使用的变量
                    //sceneUtil.addTtemsBank(value);
                    //是否设置常量集合
                    sceneUtil.bandSelectValInit_constants(this,items,value);
                }
            });

        },
        /**
         * 设置使用过的变量值
         * @param value
             */
        addTtemsBank:function (value) {
            var flag = true;
          for (var i = 0; i < sceneUtil.data.itemsBank.length;i++){
              if(value == sceneUtil.data.itemsBank[i]){
                  flag = false;
                  break;
              }
          }
          if(flag){
              sceneUtil.data.itemsBank.push(value);
          }
        },
        /**
         * 重置常量集合
         * @param entityId
         * @param t
         */
        bandSelectValInit_constants: function(obj,items,itemId)  {
            var constants = [];
            var hasCons = false;
            //找到等于的值的地方
            for(var i=0;i<items.length;i++){
                var iid = items[i].value;
                if(iid == itemId){
                    if(items[i].sons != undefined && items[i].sons.length > 0 && items[i].sons != null)
                    constants = items[i].sons;
                    hasCons = true;
                    break;
                }
            }
            $(obj).parent().find(".val").editable('destroy');

            if(constants.length > 0){
                $(obj).parent().find(".val").unbind("click");
                //选择常量
                $(obj).parent().find(".val").editable({
                    type: "select",              //编辑框的类型。支持text|textarea|select|date|checklist等
                    source: constants,
                    title: "选择常量",           //编辑框的标题
                    disabled: false,           //是否禁用编辑
                    emptytext: "选择常量",       //空值的默认文本
                    mode: "popup",            //编辑框的模式：支持popup和inline两种模式，默认是popup
                    onblur:"submit",
                    validate: function (value) { //字段验证
                        if (!$.trim(value)) {
                            return '不能为空';
                        }
                        $(this).attr("data-value",value);
                    }
                });
            }else{
                //设置 输入框模式和可选变量模式
                // TODO: 改变输入模式
                sceneUtil.conditionValInput($(obj).parent().find(".val"));
                // sceneUtil.bandOneValInit( $(obj).parent().find(".val"));
            }

        },
        /**
         * 一级下拉
         * @param obj 对象
         * @param data 数据
         * @param text 标题
         */
        bandSelectValInit_condition: function (obj,data,text) {
            //摧毁
            obj.editable('destroy');
            obj.editable({
                type: "select",              //编辑框的类型。支持text|textarea|select|date|checklist等
                // value:'2',
                source: data,
                title: text,           //编辑框的标题
                disabled: false,           //是否禁用编辑
                // emptytext: "选择对象",       //空值的默认文本
                mode: "popup",            //编辑框的模式：支持popup和inline两种模式，默认是popup
                onblur: "submit",
                validate: function(value){
                    if (!$.trim(value)) {
                        return '不能为空';
                    }
                    if (value == '===') {
                        $(this).parent().find(".val").text("");
                        $(this).parent().find(".val").attr("data-value", "true");
                    } else {
                        //$(this).parent().find(".val").text("值");
                        //$(this).parent().find(".val").attr("data-value", "");
                    }
                    $(this).attr("data-value", value);
                }
            });
        },

            /**
             * 初始化方法
             */
            gradeInit: function() {
                // headItem();
                $("#table tbody tr td.index").hover(function () {
                    $(this).find(".addTypeTr").show();
                },function () {
                    $(this).find(".addTypeTr").hide();
                });
                $("#table tbody tr td ul li").hover(function () {
                    var groupNameO =  $(this).parent().parent().prev().find(".groupName");
                    var display = $(groupNameO).parent().parent().css('display');
                    if(display != 'none'){
                        $(this).find(".reGroupName").show();
                    }
                    if($(this).index() > 0){
                        //判断是否是第一条
                        $(this).find(".deleteCon").show();
                    }

                },function () {
                    var groupNameO =  $(this).parent().parent().prev().find(".groupName");
                    var display = $(groupNameO).parent().parent().css('display');
                    if(display != 'none'){
                        $(this).find(".reGroupName").hide();
                    }
                    $(this).find(".deleteCon").hide();
                });
                sceneUtil.showAddAndDelete();
                //合并
                sceneUtil.rowspan();
                //绑定条件输入值得输入方式
                sceneUtil.bandOneValInit( $('.val'));
                sceneUtil.bandOneValInit( $('.groupName'));
                //绑定动作值得输入框
                sceneUtil.bandOneValInit( $('.actionVal'));
                //条件绑定
                sceneUtil.bandSelectValInit_condition($(".con"),sceneUtil.data.condition,"选择条件");
                //实体对象绑定
                sceneUtil.bandSelectValInit_entity($(".entityC"),sceneUtil.data.entitysBank,"选择对象");
                //绑定所有的变量
                $(".entityC").each(function(){
                    var entitys = sceneUtil.data.entitysBank;
                    var entityId = $(this).data("value");
                    var iid = $(this).next().data("value");
                    sceneUtil.bandSelectValInit_item(this,entitys,entityId);
                    //绑定所有常量
                    var items = [];
                    for(var i=0;i<entitys.length;i++){
                        var enid = entitys[i].value;
                        if(enid == entityId){
                            items = entitys[i].sons;
                            break;
                        }
                    }
                    sceneUtil.bandSelectValInit_constants(this,items,iid);
                });
            },
        sceneTrInit:function (tr) {
            //绑定删除按钮
            $(tr).find("td ul li").hover(function () {
                $(this).find(".deleteCon").show();
                $(this).find(".addAct").show();
            },function () {
                $(this).find(".deleteCon").hide();
                $(this).find(".addAct").hide();
            });
            //绑定条件输入值得输入方式
            sceneUtil.bandOneValInit(  $(tr).find('.val'));
            sceneUtil.bandOneValInit( $(tr).find('.groupName'));
            //绑定动作值得输入框
            sceneUtil.bandOneValInit( $(tr).find('.actionVal'));
            //选变量绑定
            //动作输入选框绑定
           $(tr).find("td ul li div.param a.actionEntity").each(function () {
                sceneUtil.actionParam.paramVaEntityBack(this);
            });
            //条件绑定
            sceneUtil.bandSelectValInit_condition($(tr).find(".con"),sceneUtil.data.condition,"选择条件");
            //实体对象绑定
            sceneUtil.bandSelectValInit_entity($(tr).find(".entityC"),sceneUtil.data.entitysBank,"选择对象");
            //绑定所有的变量
            $(tr).find(".entityC").each(function(){
                var entitys = sceneUtil.data.entitysBank;
                var entityId = $(this).data("value");
                var iid = $(this).next().data("value");
                sceneUtil.bandSelectValInit_item(this,entitys,entityId);
                //绑定所有常量
                var items = [];
                for(var i=0;i<entitys.length;i++){
                    var enid = entitys[i].value;
                    if(enid == entityId){
                        items = entitys[i].sons;
                        break;
                    }
                }
                sceneUtil.bandSelectValInit_constants(this,items,iid);
            });
        },
        /**
         * 初始化方法
         */
        gradeTrInit: function(tr) {
            // headItem();
            //绑定添加按钮
            $(tr).find("td.index").hover(function () {
                $(this).find(".addTypeTr").show();
            },function () {
                $(this).find(".addTypeTr").hide();
            });
            //绑定删除按钮
            $(tr).find("td ul li").hover(function () {
                var groupNameO =  $(this).parent().parent().prev().find(".groupName");
                var display = $(groupNameO).parent().parent().css('display');
                if(display != 'none'){
                    $(this).find(".reGroupName").show();
                }
                if($(this).index() > 0){
                    //判断是否是第一条
                    $(this).find(".deleteCon").show();
                }

            },function () {
                var groupNameO =  $(this).parent().parent().prev().find(".groupName");
                var display = $(groupNameO).parent().parent().css('display');
                if(display != 'none'){
                    $(this).find(".reGroupName").hide();
                }
                $(this).find(".deleteCon").hide();
            });
            sceneUtil.showAddAndDelete();
            //合并
            sceneUtil.rowspan();
            //绑定条件输入值得输入方式
            sceneUtil.bandOneValInit(  $(tr).find('.val'));
            sceneUtil.bandOneValInit( $(tr).find('.groupName'));

            //绑定动作
           // sceneUtil.bandSelectValInit_action($(li).find(".actionType"),sceneUtil.data.actionBank,"选择动作");
            //绑定动作值得输入框
            sceneUtil.bandOneValInit( $(tr).find('.actionVal'));
            //条件绑定
            sceneUtil.bandSelectValInit_condition($(tr).find(".con"),sceneUtil.data.condition,"选择条件");
            //实体对象绑定
            sceneUtil.bandSelectValInit_entity($(tr).find(".entityC"),sceneUtil.data.entitysBank,"选择对象");
            //绑定所有的变量
            $(tr).find(".entityC").each(function(){
                var entitys = sceneUtil.data.entitysBank;
                var entityId = $(this).data("value");
                var iid = $(this).next().data("value");
                sceneUtil.bandSelectValInit_item(this,entitys,entityId);
                //绑定所有常量
                var items = [];
                for(var i=0;i<entitys.length;i++){
                    var enid = entitys[i].value;
                    if(enid == entityId){
                        items = entitys[i].sons;
                        break;
                    }
                }
                sceneUtil.bandSelectValInit_constants(this,items,iid);
            });
        },
        /**
         * 新增条件初始化
         * @param li
             */
        conditionInit:function (li) {
            //条件绑定
            sceneUtil.bandSelectValInit_condition($(li).find(".con"),sceneUtil.data.condition,"选择条件");
            //实体对象绑定
            sceneUtil.bandSelectValInit_entity($(li).find(".entityC"),sceneUtil.data.entitysBank,"选择对象");
            //绑定删除按钮
            $(li).hover(function () {
                if($(this).index() > 0){
                    //判断是否是第一条
                    $(this).find(".deleteCon").show();
                }
            },function () {
                $(this).find(".deleteCon").hide();
            });

            //绑定所有的变量
            $(li).find(".entityC").each(function(){
                var entitys = sceneUtil.data.entitysBank;
                var entityId = $(this).data("value");
                var iid = $(this).next().data("value");
                sceneUtil.bandSelectValInit_item(this,entitys,entityId);
                //绑定所有常量
                var items = [];
                for(var i=0;i<entitys.length;i++){
                    var enid = entitys[i].value;
                    if(enid == entityId){
                        items = entitys[i].sons;
                        break;
                    }
                }
                //绑定常量
                sceneUtil.bandSelectValInit_constants(this,items,iid);
            });
        },
        /**
         * 新增条件初始化
         * @param li
         */
        actionInit:function (li) {
              //绑定删除按钮，绑定添加按钮
            $(li).hover(function () {
                $(this).find(".deleteCon").show();
                $(this).find(".addAct").show();
            },function () {
                $(this).find(".deleteCon").hide();
                $(this).find(".addAct").hide();
            });
            //选择动作绑定
            //绑定动作
            sceneUtil.bandSelectValInit_action($(li).find(".actionType"),sceneUtil.data.actionBank,"选择动作");
            //手动输入值绑定
            sceneUtil.bandOneValInit( $(li).find('.actionVal'));
            //实体对象绑定
            sceneUtil.bandSelectValInit_entity($(li).find(".entityC"),sceneUtil.data.entitysBank,"选择对象");
            //绑定所有的变量
            $(li).find(".entityC").each(function(){
                var entitys = sceneUtil.data.entitysBank;
                var entityId = $(this).data("value");
                var iid = $(this).next().data("value");
                sceneUtil.bandSelectValInit_item(this,entitys,entityId);
                //绑定所有对象的变量
                var items = [];
                for(var i=0;i<entitys.length;i++){
                    var enid = entitys[i].value;
                    if(enid == entityId){
                        items = entitys[i].sons;
                        break;
                    }
                }
                sceneUtil.bandSelectValInit_constants(this,items,iid);
            });
        },
        /**
         * 初始化方法
         */
        sceneInit: function() {
            $("#table tbody tr td ul.con_ul li").hover(function () {
                if($(this).index() > 0){
                    //判断是否是第一条
                    $(this).find(".deleteCon").show();
                }
            },function () {
                $(this).find(".deleteCon").hide();
            });
            $("#table tbody tr td ul.action_ul li").hover(function () {
                $(this).find(".deleteCon").show();
                $(this).find(".addAct").show();
            },function () {
                $(this).find(".deleteCon").hide();
                $(this).find(".addAct").hide();
            });
            //绑定条件输入值得输入方式
            sceneUtil.bandOneValInit( $('.val'));
            //绑定动作
            sceneUtil.bandSelectValInit_action($(".actionType"),sceneUtil.data.actionBank,"选择动作");
            //绑定动作值得输入框
            sceneUtil.bandOneValInit( $('.actionVal'));
            //动作输入选框绑定
            $("#table tbody tr td ul li div.param a.actionEntity").each(function () {
                sceneUtil.actionParam.paramVaEntityBack(this);
            });

            //条件绑定
            sceneUtil.bandSelectValInit_condition($(".con"),sceneUtil.data.condition,"选择条件");
            //实体对象绑定
            sceneUtil.bandSelectValInit_entity($(".entityC"),sceneUtil.data.entitysBank,"选择对象");
            //绑定所有的变量
            $(".entityC").each(function(){
                var entitys = sceneUtil.data.entitysBank;
                var entityId = $(this).data("value");
                var iid = $(this).next().data("value");
                sceneUtil.bandSelectValInit_item(this,entitys,entityId);
                //绑定所有常量
                var items = [];
                for(var i=0;i<entitys.length;i++){
                    var enid = entitys[i].value;
                    if(enid == entityId){
                        items = entitys[i].sons;
                        break;
                    }
                }
                sceneUtil.bandSelectValInit_constants(this,items,iid);
            });
        },
        /**
         * 数据初始化
         */
        dataInit: {
                //数据变量库
                entityBank:function () {
                    $.ajax({
                        cache: true,
                        type: "get",
                        url: '/rule/service/entityInfo/getEntitysByScene',
                        data: {sceneId: sceneUtil.sceneId},// 你的formid
                        async: false,
                        success: function (da) {
                            //console.log(da);
                            if (da.code == 0) {
                                sceneUtil.data.entitysBank =  da.data;
                            } else {
                                layer.msg(da.msg);
                            }
                        }
                    });
                },
                //动作库导入
                actionBank:function () {
                    $.ajax({
                        cache: true,
                        type: "get",
                        url: '/rule/service/actionInfo/getByScene',
                        data: {sceneId: sceneUtil.sceneId},// 你的formid
                        async: false,
                        success: function (da) {
                            if (da.code == 0) {
                                sceneUtil.data.actionBank =  da.data;
                            } else {
                                layer.msg(da.msg);
                            }
                        }
                    });
                },
                //常量库导入
                dicBank:function () {
                        //if (sceneUtil.data.dicBank == [] || sceneUtil.data.dicBank.length < 1) {
                            $.ajax({
                                cache: true,
                                type: "get",
                                url: '/rule/service/variable/getByIds',
                                data: {ids: '1,2,3,4,5,6,7'},
                                async: false,
                                error: function (request) {
                                    alert("Connection error");
                                },
                                success: function (da) {
                                    if (da.code == 0) {
                                        actions = da.data;
                                    } else {
                                        layer.msg(da.msg);
                                    }
                                }
                            });
                       // }
                }

            },
            getGradeRuleData: function () {
                var subForms = [];
                //统一权值设置
                var weightIndexList = [];
                var groupNameList = [];
                sceneUtil.data.itemsBank = [];
                var groupList = [];
                $("#table>tbody>tr").each(function () {
                    subForm = {
                        //权值
                        group:{},
                        //条件集合
                        conditionInfos: [],
                        //动作集合
                        actionInfos: []
                    }
                    var conditionInfos = [];
                    var actionInfos = [];

                    var groupTd = $(this).find("td.index");
                    //拼分组
                    var groupIndex = $(groupTd).data("index");
                    var groupName = $(groupTd).find("span a.groupName").text();
                    var weight = ($(groupTd).find(".qz").val()== undefined || $(groupTd).find(".qz").val() == '')?1:$(groupTd).find(".qz").val();
                    //设置权值，和分组名
                   var hasGroup = false;
                    for(var i=0;i< groupList.length ;i++){
                        //查找 权值是否存在
                        if(groupIndex == groupList[i].groupIndex){
                            groupName = groupList[i].name;
                            weight = groupList[i].weight;
                            hasGroup = true;
                        }
                    }
                    if(hasGroup){

                    }else{
                        var group =  {index:groupIndex,name:groupName,weight:weight};
                        groupList.push(group);
                    }
                      /*  var exp=/^[+]{0,1}(\d+)$|^[+]{0,1}(\d+\.\d+)$/;
                        if(!exp.test(weight) ){
                            sceneUtil.flag = false;
                            layer.msg("权值只能为数字类型");
                            return ;
                        }*/
                        //weight = weightIndexList[groupIndex];
                        //groupName = groupName;
                    subForm.group = {index:groupIndex,name:groupName,weight:weight};
                    //拼条件

                    var conTd = $(this).find("td").eq(1);
                    $(conTd).find("ul li").each(function(i,e){
                        //拼条件 的变量 ，运算符 ，值
                        var entityv = $(e).find("a.entityC").attr("data-value");
                        var entityText = $(e).find("a.entityC").text();
                        var itemv = $(e).find("a.itemC").attr("data-value");
                        var itemText = $(e).find("a.itemC").text();

                        var ysf = $(e).find("a.con").attr("data-value");
                        var ysfText = $(e).find("a.con").text();
                        var val = $(e).find("a.val").attr("data-value");
                        var valText = $(e).find("a.val").text();


                        //变量类型的哦
                        if($(e).find("a.val").hasClass("conditionEntity")){
                            val = '#'+$(e).find("a.val").attr("data-key")+'#';
                        }

                        if(val == '' || val == undefined){
                            sceneUtil.flag = false;
                            layer.tips('条件值不能为空', $(e).find("a.val"), {
                                tips: 3,time:3000
                                ,tipsMore: true
                            });
                            return ;
                        }
                        if(ysf == '' || ysf == undefined){
                            sceneUtil.flag = false;
                            layer.tips('运算符不能为空', $(e).find("a.con"), {
                                tips: 3,time:3000
                                ,tipsMore: true
                            });
                            return ;
                        }
                        if(itemv == '' || itemv == undefined){
                            sceneUtil.flag = false;
                            layer.tips('变量对象不能为空', $(e).find("a.itemC"), {
                                tips: 3,time:3000
                                ,tipsMore: true
                            });
                            return ;
                        }
                        if (val == '' || ysf == '' || itemv == '') {
                            layer.msg('必选项不能为空');
                            sceneUtil.flag = false;
                            $(e).addClass("error");
                            return ;
                        }
                        var conditionInfo = {
                            conditionExpression: '$' + itemv + '$' + '' + ysf + '' + val,
                            conditionDesc: '$' + entityText + ':'+ itemText+ '$' + ysfText + '' + valText,
                            val: valText
                        }
                        conditionInfos.push(conditionInfo);
                        //使用了那些变量，重新赋值了
                        sceneUtil.addTtemsBank(itemv);
                    });
                    //拼动作，分数了
                    var actionTd = $(this).find("td").eq(2);
                    var actionVal = $(actionTd).find("a.actionVal").attr("data-value");
                    var actionType = $(actionTd).find("a.actionVal").attr("actionParamId");
                    //验证评分卡是否数字类型
                    var re = /^[0-9]+.?[0-9]*$/;   //判断字符串是否为数字     //判断正整数 /^[1-9]+[0-9]*]*$/
                    if(actionVal == '' || !re.test(actionVal) || actionVal == undefined){
                        layer.msg("分值必须为数字，且不为空",{time:2000});
                        sceneUtil.flag = false;
                        layer.tips('分值不能为空且只能为数值', $(actionTd).find("a.actionVal"), {
                            tips: 3,time:3000
                            ,tipsMore: true
                        });
                        return;
                    }
                    //动作值
                    var actionValInfo = {
                        //动作id参数Id
                        actionParamId: 3,
                        //值
                        paramValue: actionVal + ''
                    };
                    //权值
                    var actionValQz = {
                        //动作id参数Id
                        actionParamId: 6,
                        //值
                        paramValue: weight+''
                    };
                    actionInfos.push(actionValInfo);
                    actionInfos.push(actionValQz);
                    subForm.conditionInfos = conditionInfos;
                    subForm.actionInfos = actionInfos;
                    subForms.push(subForm);
                });
                sceneUtil.sub.gradeForm = subForms;
            },
        /**
         * 决策规则数据统一获取
         */
        getSceneRuleData: function () {

            var subForms = [];
            //统一权值设置
            sceneUtil.data.itemsBank = [];
            $("#table>tbody>tr").each(function () {
                subForm = {
                    //权值
                    group:{},
                    //条件集合
                    conditionInfos: [],
                    //动作集合
                    actionInfos: []
                }
                var conditionInfos = [];
                var actionInfos = [];
                //拼条件
                var conTd = $(this).find("td").eq(0);
                $(conTd).find("ul li").each(function(i,e){
                    //拼条件 的变量 ，运算符 ，值
                    var entityv = $(e).find("a.entityC").attr("data-value");
                    var entityText = $(e).find("a.entityC").text();
                    var itemv = $(e).find("a.itemC").attr("data-value");
                    var itemText = $(e).find("a.itemC").text();

                    var ysf = $(e).find("a.con").attr("data-value");
                    var ysfText = $(e).find("a.con").text();
                    var val = $(e).find("a.val").attr("data-value");
                    var valText = $(e).find("a.val").text();
                    //变量类型的哦
                    if($(e).find("a.val").hasClass("conditionEntity")){
                        val = '#'+$(e).find("a.val").attr("data-key")+'#';
                    }
                    if(val == '' || val == undefined){
                        sceneUtil.flag = false;
                        layer.tips('条件值不能为空', $(e).find("a.val"), {
                            tips: 3,time:3000
                            ,tipsMore: true
                        });
                        return ;
                    }
                    if(ysf == '' || ysf == undefined){
                        sceneUtil.flag = false;
                        layer.tips('运算符不能为空', $(e).find("a.con"), {
                            tips: 3,time:3000
                            ,tipsMore: true
                        });
                        return ;
                    }
                    if(itemv == '' || itemv == undefined){
                        sceneUtil.flag = false;
                        layer.tips('变量对象不能为空', $(e).find("a.itemC"), {
                            tips: 3,time:3000
                            ,tipsMore: true
                        });
                        return ;
                    }
                    if (val == '' || ysf == '' || itemv == '') {
                        layer.msg('必选项不能为空');
                        sceneUtil.flag = false;
                        $(e).addClass("error");
                        return ;
                    }

                    if (val == '' || ysf == '' || itemv == '') {
                        layer.msg('必选项不能为空');
                        sceneUtil.flag = false;
                        $(e).addClass("error");
                        return ;
                    }
                    var conditionInfo = {
                        conditionExpression: '$' + itemv + '$' + '' + ysf + '' + val,
                        conditionDesc: '$' + entityText + ':'+ itemText+ '$' + ysfText + '' + valText,
                        val: valText
                    }
                    conditionInfos.push(conditionInfo);
                    //使用了那些变量，重新赋值了
                    sceneUtil.addTtemsBank(itemv);
                });
                //拼动作
                var actionTd = $(this).find("td").eq(1);
                $(actionTd).find("ul li div.param a").each(function(i,e){
                    //拼动作参数，参数值
                    var paramId = $(e).attr("paramid");
                    var val = $(e).attr("data-value");
                    var text = $(e).text();
                    //参数为变量值
                    if($(e).hasClass("actionEntity")){
                        val = '#'+$(e).attr("data-key")+'#';
                    }

                    if(val == '' || val == undefined){
                        sceneUtil.flag = false;
                        layer.tips('变量对象不能为空', $(e), {
                            tips: 3,time:3000
                            ,tipsMore: true
                        });
                        return ;
                    }
                    if (val == '' || text == '' || paramId == '') {
                        layer.msg('必选项不能为空');
                        sceneUtil.flag = false;
                        return ;
                    }
                    var actionValInfo = {
                        //动作id参数Id
                        actionParamId: paramId,
                        //值
                        paramValue: val,
                        paramText:text
                    };
                    //使用了那些变量，重新赋值了
                    actionInfos.push(actionValInfo);
                });
                subForm.conditionInfos = conditionInfos;
                subForm.actionInfos = actionInfos;
                subForms.push(subForm);
            });
            sceneUtil.sub.gradeForm = subForms;
        },
            /**
             * 提交数据
             */
            subGrad: function () {

                sceneUtil.getGradeRuleData();
                if(!sceneUtil.flag){
                    layer.msg("请检查有必填项没填");
                    sceneUtil.flag = true;
                    return;
                }
                if (sceneUtil.sceneId == '') {
                    layer.msg("必须选中一个场景哦");
                    sceneUtil.flag = true;
                    return;
                }
                var iindex =  layer.msg('提交中..', {icon: 16,time:5000});
                //实体类对象拼接
                var entityIds = [];
                for(var i=0;i < sceneUtil.data.entitysBank.length;i++){
                    entityIds.push(sceneUtil.data.entitysBank[i].value);
                }
                var form = {
                    //场景id
                    sceneId: sceneUtil.sceneId,
                    //变量集合
                    itemVals: sceneUtil.data.itemsBank,
                    //实体类集合
                    entityIds: entityIds,
                    //条件 和结果集
                    vos: sceneUtil.sub.gradeForm
                }
                console.log(form);
                //转json
                var str = JSON.stringify(form);
                //console.log(str);
                $.ajax({
                    type: "POST",
                    url: "/rule/service/rule/saveGrade",
                    data: str,
                    timeout: 15000,
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    success: function (message) {
                        layer.close(iindex);
                        console.log(message);
                        if(message.code < 0){
                            layer.alert(message.msg);
                        }
                        if (message.code == '') {

                            if(sceneUtil.subType == 2){
                                layer.msg('恭喜保存成功,是否关闭页面？', {
                                    time: 10000, //20s后自动关闭
                                    btn: ['好的', '不关闭'],
                                    icon: 1,
                                    yes:function(i){
                                        layer.close(i);
                                        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                                        parent.layer.close(index); //再执行关闭
                                    }
                                });
                            }else{
                                layer.msg("恭喜保存成功",{time:1000});
                            }
                        }
                    },
                    error: function (message) {
                        $("#request-process-patent").html("提交数据失败！");
                    }
                });

            },
        /**
         * 提交数据 决策
         */
        subScene: function () {

            sceneUtil.getSceneRuleData();
            if(!sceneUtil.flag){
                layer.msg("请检查有必填项没填");
                sceneUtil.flag = true;
                return;
            }
            if (sceneUtil.sceneId == '') {
                layer.msg("必须选中一个场景哦");
                sceneUtil.flag = true;
                return;
            }
            var iindex =  layer.msg('提交中..', {icon: 16,time:5000});
            //实体类对象拼接
            var entityIds = [];
            for(var i=0;i < sceneUtil.data.entitysBank.length;i++){
                entityIds.push(sceneUtil.data.entitysBank[i].value);
            }
            var form = {
                //场景id
                sceneId: sceneUtil.sceneId,
                //变量集合
                itemVals: sceneUtil.data.itemsBank,
                //实体类集合
                entityIds: entityIds,
                //条件 和结果集
                vos: sceneUtil.sub.gradeForm
            }
            console.log(form);
            //转json
            var str = JSON.stringify(form);
            //console.log(str);
            $.ajax({
                type: "POST",
                url: "/rule/service/rule/save",
                data: str,
                dataType: "json",
                timeout: 15000,
                contentType: "application/json; charset=utf-8",
                success: function (message) {
                    layer.close(iindex);
                    console.log(message);
                    if(message.code < 0){
                        layer.alert(message.msg);
                    }
                    if (message.code == '' ) {

                        if(sceneUtil.subType == 2){
                            layer.msg('恭喜保存成功,是否关闭页面？', {
                                time: 10000, //20s后自动关闭
                                btn: ['好的', '不关闭'],
                                icon: 1,
                                yes: function(ii){
                                    layer.close(ii);
                                    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                                    parent.layer.close(index); //再执行关闭
                               },
                                no:function (iii) {
                                    layer.close(iii);
                                }
                            });
                        }else{
                            layer.msg("恭喜保存成功",{time:1000});
                        }
                    }
                },
                error: function (message) {
                    $("#request-process-patent").html("提交数据失败！");
                }
            });

        },

        };


    ;
    exports('sceneUtil', sceneUtil);
});