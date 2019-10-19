$(function () {
    layui.use(['index','table', 'util'],function () {
        var $ = layui.$
            ,table = layui.table;

        var DISABLED = 'layui-btn-disabled'
            //区分各选项卡中的表格
            ,tabs = {
                notice: {
                    text: '通知'
                    ,id: 'LAY-app-message-notice'
                }
                ,news: {
                    text: '新闻资讯'
                    ,id: 'LAY-app-message-news'
                }
            };

        //标题内容模板
        var tplTitle = function(d){
            return '<a href="addPid.html?id='+ d.id +'">'+ d.title;
        };
        //新闻资讯
        table.render({
            elem: '#LAY-app-message-news'
            ,url: ctx + '/message/newsTabData'
            ,page: true
            ,cols: [[
                {type: 'checkbox', fixed: 'left', width:'5%'}
                ,{field: 'title', title: '标题内容', width:'75%', templet: tplTitle}
                ,{field: 'createTime', title: '时间', align: 'center',templet: '<div>{{ layui.util.timeAgo(d.createTime) }}</div>'}
            ]]
            ,skin: 'line'
        });
        //通知
        table.render({
            elem: '#LAY-app-message-notice'
            ,url: ctx + '/message/noticeTabData'
            ,page: true
            ,cols: [[
                {type: 'checkbox', fixed: 'left', width:'5%'}
                ,{field: 'title', title: '标题内容', width:'75%', templet: tplTitle}
                ,{field: 'createTime', title: '时间',align: 'center', templet: '<div>{{ layui.util.timeAgo(d.createTime) }}</div>'}
            ]]
            ,skin: 'line'
        });

        //事件处理
        var events = {
            delBatch: function(othis, type){
                var thisTabs = tabs[type]
                    ,checkStatus = table.checkStatus(thisTabs.id)
                    ,data = checkStatus.data; //获得选中的数据
                if(data.length === 0) return layer.msg('未选中行');

                var ids = new Array();
                for(var i=0;i<data.length;i++){
                    ids.push(data[i].id);
                }
                layer.confirm('确定删除选中的数据吗？', function(){
                    $.post(ctx + "/message/delBatch", {ids:JSON.stringify(ids)}, function (res) {
                        if (res.errorCode != -1) {
                            layer.msg('删除成功', {
                                icon: 1
                            });

                            table.reload(thisTabs.id);
                        } else {
                            layer.msg(res.errorMsg, {icon: 5});
                        }
                    });

                });
            }
            ,add: function(othis, type){
                var thisTabs = tabs[type];
                var formType = $(othis).attr("form-type");
                pLayer.open({
                    type: 2
                    , title: '添加'
                    , content: ctx + "/message/formPage?type="+formType
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
            ,edit: function(othis, type){
                var thisTabs = tabs[type]
                    ,checkStatus = table.checkStatus(thisTabs.id)
                    ,data = checkStatus.data; //获得选中的数据
                var formType = $(othis).attr("form-type");
                if(data.length === 0) return layer.msg('未选中行');
                if(data.length >1 ) return layer.msg('修改时，只能选择一行');
                pLayer.open({
                    type: 2
                    , title: '添加'
                    , content: ctx + "/message/formPage?type="+formType+"&id="+data[0].id
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
            ,search: function(othis, type){
                var thisTabs = tabs[type];
                var title = "";
                var formType = $(othis).attr("form-type");
                if(formType==1){
                    title = $("#noticeName").val();
                }else{
                    title = $("#newsName").val();
                }
                table.reload(thisTabs.id,{
                    where:{type:formType,title:title}
                });
            }

        };

        $('.LAY-app-message-btns .layui-btn').on('click', function(){
            var othis = $(this)
                ,thisEvent = othis.data('events')
                ,type = othis.data('type');
            events[thisEvent] && events[thisEvent].call(this, othis, type);
        });

    });
})