/**
 * 自定义文件上传方法（带进度条，暂时只支持图片，视频待完善）
 * @param options
 * options.uploadBtn:文件上传按钮选择器
 * options.uploadUrl:文件上传链接
 * options.loadFileUrl:文件加载链接
 * options.bussID:业务ID
 * options.module:业务模块
 * options.fileListNode:文件预览tr节点选择器
 * options.accept:允许的文件类型
 * options.maxFileNumber:允许最多上传文件的个数
 * options.onlyRead:是否仅仅展示附件列表
 * options.queryType:查询方式：默认:byId,按照URL查询:byUrl
 *
 */
function myUpload(options) {
    var $fileListView = $(options.fileListNode);
    var loadFileUrl = options.loadFileUrl;
    if (loadFileUrl && loadFileUrl != "" && options.bussID && options.bussID != "") {
        var queryType = "byId";
        if (options.queryType && options.queryType == "byUrl") {
            queryType = options.queryType;
        }
        //加载已有附件列表
        $.post(loadFileUrl, {
            bussID: options.bussID
            , module: options.module
            , queryType: queryType
        }, function (res) {
            $.each(res, function (index, ele) {
                var previewContent = "";
                var operBtnShow = "";
                if (!ele.fileType || ele.fileType == "images") {
                    previewContent = '<td><img src="' + ele.fileUrl + '" class="layui-upload-img"></td>';
                }
                if (options.onlyRead) {
                    operBtnShow = "style=\"display: none\"";
                }
                var tr = $(['<tr id="upload-' + ele.id + '">'
                    , previewContent
                    , '<td>' + ele.name + '</td>'
                    , '<td>' + ele.fileSize + 'kb</td>'
                    , '<td><span style="color: #5FB878;">上传成功</span></td>'
                    , '<td' + operBtnShow + '>'
                    , '<button class="layui-btn layui-btn-mini layui-btn-danger demo-delete">删除</button>'
                    , '<input type="hidden" name="fileId" value="' + ele.id + '">'
                    , '</td>'
                    , '</tr>'].join(''));
                //删除
                tr.find('.demo-delete').on('click', function () {
                    tr.remove();
                });
                $fileListView.append(tr);
            });

        });
    }
    //上传按钮不存在，默认为展示详情界面
    if (!options.uploadBtn || $(options.uploadBtn).length <= 0) {
        console.log("警告：上传组件，未配置上传按钮！");
        return false;
    }
    layui.use(['element', 'upload'], function () {
        var upload = layui.upload, element = layui.element;
        //创建监听函数
        var xhrOnProgress = function (fun) {
            xhrOnProgress.onprogress = fun; //绑定监听
            //使用闭包实现监听绑
            return function () {
                //通过$.ajaxSettings.xhr();获得XMLHttpRequest对象
                var xhr = $.ajaxSettings.xhr();
                //判断监听函数是否为函数
                if (typeof xhrOnProgress.onprogress !== 'function')
                    return xhr;
                //如果有监听函数并且xhr对象支持绑定时就把监听函数绑定上去
                if (xhrOnProgress.onprogress && xhr.upload) {
                    xhr.upload.onprogress = xhrOnProgress.onprogress;
                }
                return xhr;
            }
        }
        var uploadListIns = upload.render({
            elem: options.uploadBtn //绑定元素
            , url: options.uploadUrl //上传接口
            , multiple: true
            , accept: options.accept
            , data: {module: options.module, accept: options.accept}
            , xhr: xhrOnProgress
            , progress: function (index, value) {//上传进度回调 value进度值
                console.log("索引为==" + index);
                console.log("进度为==" + value);
                element.progress('demo' + index, value + '%');//设置页面进度条
            }
            , choose: function (obj) {
                var files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
                var fileLength = 0;
                for (var fileTemp in files) {
                    fileLength++;
                }
                var hasUploadFileNumber = $(".layui-upload input[name='fileId']").length;
                if (options.maxFileNumber && options.maxFileNumber != 0 && fileLength > options.maxFileNumber
                    || hasUploadFileNumber > options.maxFileNumber - 1) {
                    layer.msg("最多只能上传的数量为：" + options.maxFileNumber, {icon: 5});
                    return false;
                }
                //读取本地文件
                obj.preview(function (index, file, result) {
                    var previewContent = "";
                    if (!options.accept || options.accept == "images") {
                        previewContent = '<td><img src="' + result + '" class="layui-upload-img"></td>';
                    }
                    var tr = $(['<tr id="upload-' + index + '">'
                        , previewContent
                        , '<td>' + file.name + '</td>'
                        , '<td>' + (file.size / 1024).toFixed(1) + 'kb</td>'
                        , '<td><div class="layui-progress layui-progress-big" lay-filter="demo' + index + '" lay-showPercent="true"><div class="layui-progress-bar" lay-percent="0%"></div></div></td>'
                        , '<td>'
                        , '<button class="layui-btn layui-btn-mini demo-reload layui-hide">重传</button>'
                        , '<button class="layui-btn layui-btn-mini layui-btn-danger demo-delete">删除</button>'
                        , '<input type="hidden" name="fileId">'
                        , '</td>'
                        , '</tr>'].join(''));

                    //单个重传
                    tr.find('.demo-reload').on('click', function () {
                        obj.upload(index, file);
                    });

                    //删除
                    tr.find('.demo-delete').on('click', function () {
                        delete files[index]; //删除对应的文件
                        tr.remove();
                        uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                    });

                    $fileListView.append(tr);
                });
            }
            , done: function (res, index, upload) {
                //上传完毕回调
                //debugger
                if (res.code == 0) { //上传成功
                    var tr = $fileListView.find('tr#upload-' + index)
                        , tds = tr.children();
                    tds.eq(3).html('<span style="color: #5FB878;">上传成功</span>');
                    tds.eq(4).find("input[name='fileId']").val(res.data.id);
                    //tds.eq(4).html(''); //清空操作
                    return delete this.files[index]; //删除文件队列已经上传成功的文件
                }
                this.error(index, upload);
            }
            , error: function (index, upload) {
                //请求异常回调
                var tr = $fileListView.find('tr#upload-' + index)
                    , tds = tr.children();
                tds.eq(3).html('<span style="color: #FF5722;">上传失败</span>');
                tds.eq(4).find('.demo-reload').removeClass('layui-hide'); //显示重传
                element.progress('demo' + index, '0%');//设置页面进度条
            }
        });
    });
}