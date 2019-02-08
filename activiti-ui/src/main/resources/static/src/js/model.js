layui.config({
    base: '/activiti/ui/src/js/modules/' //假设这是你存放拓展模块的根目录
}).extend({ //设定模块别名
    myutil: 'common' //如果 mymod.js 是在根目录，也可以不用设定别名
});
var preUrl = "/activiti/service/";
var preUrlUi = "/activiti/ui/";
layui.use(['table', 'jquery', 'myutil'], function () {
    var table = layui.table;
    var itemTable = layui.table;
    var $ = layui.jquery;
    //第一个实例
    table.render({
        elem: '#model_list'
        , height: 'auto'
        , url: preUrl + '/list1' //数据接口
        , id: 'testReload'
        , page: true //开启分页
        , cols: [[ //表头\
            {field: 'modelName', title: '模型名称', width: "20%"}
            , {field: 'belongSystem', title: '所属系统', width: "20%"}
            , {field: 'creTime', title: '创建时间', width: "20%"}
            , {fixed: 'right', width: 150, align: 'center', toolbar: '#barDemo', width: "40%"}
        ]]
    });
    itemTable.render({
        elem: '#version_list'
        , height: 'auto'
        , url: pathConfig.activitiServicePath + 'page' //数据接口
        , id: "versionReload"
        , page: true //开启分页
        , cols: [[ //表头\
            {field: 'modelCode', title: '模型编码', width: "15%"}
            , {field: 'versionType', title: '版本类型', width: "10%", templet: '#versionTpl'}
            , {field: 'modelVersion', title: '模型版本', width: "10%"}
            // ,{field: 'isValidate', title: '验证状态', width:"10%" ,templet:'#verfiactionTpl'}
            , {field: 'isApprove', title: '审核状态', width: "10%", templet: '#approvalTpl'}
            , {field: 'createUser', title: '创建人', width: "10%"}
            , {field: 'createTime', title: '创建时间', width: "20%"}
            , {field: 'start', title: '启动', templet: '#start'}
        ]]
    });
    table.on('tool(model)', function (obj) { //注：tool是工具条事件名，model是table原始容器的属性 lay-filter="对应的值"
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        var tr = obj.tr; //获得当前行 tr 的DOM对象
        var modelId = data.modelId;
        var deploymentId = data.deploymentId;
        console.log(data);
        if (layEvent === 'edit') { //编辑
            showdetail(modelId);
        } else if (layEvent === 'deploy') {// 发布测试版本
            deploy(modelId);
        } else if (layEvent === 'del') { //删除
            deleteModel(obj, modelId, deploymentId);
        } else if (layEvent === 'version') { //查看
            queryVersionList(modelId);
        }
    });
    itemTable.on('tool(version)', function (obj) {
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        var modelProcdefId = data.modelProcdefId;
        var modelVersion = data.modelVersion;
        if (layEvent === 'startProc') { // 启动流程
            startProc(modelProcdefId, modelVersion);
        } else if (layEvent === 'viewFile') { // 查看流程文件
            viewFile(modelProcdefId, modelVersion);
        }
    })
    var active = {
        reload: function () {
            var modelName = $('#modelName');
            console.log(modelName.val());
            table.reload('testReload', {
                page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {
                    "key": modelName.val()
                }
            });
        },
        versionReload: function () {
            var modelVersion = $('#modelVersion');
            table.reload('versionReload', {
                page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {
                    "modelVersion": modelVersion.val()
                }
            });
        }
    };

    $('#model_search').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });

    function deleteModel(obj, modelId, deploymentId) {
        if (deploymentId != '' && deploymentId != null) {
            layer.msg("模型已部署，无法删除！");
            return;
        }
        layer.confirm('是否删除？', function (index) {
            //向服务端发送删除指令
            $.ajax({
                cache: true,
                type: "GET",
                url: pathConfig.activitiServicePath + 'deleteModel?modelId=' + modelId,
                timeout: 6000, //超时时间设置，单位毫秒
                async: false,
                error: function (request) {
                    layer.msg("删除失败！");
                },
                success: function (data) {
                    console.log(data);
                    if (data.code == 0) {
                        layer.msg("删除成功！");
                        obj.del(); //删除对应行（tr）的DOM结构，并更新缓存
                        layer.close(index);
                    } else {
                        layer.msg(data.msg);
                    }
                }
            });
        });
    }

    function deploy(modelId) {
        layer.confirm('您确定发布测试版吗？', function (index) {
            layer.close(index);
            console.log("modelId" + modelId);
            layer.load();
            $.ajax({
                cache: true,
                type: "GET",
                url: pathConfig.activitiServicePath + 'deploy?modelId=' + modelId,
                async: false,
                timeout: 10000,
                error: function (request) {
                    layer.msg("发布失败！");
                    layer.closeAll('loading');
                },
                success: function (data) {
                    console.log(data);
                    layer.closeAll('loading');
                    if (data.code == 0) {
                        layer.msg("发布成功！");
                    } else {
                        layer.msg(data.msg);
                    }
                }
            });
        });
    }

    function viewFile(modelProcdefId, modelVersion) {
        var layIndex = layer.open({
            type: 2,
            shade: false,
            title: "查看文件",
            anim: 5,
            area: ['1000px', '800px'],
            content: preUrlUi + '/viewFile',
            zIndex: layer.zIndex, //重点1
            success: function (layero, index) {
                // var iframe = window['layui-layer-iframe' + index];
                // iframe.child(modelProcdefId,modelVersion);



                var body = layer.getChildFrame('body', index);
                var input = body.find("input[type='hidden']");
                input.val(modelProcdefId);
                layer.setTop(layero); //重点2
            }
        });
    }

    function startProc(modelProcdefId, modelVersion) {
        layer.confirm('您确定启动流程吗？', function (index) {
            layer.close(index);
            console.log("modelProcdefId==" + modelProcdefId + ">>modelVersion==" + modelVersion);
            layer.load();
            $.ajax({
                cache: true,
                type: "GET",
                url: pathConfig.activitiServicePath + 'start?procDefId=' + modelProcdefId + "&version=" + modelVersion,
                async: false,
                timeout: 10000,
                error: function (request) {
                    layer.msg("启动失败！");
                    layer.closeAll('loading');
                },
                success: function (data) {
                    console.log(data);
                    layer.closeAll('loading');
                    if (data.code == 0) {
                        layer.msg("启动成功！");
                    } else {
                        layer.msg(data.msg);
                    }
                }
            });
        });
    }

    function showdetail(modelId) {
        $("#input_hidden_model").val(modelId);
        var layIndex = layer.open({
            type: 2,
            shade: false,
            title: "模型定义",
            content: preUrlUi + '/modelEdit?modelId=' + modelId + "&date=" + new Date(),
            zIndex: layer.zIndex, //重点1
            success: function (layero) {
                layer.setTop(layero); //重点2
            }
        });
        layer.full(layIndex);
    }

    function queryVersionList(modelId) {
        table.reload('versionReload', {
            page: {
                curr: 1 //重新从第 1 页开始
            }
            , where: {
                "modelId": modelId
            }
        });
    }

    $('#version_search').on('click', function () {
        var type = $(this).data('type');
        console.log(type);
        active[type] ? active[type].call(this) : '';
    });


});

function addModel() {
    layer.closeAll();
    var layIndex = layer.open({
        type: 2,
        shade: false,
        title: "新增模型",
        area: ['800px', '450px'],
        content: pathConfig.activitiUiPath + '/addView',
        zIndex: layer.zIndex, //重点1
        success: function (layero) {
            // layer.msg(data.msg);
            layer.setTop(layero); //重点2
        }
    });
}










