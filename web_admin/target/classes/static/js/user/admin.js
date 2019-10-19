$(function () {
    layui.use(['index',"table","form"],function () {
        var table = layui.table,form = layui.form;
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
            ,url: ctx+ '/admin/tabData' //模拟接口
            , cols: [[
                {type: 'checkbox', fixed: 'left',width:'5%'}
                , {field: 'userName', title: '姓名',width:'15%'}
                , {field: 'phone', title: '手机号码',width:'15%'}
                , {field: 'mail', title: '邮箱', align: 'center',width:'20%'}
                , {field: 'state', title: '是否启用',templet:'#stateTpl', align: 'center',width:'18%'}
                , {title: '操作', align: 'center', toolbar: '#operBtnTpl'}
            ]]
            , page: true
            , limit: 10
            , limits: [10, 15, 20, 25, 30]
            , text: {
                none: '暂无相关数据' //默认：无数据。注：该属性为 layui 2.2.5 开始新增
            }
        });
        //监听列表状态值
        form.on('switch(stateFilter)', function(data){
            var name = $(data.elem).attr("name"),id = $(data.elem).attr("data-id"),
                obj=data.elem,isChecked=data.elem.checked,val = 0;
            if (isChecked) {
                val = 1;
            }
            $.post(ctx+"/admin/updateState",{id:id,state:val},function (res) {
                if(res.errorCode != -1){
                    layer.msg(res.errorMsg, {icon: 6});
                }else{
                    layer.msg(res.errorMsg, {icon: 5});
                }
            });
        });

        //监听工具条
        table.on('tool(dataList)', function(obj){
            var data = obj.data;
            if(obj.event === 'del'){
                pLayer.confirm('确定删除吗？', function(index){
                    pLayer.close(index);
                    //后台删除
                    $.post(ctx+"/admin/del",{id:data.id},function (res) {
                        if(res.errorCode != -1){
                            layer.msg(res.errorMsg, {icon: 6});
                            table.reload("dataList");
                        }else{
                            layer.msg(res.errorMsg, {icon: 5});
                        }
                    });
                });
            }  else if(obj.event === 'modifyPwd'){
                pLayer.prompt(function(value, index, elem){
                    pLayer.close(index);
                    if(value==""){
                        layer.msg("新密码不能为空", {icon: 5});
                        return false;
                    }
                    //后台删除
                    $.post(ctx+"/admin/modifyPwd",{id:data.id,password:value},function (res) {
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
                    ,title: '编辑会员'
                    ,content: ctx+"/admin/formPage?id="+ data.id
                    ,maxmin: true
                    ,area: calScreenSize()
                    ,btn: ['确定', '取消']
                    ,yes: function(index, layero){
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
                    $.post(ctx + "/admin/delBatch",{ids:JSON.stringify(ids)}, function (res) {
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
                    , title: '添加管理员'
                    , content: ctx + "/admin/formPage"
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
        $('.layui-btn.layuiadmin-btn-admin').on('click', function () {
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });
    });
})