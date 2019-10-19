$(function () {
    layui.use(['index', "table", "form"], function () {
       var table = layui.table;
        var form = layui.form;
        //=================数据列表==================================
        //监听搜索
        form.on('submit(searchBtn)', function (data) {
            var field = data.field;
            //执行重载
            table.reload('dataList', {
                where: field
            });
        });
        var type = $("input[name='type']").val();
        var shopId = $("input[name='currShopId']").val();
        //数据管理
        table.render({
            elem: '#dataList'
            , url: ctx + '/shopProdRel/tabData?unShopId='+shopId+"&type="+type
            , cols: [[
                {type: 'checkbox', fixed: 'left',width:'5%'}
                , {field: 'name', title: '名称',width:'20%'}
                , {field: 'catName', title: '类别', align: 'center',width:'15%'}
                , {field: 'specs', title: '规格', align: 'center',width:'15%'}
                , {field: 'currentPrice', title: '价格',width:'10%'}
                , {field: 'store', title: '库存', align: 'center',width:'10%'}
                , {field: 'state', title: '状态', templet: '#buttonTpl', align: 'center',width:'10%'}
                , {title: '操作', align: 'center', toolbar: '#operBtnTpl'}
            ]]
            , page: true
            , limit: 10
            , limits: [10, 15, 20, 25, 30]
            , text: {
                none: '暂无相关数据' //默认：无数据。注：该属性为 layui 2.2.5 开始新增
            }
        });
        //数据管理
        table.render({
            elem: '#selectDataList'
            , url: ctx + '/shopProdRel/selectTabData?shopId='+shopId+"&type="+type
            , cols: [[
                {type: 'checkbox', fixed: 'left',width:'5%'}
                , {field: 'name', title: '名称',width:'20%'}
                , {field: 'catName', title: '类别', align: 'center',width:'15%'}
                , {field: 'specs', title: '规格', align: 'center',width:'15%'}
                , {field: 'currentPrice', title: '价格',width:'10%'}
                , {field: 'store', title: '库存', align: 'center',width:'10%'}
                , {field: 'state', title: '状态', templet: '#buttonTpl', align: 'center',width:'10%'}
                , {title: '操作', align: 'center', toolbar: '#selectOperBtnTpl'}
            ]]
            , page: false
            , text: {
                none: '暂无相关数据' //默认：无数据。注：该属性为 layui 2.2.5 开始新增
            }

        });
        //监听工具条
        table.on('tool(dataList)', function (obj) {
            var data = obj.data;
            if (obj.event === 'addRel') {
                pLayer.confirm('确定添加吗？', function (index) {
                    pLayer.close(index);
                    //后台删除
                    $.post(ctx + "/shopProdRel/addRel", {shopId:shopId,productId: data.id}, function (res) {
                        if (res.errorCode != -1) {
                            layer.msg(res.errorMsg, {icon: 6});
                            table.reload("dataList");
                            table.reload("selectDataList");
                        } else {
                            layer.msg(res.errorMsg, {icon: 5});
                        }
                    });
                });
            }
        });

        table.on('tool(selectDataList)', function (obj) {
            var data = obj.data;
            if (obj.event === 'delRel') {
                pLayer.confirm('确定删除吗？', function (index) {
                    pLayer.close(index);
                    //后台删除
                    $.post(ctx + "/shopProdRel/delRel", {shopId:shopId,productId: data.id}, function (res) {
                        if (res.errorCode != -1) {
                            layer.msg(res.errorMsg, {icon: 6});
                            table.reload("dataList");
                            table.reload("selectDataList");
                        } else {
                            layer.msg(res.errorMsg, {icon: 5});
                        }
                    });
                });
            }
        });


    });
})