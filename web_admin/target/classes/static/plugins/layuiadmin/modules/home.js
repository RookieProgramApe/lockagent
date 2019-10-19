/**

 @Name：layuiAdmin 主页示例
 @Author：star1029
 @Site：http://www.layui.com/admin/
 @License：GPL-2
    
 */


layui.define(function(exports){
  var admin = layui.admin;

  //区块轮播切换
  layui.use(['admin', 'carousel'], function(){
    var $ = layui.$
    ,admin = layui.admin
    ,carousel = layui.carousel
    ,element = layui.element
    ,device = layui.device();

    //轮播切换
    $('.layadmin-carousel').each(function(){
      var othis = $(this);
      carousel.render({
        elem: this
        ,width: '100%'
        ,arrow: 'none'
        ,interval: othis.data('interval')
        ,autoplay: othis.data('autoplay') === true
        ,trigger: (device.ios || device.android) ? 'click' : 'hover'
        ,anim: othis.data('anim')
      });
    });

    element.render('progress');

  });


  //访问量
  layui.use(['carousel', 'echarts'], function(){
    var $ = layui.$
    ,carousel = layui.carousel
    ,echarts = layui.echarts;
    var echartsApp = [], options = [
      {
        tooltip : {
          trigger: 'axis'
        },
        calculable : true,
        legend: {
          data:['会员数']
        },
        xAxis : [
          {
            type : 'category',
            data : JSON.parse(xaixs)
          }
        ],
        yAxis : [
          {
            type : 'value',
            name : '会员数',
            axisLabel : {
              formatter: '{value} '
            }
          }
        ],
        series : [
          {
            name:'会员数',
            type:'line',
            data:JSON.parse(yaixs)
          }
        ]
      }
    ]
    ,elemDataView = $('#LAY-index-pagetwo').children('div')
    ,renderDataView = function(index){
      echartsApp[index] = echarts.init(elemDataView[index], layui.echartsTheme);
      echartsApp[index].setOption(options[index]);
      window.onresize = echartsApp[index].resize;
    };
    //没找到DOM，终止执行
    if(!elemDataView[0]) return;
    renderDataView(0);
    
  });
  exports('home', {})
});