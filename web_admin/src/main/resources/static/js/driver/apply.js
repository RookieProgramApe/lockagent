$(function () {
    layui.use(['index',"table","form","laydate"],function () {
        var table = layui.table,form = layui.form,laydate= layui.laydate;
        laydate.render({
            elem: '#beginDate'
        });
        laydate.render({
            elem: '#endDate'
        });
        //=================数据列表==================================
        //监听搜索
        form.on('submit(searchBtn)', function (data) {
            var field = data.field;
            //执行重载
            table.reload('dataList', {
                where: field
            });
        });
        //数据管理
        table.render({
            elem: '#dataList'
            ,url: ctx+ '/apply/tabData' //模拟接口
            , cols: [[
                {type: 'checkbox', fixed: 'left',width:'5%'}
                , {field: 'createTime', title: '创建时间', align: 'left',width:'15%'}
                , {field: 'realName', title: '姓名',width:'6%'}
                , {field: 'shopName', title: '驾校名称',width:'10%'}
                , {field: 'productName', title: '套餐名称',width:'12%'}
                , {field: 'currentPrice', title: '单价',width:'6%'}
                , {field: 'classes', title: '班级', align: 'center',width:'7%'}
                , {field: 'motorcycle', title: '车型', align: 'center',width:'7%'}
                , {field: 'studentId', title: '学员ID', align: 'center',width:'7%'}
                , {field: 'state', title: '报名状态',templet:'#stateTpl', align: 'center',width:'8%'}
                , {field: 'payState', title: '支付状态',templet:'#payStateTpl', align: 'center',width:'8%'}
                , {title: '操作', align: 'left', toolbar: '#operBtnTpl'}
            ]]
            , page: true
            , limit: 10
            , limits: [10, 15, 20, 25, 30]
            , text: {
                none: '暂无相关数据' //默认：无数据。注：该属性为 layui 2.2.5 开始新增
            }
        });
        //监听工具条
        table.on('tool(dataList)', function(obj){
            var data = obj.data;
            var val = 3;
            if(obj.event === 'show'){
                pLayer.open({
                    type: 2
                    ,title: '查看详情'
                    ,content: ctx+"/apply/detailPage?id="+ data.id
                    ,maxmin: true
                    ,area: calScreenSize()
                    ,btn: ['关闭']
                    ,yes: function(index, layero){
                        //点击确认触发 iframe 内容中的按钮提交
                        pLayer.close(index);
                    }
                });
            }
        });
    });
})