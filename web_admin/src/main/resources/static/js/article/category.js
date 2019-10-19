$(function () {
    layui.use(['index',"table","form"],function () {
        var table = layui.table,form = layui.form;
        //=================分类列表==================================
        table.render({
            elem: '#tagTab'
            ,url: ctx+ '/articleCat/categoryTabData' //模拟接口
            ,cols: [[
                {type: 'numbers', fixed: 'left'}
                ,{field: 'name', title: '分类名'}
                ,{field: 'state', title: '状态',templet:'#state'}
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
            $.post(ctx+"/articleCat/updateCateState",{id:id,state:val},function (res) {
                if(res.errorCode != -1){
                    layer.msg(res.errorMsg, {icon: 6});
                }else{
                    layer.msg(res.errorMsg, {icon: 5});
                }
            });
        });

        //监听工具条
        table.on('tool(tagTabFilter)', function(obj){
            var data = obj.data;
            if(obj.event === 'del'){
                pLayer.confirm('确定删除此分类？', function(index){
                    pLayer.close(index);
                    //后台删除
                    $.post(ctx+"/articleCat/delCate",{id:data.id},function (res) {
                        if(res.errorCode != -1){
                            layer.msg(res.errorMsg, {icon: 6});
                            table.reload("tagTab");
                        }else{
                            layer.msg(res.errorMsg, {icon: 5});
                        }
                    });
                });
            } else if(obj.event === 'edit'){
                var tr = $(obj.tr);
                pLayer.open({
                    type: 2
                    ,title: '编辑分类'
                    ,content: ctx+"/articleCat/categoryForm?id="+ data.id
                    ,maxmin: true
                    ,area: calScreenSize()
                    ,btn: ['确定', '取消']
                    ,yes: function(index, layero){
                        var iframeWindow = parent.window['layui-layer-iframe'+ index]
                            ,submitFilter = 'submitBtnFilter'
                            ,submitBtnID = 'submitBtn'
                            ,submitBtn = layero.find('iframe').contents().find('#'+ submitBtnID);
                        //监听提交
                        iframeWindow.layui.form.on('submit('+ submitFilter +')', function(data){
                            var formData = data.field; //获取提交的字段
                            //提交 Ajax 成功后，静态更新表格中的数据
                            $.post(ctx+"/articleCat/categoryEdit",formData,function (res) {
                                if(res.errorCode != -1){
                                    layer.msg(res.errorMsg, {icon: 6});
                                }else{
                                    layer.msg(res.errorMsg, {icon: 5});
                                }
                            });
                            table.reload('tagTab'); //数据刷新
                            pLayer.close(index); //关闭弹层
                        });
                        submitBtn.trigger('click');
                    }
                    ,success: function(layero, index){
                        //给iframe元素赋值
                        var othis = layero.find('iframe').contents().find("#tags").click();
                        othis.find('input[name="tags"]').val(data.tags);
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
                    ,content: ctx+"/articleCat/categoryForm"
                    ,maxmin: true
                    ,area: calScreenSize()
                    ,btn: ['确定', '取消']
                    ,yes: function(index, layero){
                        var iframeWindow = parent.window['layui-layer-iframe'+ index]
                            ,submitFilter = 'submitBtnFilter'
                            ,submitBtnID = 'submitBtn'
                            ,submitBtn = layero.find('iframe').contents().find('#'+ submitBtnID);
                        //监听提交
                        iframeWindow.layui.form.on('submit('+ submitFilter +')', function(data){
                            var formData = data.field; //获取提交的字段
                            //提交 Ajax 成功后，静态更新表格中的数据
                            $.post(ctx+"/articleCat/categoryEdit",formData,function (res) {
                                if(res.errorCode != -1){
                                    layer.msg(res.errorMsg, {icon: 6});
                                }else{
                                    layer.msg(res.errorMsg, {icon: 5});
                                }
                            });
                            table.reload('tagTab'); //数据刷新
                            pLayer.close(index); //关闭弹层
                        });
                        submitBtn.trigger('click');
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