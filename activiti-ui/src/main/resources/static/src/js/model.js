layui.config({
    base: '/activiti/ui/src/js/modules/' //假设这是你存放拓展模块的根目录
}).extend({ //设定模块别名
    myutil: 'common' //如果 mymod.js 是在根目录，也可以不用设定别名
});
var preUrl="/activiti/service/";
var preUrlUi="/activiti/ui/";
layui.use(['table','jquery','myutil'], function(){
    var table = layui.table;
    var $ = layui.jquery;
    //第一个实例
    table.render({
        elem: '#model_list'
        ,height: 'auto'
        ,url:preUrl+'/list' //数据接口
        ,id: 'testReload'
        ,page: true //开启分页
        ,cols: [[ //表头\
             {field: 'modelName', title: '模型名称', width:"25%"}
            ,{field: 'createDate', title: '创建时间', width:"25%"}
            ,{fixed: 'right', width:150, align:'center', toolbar: '#barDemo', width: "50%"}
        ]]
    });
    table.render({
        elem: '#version_list'
        ,height: 'auto'
        ,url: pathConfig.activitiConfigPath+'page' //数据接口
        ,id: "versionReload"
        ,page: true //开启分页
        ,cols: [[ //表头\
            {field: 'modelCode', title: '模型编码', width:"15%"}
            ,{field: 'versionType', title: '版本类型', width:"15%" ,templet:'#versionTpl'}
            ,{field: 'modelVersion', title: '模型版本', width:"10%"}
            ,{field: 'isValidate', title: '验证状态', width:"10%" ,templet:'#verfiactionTpl'}
            ,{field: 'isApprove', title: '审核状态', width:"10%",templet:'#approvalTpl'}
            ,{field: 'createUser', title: '创建人', width:"20%"}
            ,{field: 'createTime', title: '创建时间', width:"20%" }
        ]]
    });
    table.on('tool(model)', function(obj){ //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        var tr = obj.tr; //获得当前行 tr 的DOM对象
        var modelId = data.modelId;
        var deploymentId = data.deploymentId;
        console.log(data);
        if(layEvent === 'edit'){ //编辑
            showdetail(modelId);
        }else if(layEvent === 'deploy'){// 发布测试版本
            deploy(modelId);
        } else if(layEvent === 'del'){ //删除
            deleteModel(obj,modelId,deploymentId);
        } else if(layEvent === 'version'){ //查看
            queryVersionList(modelId);
        }
    });
    var active = {
        reload: function(){
            var modelName = $('#modelName');
            console.log(modelName.val());
            table.reload('testReload', {
                page: {
                    curr: 1 //重新从第 1 页开始
                }
                ,where: {
                    "modelName": modelName.val()
                }
            });
        },
        versionReload: function(){
            var modelVersion = $('#modelVersion');
            table.reload('versionReload', {
                page: {
                    curr: 1 //重新从第 1 页开始
                }
                ,where: {
                    "modelVersion": modelVersion.val()
                }
            });
        }
    };

    $('#model_search').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });

    function deleteModel(obj,modelId,deploymentId){
        if(deploymentId != '' && deploymentId != null){
            layer.msg("模型已部署，无法删除！");
            return;
        }
        layer.confirm('是否删除？', function(index){
            //向服务端发送删除指令
            $.ajax({
                cache : true,
                type : "GET",
                url : pathConfig.activitiServicePath+'deleteModel?modelId='+modelId,
                timeout : 6000, //超时时间设置，单位毫秒
                async : false,
                error : function(request) {
                    layer.msg("删除失败！");
                },
                success : function(data) {
                    console.log(data);
                    if(data.code == 0){
                        layer.msg("删除成功！");
                        obj.del(); //删除对应行（tr）的DOM结构，并更新缓存
                        layer.close(index);
                    }else {
                        layer.msg(data.msg);
                    }
                }
            });
        });
    }

    function deploy(modelId){
        layer.confirm('您确定发布测试版吗？', function(index){
            layer.close(index);
            console.log("modelId"+modelId);
            layer.load();
            $.ajax({
                cache : true,
                type : "GET",
                url : pathConfig.activitiServicePath+'modelDeploy/deploy?modelId='+modelId,
                async : false,
                timeout: 10000,
                error : function(request) {
                    layer.msg("发布失败！");
                    layer.closeAll('loading');
                },
                success : function(data) {
                    console.log(data);
                    layer.closeAll('loading');
                    if(data.code == 0){
                        layer.msg("发布成功！");
                    }else {
                        layer.msg(data.msg);
                    }
                }
            });
        });
    }

    function showdetail(modelId){
        $("#input_hidden_model").val(modelId);
        var layIndex = layer.open({
            type: 2,
            shade: false,
            title:"模型定义",
            content: preUrlUi+'/modelEdit?modelId='+modelId+"&date="+new Date(),
            zIndex: layer.zIndex, //重点1
            success: function(layero){
                layer.setTop(layero); //重点2
            }
        });
        layer.full(layIndex);
    }

    function queryVersionList(modelId){
        table.reload('versionReload', {
            page: {
                curr: 1 //重新从第 1 页开始
            }
            ,where: {
                "modelId": modelId
            }
        });
    }
    $('#version_search').on('click', function(){
        var type = $(this).data('type');
        console.log(type);
        active[type] ? active[type].call(this) : '';
    });


});
function addModel(){
    layer.closeAll();
    var layIndex = layer.open({
        type: 2,
        shade: false,
        title:"新增模型",
        area: ['800px','450px'],
        content: pathConfig.activitiUiPath+'/addView',
        zIndex: layer.zIndex, //重点1
        success: function(layero){
            // layer.msg(data.msg);
            layer.setTop(layero); //重点2
        }
    });
}










