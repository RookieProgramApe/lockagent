$(function () {

    /**
     * 字符串截取加省略号
     * str:字符串
     * len 截取的长度
     * 用法:xxx.interceptString(5)
     */
    jQuery.interceptString = function (str, len) {
        if (str) {
            if (str.length > len) {
                return str.substring(0, len) + "...";
            } else {
                return str;
            }
        } else {
            return '';
        }
    }

    /**
     * 字符串NULL转换''
     * str:字符串
     * len 截取的长度
     * 用法:xxx.interceptString(5)
     */
    jQuery.replaceNull = function (str) {
        if (str == null) {
            return '';
        } else {
            return str
        }
    }

    /**
     * layer.msg 请求成功状态的提示窗口
     * message:提示内容
     * successfn:窗口成功显示完后调用函数
     */
    alertSucceedMsg = function (message, successfn) {
        layer.msg(message, {time: 500, shade: 0.1}, function () {
            successfn();
        });
    }

    /**
     * layer.msg 请求失败状态的提示窗口
     * message:提示内容
     * successfn:窗口成功显示完后调用函数
     */
    alertFailMsg = function (message, successfn) {
        layer.msg(message, {icon: 2, time: 1000}, function () {
            successfn();
        });
    }

    /**
     * 获取父类的Iframe
     */
    getParentIframe = function () {
        var ifame = $(parent.document).find("div.layui-show").find("iframe")[0];
        return ifame.contentWindow;
    }
    getParentIframeByOrder = function (i) {
        var ifame = $(parent.document).find(".layui-layer-iframe")[i];
        return $(ifame).find("iframe")[0].contentWindow;
    }
    /**
     * 关闭layer.open窗口
     */
    winClose = function () {
        var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
        parent.layer.close(index);
    }


    /**
     * 加载select控件
     * @param url
     * @param data
     * @param def
     * @param async  true:异步
     */
    $.fn.Select = function (options) {
        var me = this;
        var defaults = {
            url: '',
            data: {},
            def: '',
            async: false,
            label: '-请选择-',
            value: '',
            valueName:'value',
            labelName:'name'
        };
        var settings = $.extend({}, defaults, options);//将一个空对象做为第一个参数
        //初始化方法
        me.init = function () {
            $.ajax({
                type: "POST",
                data: settings.data,
                url: settings.url,
                dataType: "json",
                async: settings.async,
                success: function (d) {
                    if (d.code == 200) {
                        var dataArr = d.data;
                        $(me).append("<option value='" + settings.value + "'>" + settings.label + "</option>");
                        for (var i = 0; i < dataArr.length; i++) {
                            var html;
                            if (settings.def && dataArr[i][settings.valueName]  == settings.def) {
                                html = "<option value='" + dataArr[i][settings.valueName] + "' selected='selected'>" + dataArr[i][settings.labelName] + "</option>";
                            } else {
                                html = "<option value='" + dataArr[i][settings.valueName] + "'>" + dataArr[i][settings.labelName] + "</option>";
                            }
                            $(me).append(html);
                        }
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    layer.msg("请求出错,请检查", {icon: 2});
                }
            });
        }
        //自动执行初始化函数
        me.init();
    }


    /**
     * 上传附件
     * @param options
     * @param successfn
     */
    $.fn.uploadFile = function (options, successfn) {
        var me = this;
        var defaults = {
            loadUrl: '',
            size: 2000,//单个文件最大为2000m
            number: 100,
            model: '',
            name: "url",
            type: 'file',
            def:''
        };
        var settings = $.extend({}, defaults, options);//将一个空对象做为第一个参数
        //初始化方法
        me.init = function () {
            layui.use('element', function () {
                var element = layui.element;
                var tableId = $(me).attr("id") + "_tableList";
                var tabelHtml = " <div class='layui-upload-list'> " +
                    "<table class='layui-table' style='display:none;' lay-size='sm'><thead>" +
                    "<tr><th>附件</th><th>大小</th><th>状态</th><th>操作</th></tr>" +
                    "</thead>" +
                    "<tbody id='" + tableId + "'></tbody>" +
                    "</table>" +
                    "</div>";
                $(me).after(tabelHtml);
                var $list = $('#' + tableId);
                var accept = {};
                if (settings.type == 'image') {//图片
                    accept.title = "Images";
                    accept.extensions = 'gif,jpg,jpeg,bmp,png';
                    accept.mimeTypes = "image/*";
                } else if (options.type == 'video') {//视频
                    accept.title = "video";
                    accept.extensions = "mp4,MP4,ogv,OGV";
                    accept.mimeTypes = "*";
                }
                var uploader = WebUploader.create({
                    auto: true,
                    swf: $ctx + '/static/plugins/webuploader/Uploader.swf',// swf文件路径
                    method: 'POST',
                    accept: accept,
                    server: $ctx + '/file/uploadFile',// 文件接收服务端。
                    formData: {
                        model: settings.model//模块目录
                    },
                    pick: '#' + $(me).attr("id"),// 选择文件的按钮。可选。
                    duplicate: true,//去重
                    // fileNumLimit:settings.number,
                    fileSingleSizeLimit: settings.size * 1024 * 1024,//单个文件最大为200m
                    resize: false// 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
                });
                //初始化数据
                if (settings.def) {
                    $list.parent('table').show();
                    var thumb = '<img height="50" width="100" src=\"' + settings.def + '\"  onclick="LookPhoto(\'' + settings.def + '\')">';
                    if (settings.type != 'image') {//非图片
                        thumb = '<a href="'+settings.def +'" target="_blank" style="color: blue">[查看附件]</a>';
                    }
                    var tra = $(['<tr>'
                        , '<td>' + thumb + '</td>'
                        , '<td>无</td>'
                        , '<td><font style="color: #5FB878;">已上传</font></td>'
                        , '<td>'
                        , '<input type="hidden" name="' + settings.name + '"  value="' + settings.def + '" /><div class="layui-btn layui-btn-xs layui-btn-danger" onclick="delFile(this)">删除</div>'
                        , '</td>'
                        , '</tr>'].join(''));
                    $list.append(tra);
                }
                //默认加载
                if (settings.loadUrl) {
                    $.ajax({
                        type: "GET",
                        url: settings.loadUrl,
                        async: true,
                        success: function (d) {
                            var farr = d.data;
                            if (d.code == 200) {
                                if (farr.length > 0) {
                                    $list.parent('table').show();
                                    for (var i = 0; i < farr.length; i++) {
                                        var thumb = "";
                                        if (farr[i].fileType == "2") {
                                            thumb = ' <video height="50" width="100" src=\"' + farr[i].fileUrl + '\" onclick="loadPlay(\'' + farr[i].fileUrl + '\')"></video>';
                                        } else {
                                            thumb = '<img height="50" width="100" src=\"' + farr[i].fileUrl + '\" onclick="LookPhoto(\'' + farr[i].fileUrl + '\')">';
                                        }
                                        var tra = $(['<tr id="' + farr[i].id + '">'
                                            , '<td>' + thumb + '</td>'
                                            , '<td>无</td>'
                                            , '<td><font style="color: #5FB878;">已上传</font></td>'
                                            , '<td>'
                                            , '<input type="hidden" name="' + settings.name + '"  value="' + farr[i].fileUrl + '" /><div class="layui-btn layui-btn-xs layui-btn-danger" onclick="delFile(this)">删除</div>'
                                            , '</td>'
                                            , '</tr>'].join(''));
                                        $list.append(tra);
                                    }
                                }
                            }
                            return;
                        },
                        error: function (XMLHttpRequest, textStatus, errorThrown) {
                            layer.msg("请求出错,请检查", {icon: 2});
                        }
                    });
                }
                // 当有文件被添加进队列的时候
                uploader.on('fileQueued', function (file) {
                    var trCount = $list.find("tr").length;
                    if (trCount >= settings.number) {
                        layer.msg("文件上传上限为" + settings.number, {icon: 2});
                        return;
                    }
                    $list.parent('table').show();
                    var size = (file.size / 1024 / 1024).toFixed(1);
                    var dw = "MB";
                    if (size <= 0) {
                        size = (file.size / 1024).toFixed(1);
                        dw = "KB";
                    }
                    if (size <= 0) {
                        size = file.size;
                        dw = "B";
                    }
                    if (settings.type == 'image') {//图片
                        var tr = $(['<tr id="' + file.id + '">'
                            , '<td><img height="50" width="100" onclick="lookPic(this)"></td>'
                            , '<td>' + size + dw + '</td>'
                            , '<td><p class="state">正在上传..</p></td>'
                            , '<td>'
                            , ''
                            , '</td>'
                            , '</tr>'].join(''));
                        $list.append(tr);
                        uploader.makeThumb(file, function (error, src) {
                            if (error) {
                                $img.replaceWith('<span>' + file.name + '</span>');
                                return;
                            } else {
                                tr.find('img').attr('src', src);
                            }
                        }, 100, 50);
                    } else {//视频
                        var tr = $(['<tr id="' + file.id + '">'
                            , '<td><span>' + file.name + '</span></td>'
                            , '<td>' + size + dw + '</td>'
                            , '<td><p class="state">正在上传..</p></td>'
                            , '<td>'
                            , ''
                            , '</td>'
                            , '</tr>'].join(''));
                        $list.append(tr);
                    }
                });

                // 文件上传过程中创建进度条实时显示。
                uploader.on('uploadProgress', function (file, percentage) {
                    var tr = $list.find('tr#' + file.id), tds = tr.children();
                    var $li = tds.eq(2),
                        $percent = $li.find('.layui-progress  .layui-progress-bar');
                    // 避免重复创建
                    if (!$percent.length) {
                        $percent = $('<div class="layui-progress" lay-filter="progress' + file.id + '">' +
                            '<div class="layui-progress-bar" lay-percent="0%"></div> ' +
                            '</div>').appendTo($li).find('.layui-progress-bar');
                    }
                    $li.find('p.state').text('上传中');
                    element.progress('progress' + file.id, percentage * 100 + '%');
                });

                uploader.on('uploadSuccess', function (file, data) {
                    if (successfn) {
                        successfn(data.fileUrl);
                    }
                    var tr = $list.find('tr#' + file.id), tds = tr.children();
                    tr.find('p.state').html('<font style="color: #5FB878;">上传成功</font>');
                    tds.eq(3).html('<input type="hidden" name="' + settings.name + '"  value="' + data.fileUrl + '"  data-fileName="'+data.fileName+'" data-filePath="'+data.filePath+'"  /><button class="layui-btn layui-btn-xs layui-btn-danger file-delete">删除</button>');
                    tr.find('.file-delete').on('click', function () {//删除附件
                        tr.remove();
                        uploader.removeFile(file);
                    });
                });

                uploader.on('uploadError', function (file) {
                    $list.find('tr#' + file.id).html('<font style="color: red;">上传失败</font>');
                });

                uploader.on('uploadComplete', function (file) {
                    $list.find('tr#' + file.id).find('.layui-progress').fadeOut();
                });
                uploader.on('error', function (type) {
                    /*if(data='F_EXCEED_SIZE'){
                        layer.msg("单个附件不能超过"+settings.size+"MB,文件上传上限为"+settings.number, {icon: 2});
                    }*/
                    if (type == "Q_TYPE_DENIED") {
                        if (settings.type == 'image') {//图片
                            layer.msg("请上传gif,jpg,jpeg,bmp,png格式文件");
                        } else if (options.type == 'video') {//视频
                            layer.msg("请上传视频格式文件");
                        }
                    } else if (type == "Q_EXCEED_SIZE_LIMIT" || type == "F_EXCEED_SIZE") {
                        layer.msg("文件大小不能超过" + settings.size + "MB");
                    } else if (type == "Q_EXCEED_NUM_LIMIT") {
                        layer.msg("文件上传上限为" + settings.number);
                    } else {
                        layer.msg("上传出错！请检查后重新上传！错误代码" + type);
                    }
                });
            });
        }
        //自动执行初始化函数
        me.init();
    }
    /**
     * 图片-文本管理
     * @param options
     * @param successfn
     */
    $.fn.uploadPicFile = function (options, successfn) {
        var me = this;
        var defaults = {
            loadUrl: '',
            size: 2000,//单个文件最大为2000m
            number: 100,
            model: '',
            name: "url",
            type: 'file',
            def: ''
        };
        var settings = $.extend({}, defaults, options);//将一个空对象做为第一个参数
        //初始化方法
        me.init = function () {
            layui.use('element', function () {
                var element = layui.element;
                var tableId = $(me).attr("id") + "_tableList";
                var tabelHtml = "<div class='tidiv' style='display:none;' id='" + tableId + "'> </div>"
                $(me).after(tabelHtml);
                var $list = $('#' + tableId);
                var accept = {};
                accept.title = "Images";
                accept.extensions = 'gif,jpg,jpeg,bmp,png';
                accept.mimeTypes = "image/*";
                var uploader = WebUploader.create({
                    auto: true,
                    swf: $ctx + '/static/plugins/webuploader/Uploader.swf',// swf文件路径
                    method: 'POST',
                    accept: accept,
                    server: $ctx + '/file/uploadFile',// 文件接收服务端。
                    formData: {
                        model: settings.model//模块目录
                    },
                    pick: '#' + $(me).attr("id"),// 选择文件的按钮。可选。
                    duplicate: true,//去重
                    // fileNumLimit:settings.number,
                    fileSingleSizeLimit: settings.size * 1024 * 1024,//单个文件最大为200m
                    resize: false// 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
                });
                //初始化数据
                if (settings.def) {
                    $(me).hide();
                    $list.show();
                    var divCont = $(["<div>" +
                                            "<img class='pict' src=' "+ settings.def + "' onclick='LookPhoto('"+ settings.def +"' )'>" +
                                            "<input type='hidden'  name=' "+ settings.name + "'  value='" + settings.def + "' /><img src='"+$ctx +"/static/image/icon2.png' data-id='"+$(me).attr("id")+"'  class='cha' onclick='delTxFile(this)'>"+
                                    "</div>"].join(''));
                    $list.html(divCont);
                }
                // 当有文件被添加进队列的时候
                uploader.on('fileQueued', function (file) {
                    $(me).hide();
                    $list.show();
                    if (settings.type == 'image') {//图片
                        var divCont = $(["<div id='" + file.id + "'>" +
                        "<img class='pict'  onclick='lookPic(this)'>" +
                        "<p class=\"state \">正在上传..</p>" +
                        "</div>"].join(''));
                        $list.html(divCont);
                        uploader.makeThumb(file, function (error, src) {
                            if (error) {
                                $img.replaceWith('<span>' + file.name + '</span>');
                                return;
                            } else {
                                divCont.find("img").attr('src', src);
                            }
                        }, 100, 50);
                    }
                });

                // 文件上传过程中创建进度条实时显示。
                uploader.on('uploadProgress', function (file, percentage) {
                    var divC = $list.find('div#' + file.id),
                        $percent = divC.find('.layui-progress  .layui-progress-bar');
                    // 避免重复创建
                    if (!$percent.length) {
                        $percent = $('<div class="layui-progress" lay-filter="progress' + file.id + '">' +
                            '<div class="layui-progress-bar" lay-percent="0%"></div> ' +
                            '</div>').appendTo(divC).find('.layui-progress-bar');
                    }
                    divC.find('p.state').text('上传中');
                    element.progress('progress' + file.id, percentage * 100 + '%');
                });

                uploader.on('uploadSuccess', function (file, data) {
                    if (successfn) {
                        successfn(data.fileUrl);
                    }
                    var divC = $list.find('div#' + file.id);
                    divC.find('p.state').remove();
                    // divC.find('p.state').html('<font style="color: #5FB878;">上传成功</font>');
                    divC.append('<input type="hidden" name="' + settings.name + '"  value="' + data.fileUrl + '"  data-fileName="' + data.fileName + '" data-filePath="' + data.filePath + '"  /><img src="' + $ctx + '/static/image/icon2.png" class="cha file-delete" >');
                    divC.find('.file-delete').on('click', function () {//删除附件
                        divC.remove();
                        $list.hide();
                        $(me).show();
                        uploader.removeFile(file);
                    });
                });

                uploader.on('uploadError', function (file) {
                    $list.find('div#' + file.id).html('<font style="color: red;">上传失败</font>');
                });

                uploader.on('uploadComplete', function (file) {
                    $list.find('div#' + file.id).find('.layui-progress').fadeOut();
                });
                uploader.on('error', function (type) {
                    if (type == "Q_TYPE_DENIED") {
                        if (settings.type == 'image') {//图片
                            layer.msg("请上传gif,jpg,jpeg,bmp,png格式文件");
                        } else if (options.type == 'video') {//视频
                            layer.msg("请上传视频格式文件");
                        }
                    } else if (type == "Q_EXCEED_SIZE_LIMIT" || type == "F_EXCEED_SIZE") {
                        layer.msg("文件大小不能超过" + settings.size + "MB");
                    } else if (type == "Q_EXCEED_NUM_LIMIT") {
                        layer.msg("文件上传上限为" + settings.number);
                    } else {
                        layer.msg("上传出错！请检查后重新上传！错误代码" + type);
                    }
                });
            });
        }
        //自动执行初始化函数
        me.init();
    }
    /**
     * 加载附件值
     */
    getFileUrls = function (name) {
        var arr = new Array();
        $("input:hidden[name='" + name + "']").each(function () {
            arr.push($(this).val());
        });
        return arr;
    }

});

function delFile(obj) {
    $(obj).parent().parent("tr").remove();
}
function delTxFile(obj) {
    var id=$(obj).attr("data-id");
    var tableId = id + "_tableList";
    $(obj).parent().remove();
    $("#"+id).show();
    $("#"+tableId).hide();

}
/**
 * 窗口大小
 * @returns {any[]}
 */
function calScreenSize() {
    var screenSize = new Array();
    layui.use(['index', 'admin'], function () {
        var admin = layui.admin;
        if (admin.screen() == 1) {
            screenSize = ['80%', '300px'];
        }
        if (admin.screen() == 2) {
            screenSize = ['70%', '550px'];
        }
        if (admin.screen() == 3) {
            screenSize = ['70%', '600px'];
        }
    });
    return screenSize;
}

/**
 * 封装POST请求-带loading
 * @param url
 * @param data
 * @param successfn
 */
jQuery.ajaxPost = function (url, data, successfn) {
    layui.use(['index', 'admin'], function () {
        var admin = layui.admin;
        var index;
        admin.req({
            url: $ctx + url,
            data: data,
            type: "POST",
            async: true,
            beforeSend: function (xhr) {
                //index=layer.load()
                index = layer.msg("正在请求中", {icon: 16, time: false, shade: 0.1});
            },
            success: function (data) {
                layer.close(index);
                if (data.code == 200) {
                    if(!data.msg){
                        data.msg = "操作成功";
                    }
                    alertSucceedMsg(data.msg, function () {
                        if (successfn != null)
                            successfn(data);
                    });
                }
            },
        })
    });
};
/**
 * 封装POST请求-不带loading
 * @param url
 * @param data
 * @param successfn
 */
jQuery.asynPost = function (url, data, successfn) {
    layui.use(['index', 'admin'], function () {
        var admin = layui.admin;
        var index;
        admin.req({
            url: $ctx + url,
            data: data,
            type: "POST",
            async: true,
            success: function (data) {
                if (data.code == 200) {
                    if (successfn != null)
                        successfn(data);
                }
            },
        })
    });
};

/**
 * 通用窗口封装
 * @param title
 * @param url
 * @param type
 * @constructor
 */
function LayOpen(title, url, type,size) {
    if(size==null)size=calScreenSize();
    if (type == 'add' || type == 'update') {
        var index = top.layer.open({
            type: 2,
            isOutAnim: true,
            title: title,
            shade: 0.1,
            maxmin: true, //开启最大化最小化按钮
            area: size,
            content: $ctx + url,
            btn: ['确定', '取消'],
            btnAlign: 'r',
            yes: function (index, layero) {
                var submit = layero.find('iframe').contents().find("#save");
                submit.click();
            }
        });
    } else if (type == 'find') {
        var index = top.layer.open({
            type: 2,
            isOutAnim: true,
            title: title,
            shade: 0.1,
            maxmin: true, //开启最大化最小化按钮
            area: size,
            content: $ctx + url
        });
    }
}

/**
 * 看图片
 * @param url
 * @constructor
 */
function LookPhoto( url) {
    layer.photos({
        photos: {
            "title": "图片", //相册标题
            "id": 123, //相册id
            "start": 0, //初始显示的图片序号，默认0
            "data": [   //相册包含的图片，数组格式
                {
                    "alt": "图片",
                    "pid": 666, //图片id
                    "src": url, //原图地址
                    "thumb": url //缩略图地址
                }
            ]
        }
        ,anim: 5
    });
}

function lookPic(obj) {
    layer.photos({
        photos: {
            "title": "图片", //相册标题
            "id": 123, //相册id
            "start": 0, //初始显示的图片序号，默认0
            "data": [   //相册包含的图片，数组格式
                {
                    "alt": "图片",
                    "pid": 666, //图片id
                    "src": $(obj).attr("src"), //原图地址
                    "thumb": $(obj).attr("src") //缩略图地址
                }
            ]
        }
        ,anim: 5
    });
}
function S4() {
    return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
}
function guid() {
    return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
}
