var imageListView = $('#imageListView')
    , alreadyUploadFiles = {}//记录已经上传成功的文件相对路径（后台返回）
    , maxNumber = 5//这里设置自己允许最大上传数
    , uploadListIns = upload.render({
    elem: '#imageList'
    , url: ctx + 'FAQOrder/uploadPic'//这里设置自己的上传接口
    , accept: 'images'
    , multiple: true
    , auto: false
    , size: 10240
    , number: maxNumber
    , bindAction: '#imageListAction'
    , xhr: xhrOnProgress
    , progress: function (index, value) {//上传进度回调 value进度值
        element.progress('image' + index, value + '%');//设置页面进度条
    }
    , errorMsg: function (content) {
        layer.msg(content, {icon: 2, shift: 6});
    }
    , choose: function (obj) {
        var that = this;
        //检查是否已经成功上传了maxNumber个图片
        //获取上传成功的文件：
        var alreadyUploadFilesLength = Object.keys(alreadyUploadFiles).length;
        if (alreadyUploadFilesLength >= maxNumber) {
            that.errorMsg('同时最多只能上传的数量为：' + maxNumber);
            return false;
        }
        //读取本地文件
        obj.preview(function (index, file, result) {
            var files = that.files = obj.pushFile(); //将每次选择的文件追加到文件队列
            var alreadyChooseFileLength = 0;
            if (that.files) {//已经选择，未上传的文件
                $.each(that.files, function () {
                    alreadyChooseFileLength++;
                })
            }
            if ((alreadyUploadFilesLength + alreadyChooseFileLength) > maxNumber) {
                that.errorMsg('同时最多只能上传的数量为：' + maxNumber);
                delete that.files[index];
                delete files[index];
                uploadListIns.config.elem.next()[0].value = '';
                return false;
            }

            var tr = $(['<tr id="upload-' + index + '">'
                , '<td class="name"><a class="upload-img-prev-link" href="javascript:;">' + file.name + '</a></td>'
                , '<td>' + (file.size / 1024).toFixed(1) + 'kb</td>'
                , '<td>等待上传</td>'
                , '<td>'
                , '<div class="layui-progress layui-progress-big layui-progress-radius-fix" lay-showpercent="true" lay-filter="image' + index + '">'
                , '<div class="layui-progress-bar" lay-percent="0%">'
                , '<span class="layui-progress-text">0%</span>'
                , '</div>'
                , '</div>'
                , '</td>'
                , '<td>'
                , '<button class="layui-btn layui-btn-mini image-reload layui-hide">重传</button>'
                , '<button class="layui-btn layui-btn-mini layui-btn-danger image-delete">删除</button>'
                , '</td>'
                , '</tr>'].join(''));

            //单个重传
            tr.find('.image-reload').on('click', function () {
                obj.upload(index, file);
            });
            //文件预览
            var thisTipIndex = 0;
            tr.find('.upload-img-prev-link').on('mouseover', function () {
                thisTipIndex = layer.tips('<img width=320 src=' + result + '></img>', $(this), {
                    tips: [2, 'rgb0.value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                }
                layer.msg((data && data.message) || '未知');
            }
        })
    })
}else
{
    delete files[index]; //删除对应的文件
    tr.remove();
    uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
}
})
;
imageListView.append(tr);
})
;
}
,
done: function (res, index, upload) {
    if (res.code == 0) { //上传成功
        var tr = imageListView.find('tr#upload-' + index)
            , tds = tr.children();
        tds.eq(2).html('<span style="color: #5FB878;">上传成功</span>');
        tds.eq(3).find('.layui-progress-bar').css('background-color', '#86EAA1');
        alreadyUploadFiles[index] = res.data && res.data[0];//缓存已上传的文件
        return delete this.files[index]; //删除文件队列已经上传成功的文件
    }
    this.error(index, upload);
}
,
error: function (index, upload) {
    var tr = imageListView.find('tr#upload-' + index)
        , tds = tr.children();
    tds.eq(2).html('<span style="color: #ff5722;">上传失败</span>');
    tds.eq(3).find('.layui-progress-bar').css('background-color', '#ff5722');
    tds.eq(4).find('.image-reload').removeClass('layui-hide'); //显示重传
}
})
;