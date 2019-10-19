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
            ,url: ctx+ '/member/tabData' //模拟接口
            , cols: [[
                {type: 'checkbox', fixed: 'left',width:'5%'}
                , {field: 'headUrl', title: '头像', templet: '#imageUrlTpl', align: 'center',width:'10%'}
                , {field: 'nickName', title: '昵称',width:'10%'}
                , {field: 'phone', title: '手机', align: 'center',width:'10%'}
                , {field: 'mail', title: '邮箱',width:'10%'}
                , {field: 'gender', title: '性别', align: 'center',width:'6%'}
                , {field: 'isAuth', title: '是否认证',templet:'#authTpl', align: 'center',width:'10%'}
                , {field: 'state', title: '是否启用',templet:'#stateTpl', align: 'center',width:'10%'}
                , {field: 'createTime', title: '加入时间', templet: '#buttonTpl', align: 'center',width:'15%'}
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
                obj=data.elem,isChecked=data.elem.checked,val = "无效";
            if (isChecked) {
                val = "有效";
            }
            $.post(ctx+"/member/updateState",{id:id,state:val},function (res) {
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
                    $.post(ctx+"/member/del",{id:data.id},function (res) {
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
                    ,content: ctx+"/member/formPage?id="+ data.id
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
    });
})