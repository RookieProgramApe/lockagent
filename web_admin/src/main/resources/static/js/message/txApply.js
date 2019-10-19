$(function () {
    layui.use(['index', "table", "form"], function () {
        var table = layui.table, form = layui.form;
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
            , url: ctx + '/tx/tabData' //模拟接口
            , cols: [[
                {type: 'checkbox', fixed: 'left', width: '5%'}
                , {field: 'createTime', title: '申请时间', align: 'center', width: '20%'}
                , {field: 'realName', title: '姓名', width: '8%'}
                , {field: 'money', title: '提现金额', align: 'center', width: '15%'}
                , {field: 'state', title: '审核状态', templet:'#stateTpl',width: '10%'}
                , {field: 'payState', title: '支付状态', templet:'#payStateTpl',width: '10%'}
                , {field: 'reason', title: '备注', align: 'center', width: '15%'}
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
        table.on('tool(dataList)', function (obj) {
            var data = obj.data;
            if (obj.event === 'unagree') {
                pLayer.prompt(function (value, index, elem) {
                    var state = "审核不通过";
                    var reason = value;
                    $.post(ctx + "/tx/doApply", {id: data.id, state: state, reason: reason}, function (res) {
                        if (res.errorCode != -1) {
                            layer.msg(res.errorMsg, {icon: 6});
                            table.reload("dataList");
                        } else {
                            layer.msg(res.errorMsg, {icon: 5});
                        }
                    });
                    pLayer.close(index);
                });
            } else if (obj.event === 'agree') {
                pLayer.confirm('同意该申请吗？', {icon: 3, title: '提示'}, function (index) {
                    pLayer.close(index);
                    var state = "审核通过";
                    var reason = "同意提现。";
                    $.post(ctx + "/tx/doApply", {id: data.id, state: state, reason: reason}, function (res) {
                        if (res.errorCode != -1) {
                            layer.msg(res.errorMsg, {icon: 6});
                            table.reload("dataList");
                        } else {
                            layer.msg(res.errorMsg, {icon: 5});
                        }
                    });
                });
            } else if (obj.event === 'wePay') {
                pLayer.confirm('确认支付吗？', {icon: 3, title: '提示'}, function (index) {
                    pLayer.close(index);
                    $.post(ctx + "/tx/doPay", {id: data.id}, function (res) {
                        if (res.errorCode != -1) {
                            layer.msg(res.errorMsg, {icon: 6});
                            table.reload("dataList");
                        } else {
                            layer.msg(res.errorMsg, {icon: 5});
                        }
                    });
                });
            }
        });
    });
})