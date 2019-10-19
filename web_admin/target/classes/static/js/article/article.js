$(function () {
    layui.use(['index', "table", "form"], function () {
      var  table = layui.table;
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
        //数据管理
        table.render({
            elem: '#dataList'
            , url: ctx + '/article/tabData'
            , cols: [[
                {type: 'checkbox', fixed: 'left',width:'5%'}
                , {field: 'name', title: '名称',width:'15%'}
                , {field: 'categoryId', title: '分类', templet: '#categoryTpl', align: 'center',width:'10%'}
                , {field: 'specs', title: '规格', align: 'center',width:'10%'}
                , {field: 'currentPrice', title: '价格',width:'10%'}
                , {field: 'store', title: '库存', align: 'center',width:'10%'}
                , {field: 'state', title: '状态', templet: '#buttonTpl', align: 'center',width:'10%'}
                , {field: 'isHot', title: '是否推荐', templet: '#isHot',width:'10%'}
                , {title: '操作', align: 'center', toolbar: '#operBtnTpl'}
            ]]
            , page: true
            , limit: 10
            , limits: [10, 15, 20, 25, 30]
            , text: {
                none: '暂无相关数据' //默认：无数据。注：该属性为 layui 2.2.5 开始新增
            }
            , response: {
                statusName: 'status' //数据状态的字段名称，默认：code
                , statusCode: 200 //成功的状态码，默认：0
                , msgName: 'msg' //状态信息的字段名称，默认：msg
                , countName: 'total' //数据总数的字段名称，默认：count
                , dataName: 'records' //数据列表的字段名称，默认：data
            }

        });
        //监听列表开关
        form.on('switch(isHot)', function (data) {
            var name = $(data.elem).attr("name"), id = $(data.elem).attr("data-id"),
                obj = data.elem, isChecked = data.elem.checked, val = 0;
            if (isChecked) {
                val = 1;
            }
            $.post(ctx + "/article/updateIsHot", {id: id, isHot: val}, function (res) {
                if (res.errorCode != -1) {
                    layer.msg(res.errorMsg, {icon: 6});
                } else {
                    layer.msg(res.errorMsg, {icon: 5});
                }
            });
        });

        //监听工具条
        table.on('tool(dataList)', function (obj) {
            var data = obj.data;
            if (obj.event === 'del') {
                pLayer.confirm('确定删除此资讯？', function (index) {
                    pLayer.close(index);
                    //后台删除
                    $.post(ctx + "/article/del", {id: data.id}, function (res) {
                        if (res.errorCode != -1) {
                            layer.msg(res.errorMsg, {icon: 6});
                            table.reload("dataList");
                        } else {
                            layer.msg(res.errorMsg, {icon: 5});
                        }
                    });
                });
            } else if (obj.event === 'edit') {
                var tr = $(obj.tr);
                pLayer.open({
                    type: 2
                    , title: '编辑资讯'
                    , content: ctx + "/article/formPage?id=" + data.id
                    , maxmin: true
                    , area: calScreenSize()
                    , btn: ['确定', '取消']
                    , yes: function (index, layero) {
                        //点击确认触发 iframe 内容中的按钮提交
                        var submit = layero.find('iframe').contents().find("#submitBtn");
                        submit.click();
                    }
                });
            }
        });
        //=================添加或编辑表单==================================
        var active = {
            batchdel: function () {
                var checkStatus = table.checkStatus('dataList')
                    , checkData = checkStatus.data; //得到选中的数据
                if (checkData.length === 0) {
                    return layer.msg('请选择数据');
                }
                layer.confirm('确定删除吗？', function (index) {
                    var ids = new Array();
                    for(var i=0;i<checkData.length;i++){
                        ids.push(checkData[i].id);
                    }
                    //后台批量删除
                    $.post(ctx + "/article/delBatch",{ids:JSON.stringify(ids)}, function (res) {
                        if (res.errorCode != -1) {
                            layer.msg(res.errorMsg, {icon: 6});
                            table.reload("dataList");
                        } else {
                            layer.msg(res.errorMsg, {icon: 5});
                        }
                    });
                    table.reload('dataList');
                    layer.msg('已删除');
                });
            },
            add: function () {
                pLayer.open({
                    type: 2
                    , title: '添加资讯'
                    , content: ctx + "/article/formPage"
                    , maxmin: true
                    , area: calScreenSize()
                    , btn: ['确定', '取消']
                    , yes: function (index, layero) {
                        //点击确认触发 iframe 内容中的按钮提交
                        var submit = layero.find('iframe').contents().find("#submitBtn");
                        submit.click();
                    }
                });
            }
        }
        $('.layui-btn.layuiadmin-btn-add').on('click', function () {
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });
        $('.layui-btn.layuiadmin-btn-batchDel').on('click', function () {
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });
    });
})