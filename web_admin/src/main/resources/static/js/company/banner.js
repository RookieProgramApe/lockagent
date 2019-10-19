$(function () {
    layui.use(['index',"table","form"],function () {
        var table = layui.table,form = layui.form;
        //=================列表==================================
        table.render({
            elem: '#dataList'
            ,url: ctx+ '/banner/tabData' //模拟接口
            ,cols: [[
                {type: 'numbers',width:'10%'}
                ,{field: 'imgUrl', title: '图标',templet:'#imageUrlTpl',width:'20%'}
                ,{field: 'description', title: '描述',width:'20%'}
                ,{field: 'state', title: '状态',templet:'#stateTpl',width:'20%'}
                ,{title: '操作',align: 'center', fixed: 'right', toolbar: '#tagsBar'}
            ]]
            ,text: {
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
            $.post(ctx+"/banner/updateState",{id:id,state:val},function (res) {
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
                pLayer.confirm('确定删除此分类？', function(index){
                    pLayer.close(index);
                    //后台删除
                    $.post(ctx+"/banner/del",{id:data.id},function (res) {
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
                    ,title: '编辑分类'
                    ,content: ctx+"/banner/formPage?id="+ data.id
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
        //=================分类表单==================================
        var active = {
            add: function(){
                pLayer.open({
                    type: 2
                    ,title: '添加分类'
                    ,content: ctx+"/banner/formPage"
                    ,maxmin: true
                    ,area: ['850px', '600px']
                    ,btn: ['确定', '取消']
                    ,yes: function(index, layero){
                        //点击确认触发 iframe 内容中的按钮提交
                        var submit = layero.find('iframe').contents().find("#submitBtn");
                        submit.click();
                    }
                });
            }
        }
        $('.layui-btn.layuiadmin-btn-tags').on('click', function(){
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });
    });
})