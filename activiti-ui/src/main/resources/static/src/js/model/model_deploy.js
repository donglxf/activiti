layui.config({
    base: '/activiti/ui/src/js/modules/' //假设这是你存放拓展模块的根目录
}).extend({ //设定模块别名
    myutil: 'common' //如果 mymod.js 是在根目录，也可以不用设定别名
});
layui.use(['table','jquery','myutil'], function(){
    var table = layui.table;
    var $ = layui.jquery;
    //第一个实例
    table.render({
        elem: '#info_list'
        ,height: 'auto'
        ,url: pathConfig.activitiServicePath+'list' //数据接口
        ,page: true //开启分页
        ,cols: [[ //表头\
            {field: 'name',event: 'setItem', title: '模型名称', width:"30%"},
            {field: 'category',event: 'setItem', title: '分类', width:"50%"},
            {fixed: 'right', align:'center', toolbar: '#barDemo', width: "20%"}
        ]]
    });

    //监听工具条
    table.on('tool(info)', function(obj){ //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        var deployId = data.deploymentId;

        if(layEvent === 'setItem'){ //行点击事件
            console.log('deployId = '+deployId);
            table.reload('deploy_list', {
                url: pathConfig.activitiServicePath+'getDeployVersionList',
                where: {
                   "deployId":deployId
                } //设定异步数据接口的额外参数
                //,height: 300
            });
        }
    });

    table.render({
        elem: '#deploy_list'
        ,height: 'auto'
        ,url: pathConfig.activitiServicePath+'getDeployVersionList' //数据接口
        ,page: false //开启分页
        ,cols: [[ //表头\
            {field: 'deploymentId', title: '发布ID', width:"34%"},
            {field: 'key', title: 'key', width:"33%"},
            {field: 'version', title: '版本号', width:"33%"}
        ]]
    });


});






