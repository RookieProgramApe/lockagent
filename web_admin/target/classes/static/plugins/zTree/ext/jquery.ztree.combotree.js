;(function($) { 
	$.fn.comboTree = function(options,checkfn) {
		var me=this;
		var defaults = {
			     expandAll:false,
			     type:'radio',
			     param:{}
		};
		var settings = $.extend({},defaults, options);//将一个空对象做为第一个参数
		//新定义tree(id元素属性)
		var id=$(me).attr('id');
		var random= $.randomString(10)//随机数
		var MenuContentId = id+'_MenuContent_RAM_'+random;
		var ValuesId = id+'_ZTreeValues_RAM_'+random;
		var ZtreeId = id+'_ZtreeContent_RAM_'+random;
		var ClearId = id+'_Clear_RAM_'+random;
		//DOM元素模板与初始化
		var htmlStr="<a href='javascript:;' style='margin: 6px 0 0 -20px;outline: none;position: absolute;' id='"+ClearId+"' title='清空'><b>x</b></a>" +
				"<input name='"+$(me).attr('name')+"' type='hidden' id='"+ValuesId+"' />" +
				"<div id='"+MenuContentId+"'  style='display:none; position:absolute; z-index:9999;'>" +
				"<div class='search-bar'></div>" +		
				"<div class='content'>" +
				"<ul  id='"+ZtreeId+"' class='ztree'></ul>" +
				"</div></div>";
		$(me).css("display","inline").css("color","#c2c2c2").removeAttr("name").after(htmlStr);
		//初始化方法  
        me.init = function(){ 
			//初始化ztree
        	me.ztreeInit();
			//绑定input点击实际
			$(me).on('click', function(){
					if($("#"+MenuContentId).is(":visible")){me.hideMenu(); return;}//是否可见 
					me.refreshWidth();
					me.showMenu();
					$("body").on("mousedown",me.onBodyDown);
			});
			//绑定Clear清除事件
			$('#'+ClearId).on('click', function(){
				me.clear();
			});
			//窗口改变
			$(window).on('resize',function(e){
				me.refreshWidth();
			})
        }
        //clear清除
        me.clear = function(){
        	$(me).attr("value", "");
        	$('#'+ValuesId).attr("value", "");
        	var treeObj = $.fn.zTree.getZTreeObj(ZtreeId);
        	treeObj.checkAllNodes(false);
        }
        //ztree宽度自适应
        me.refreshWidth = function(){
			$("#"+ZtreeId).css("width",$(me).width());
		}
		//ztree面板展开
        me.showMenu = function(){
			$("#"+MenuContentId).css({top:$(me).outerHeight() + "px"}).slideDown("fast");
		}
        //ztree面板收缩
        me.hideMenu = function(){
			$("#"+MenuContentId).fadeOut("fast");
			$("body").unbind("mousedown",me.onBodyDown);
		}
		//body层点击触发
		me.onBodyDown = function(event) {
			if (!(event.target.id == $(me).attr('id') || event.target.id == MenuContentId|| $(event.target).parents("#"+MenuContentId).length>0)) {
				me.hideMenu();
			}
		}
		//ztree初始化
        me.ztreeInit = function(){
            $.ajax({
                type: "GET",
                data: settings.param,
                url: settings.url,
                async:true,
                success: function(data){
                	var ZtreeDefaultSetting;
        	        if(settings.type =='checkbox'){
        					 ZtreeDefaultSetting = {
        							view: {
        								selectedMulti: true,
        								showTitle: true
        							},
        							check: {
        								enable: true,
        								chkStyle: "checkbox",
        								chkboxType: { "Y": "ps", "N": "s" }
        							},
        							data: {
        								key: {
        									title: "title"
        								},
        								simpleData: {
        									enable: true
        								}
        							},
        							callback: {
        								onCheck: me.onCheck
        							}
        					};
        	        	}else if(settings.type =='radio'){
        	        		var ZtreeDefaultSetting = {
        						view: {
        							dblClickExpand: false
        						},
        						data: {
        							simpleData: {
        								enable: true
        							}
        						},
        						callback: {
        							onClick : me.onClick 
        						}
        	        		};
        	        	}
        			var treeObj=$.fn.zTree.init($("#"+ZtreeId), ZtreeDefaultSetting,data);
        			//全部展开
                	treeObj.expandAll(treeObj.expandAll(settings.expandAll));
                	//默认数据
                    me.setValue(treeObj);
                },
                error: function(XMLHttpRequest, textStatus, errorThrown){
                }
            });
		}
        
        //默认赋值
        me.setValue = function(treeObj){ 
        	if(settings.value){
        		var vals=settings.value.split(",");
	        	if(settings.type =='radio'){
	        		for (var i=0;i<vals.length;i++){
	        			var nodes = treeObj.getNodesByParam("id",vals[i], null);
	        			for(j = 0,len=nodes.length; j < len; j++) {
	        				$(me).attr("value", nodes[j].name);
	            			$('#'+ValuesId).attr("value", nodes[j].id);
	            			checkfn(nodes[j].id, nodes[j].name);
	        			}
	        		}
	        	}else if(settings.type =='checkbox'){
	        		for (var i=0;i<vals.length;i++){
	        			var nodes = treeObj.getNodesByParam("id",vals[i], null);
	        			for(j = 0,len=nodes.length; j < len; j++) {
	        				treeObj.checkNode(nodes[j], true, false,true);
	        			}
	        		}
	        	}
        	}
        }  
		//ztree选中触发
        me.onClick = function(e, treeId, treeNode){  
			$(me).attr("value", treeNode.name);
			$('#'+ValuesId).attr("value", treeNode.id);
			me.hideMenu();
			checkfn(treeNode.id, treeNode.name);
        }  
        me.onCheck = function(e, treeId, treeNode) {
			var treeObj = $.fn.zTree.getZTreeObj(treeId);
			var nodes = treeObj.getCheckedNodes(true);
			var ids=new Array();
			var names=new Array();
			for(j = 0,len=nodes.length; j < len; j++) {
				names[j]=nodes[j].name;
				ids[j]=nodes[j].id;
			}
			$(me).attr("value",names.join(','));
			$('#'+ValuesId).attr("value", ids.join(','));
			checkfn(ids.join(','), names.join(','));
		}	
	    //自动执行初始化函数  
        me.init();  
	}
	
	//获取随机数
	$.randomString = function(len) {  
		　　len = len || 32;  
		　　var $chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';  
		　　var maxPos = $chars.length;  
		　　var pwd = '';  
		　　for (i = 0; i < len; i++) {  
		　　　　pwd += $chars.charAt(Math.floor(Math.random() * (maxPos+1)));  
		　　}  
		　　return pwd;  
	} 
})(jQuery);