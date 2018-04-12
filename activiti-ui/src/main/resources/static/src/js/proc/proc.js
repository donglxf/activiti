layui.config({
    base: '/activiti/ui/src/js/modules/' //假设这是你存放拓展模块的根目录
}).extend({ //设定模块别名
    myutil: 'common' //如果 mymod.js 是在根目录，也可以不用设定别名
});
var preUrl="/activiti/service/";
var preUrlUi="/activiti/ui/";
var proInstIds='';
layui.use(['table','jquery','myutil'], function(){
    var table = layui.table;
    var itemTable = layui.table;
    var $ = layui.jquery;
    //第一个实例
    table.render({
        elem: '#proc_list'
        ,height: 'auto'
        ,url:preUrl+'/queryHisProcList' //数据接口
        ,id: 'testReload'
        ,page: true //开启分页
        ,cols: [[ //表头\
             {field: 'proInstId', title: '实例id'}
            ,{field: 'startTime', title: '开始系统'}
            ,{field: 'endTime', title: '结束时间'}
            ,{field: 'isComplate', title: '是否结束',templet:"#start"}
            ,{fixed: 'right', width:250, align:'center', toolbar: '#barDemo'}
        ]]
    });
    table.on('tool(model)', function(obj){ //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        var tr = obj.tr; //获得当前行 tr 的DOM对象
        var proInstId = data.proInstId;
        console.log(data);
        if(layEvent === 'view'){ //编辑
            showdetail(proInstId);
        }else if(layEvent==='viewImg'){
            showProImg(proInstId);
        }
    });

    /**
     * 设置表单值
     * @param el
     * @param data
     */
    function setFromValues(el, data) {
        for (var p in data) {
            el.find(":input[name='" + p + "']").val(data[p]);
        }
    }


    active = {
        reload: function(){
            var modelName = $('#modelId');
            console.log(modelName.val());
            table.reload('testReload', {
                page: {
                    curr: 1 //重新从第 1 页开始
                }
                ,where: {
                    "proId": modelName.val()
                }
            });
        }
    };

    $('#model_search').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });

    function showProImg(proInstId){
        proInstIds=proInstId;
        var layIndex = layer.open({
            type: 2,
            shade: false,
            title:"流程图",
            anim:5,
            area : [ '1200px', '600px' ],
            content: preUrlUi+'/showProImg',
            zIndex: layer.zIndex, //重点1
            success: function(layero, index){
                var body = layer.getChildFrame('body', index);
                var input=body.find("input[type='hidden']");
                input.val(proInstId);
                layer.setTop(layero); //重点2
            }
        });
    }


    function showdetail(proInstId){
        // $.get(preUrl+"processHisAutoIdea?processInstanceId="+proInstId,function (data) {
        //     var result = data.data;
        //     $.get(preUrlUi+'/procDetail', null, function (form) {
        //         topIndex =  layer.open({
        //             type :1,
        //             title : '流程明细',
        //             maxmin : true,
        //             shadeClose : false, // 点击遮罩关闭层
        //             area : [ '600px', '400px' ],
        //             content :  form,
        //             btnAlign: 'c',
        //             btn: ['保存', '取消'],
        //             success: function (layero, index) {
        //                 setFromValues(layero, result);
        //                 // $("#proInstId").value(proInstId);
        //             }
        //             ,yes: function (index) {
        //                 //layedit.sync(editIndex);
        //                 //触发表单的提交事件
        //                 $('form.layui-form').find('button[lay-filter=formDemo]').click();
        //                 //  layer.close(index);
        //             },
        //         });
        //     });
        // })
        var layIndex = layer.open({
            type: 2,
            shade: false,
            title:"流程明细",
            anim:5,
            area : [ '700px', '600px' ],
            content: preUrlUi+'/procDetail',
            zIndex: layer.zIndex, //重点1
            success: function(layero, index){
                var body = layer.getChildFrame('body', index);
                var input=body.find("input[type='hidden']");
                input.val(proInstId);
                layer.setTop(layero); //重点2
            }
        });
        // layer.full(layIndex);
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










