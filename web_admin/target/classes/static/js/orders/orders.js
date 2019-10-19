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
            ,url: ctx+ '/orders/tabData' //模拟接口
            , cols: [[
                {type: 'checkbox', fixed: 'left',width:'5%'}
                , {field: 'createTime', title: '创建时间', align: 'center',width:'15%'}
                , {field: 'type', title: '订单类型' ,templet: '#typeTpl',width:'10%'}
                , {field: 'productName', title: '商品名称',width:'13%'}
                , {field: 'realName', title: '会员姓名',width:'8%'}
                , {field: 'phone', title: '会员手机', align: 'center',width:'11%'}
                , {field: 'num', title: '购买数量', align: 'center',width:'8%'}
                , {field: 'payFeeCustom', title: '金额/积分', align: 'center',width:'8%'}
                , {field: 'state', title: '状态',templet:'#stateTpl', align: 'center',width:'8%'}
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
            if(obj.event === 'del'){
                pLayer.confirm('确定核销吗？', function(index){
                    pLayer.close(index);
                    $.post(ctx+"/orders/updateState",{id:data.id,state:val},function (res) {
                        if(res.errorCode != -1){
                            layer.msg(res.errorMsg, {icon: 6});
                            table.reload("dataList");
                        }else{
                            layer.msg(res.errorMsg, {icon: 5});
                        }
                    });
                });
            } else if(obj.event === 'edit'){
                pLayer.open({
                    type: 2
                    ,title: '查看详情'
                    ,content: ctx+"/orders/detailPage?id="+ data.id
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