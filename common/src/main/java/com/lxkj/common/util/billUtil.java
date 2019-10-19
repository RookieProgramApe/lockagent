package com.lxkj.common.util;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Date;

public class billUtil {


    private static float jPEGcompression = 0.75f;// 图片清晰比率

    /**
     * @return : java.lang.String
     * @Description : 将二维码图片和文字生成到一张图片上
     * @Param : headurl 头像
     * @Param : originalImg 背景图
     * @Param : qrCodeImg 二维码地址
     * @Param : shareDesc 图片文字
     * @Author : houzhenghai
     * @Date : 2018/8/15
     */
    public static String generateImg(String headurl,String originalImg, String qrCodeImg, String name,String filePath,String mapping) throws Exception {
        // 头像
        BufferedImage headImg = ImageIO.read(new URL(headurl));
        // 加背景图片
        BufferedImage imageLocal = ImageIO.read(new URL(originalImg));
        // 加载用户的二维码
        BufferedImage imageCode = ImageIO.read(new URL(qrCodeImg));
        // 以原图片为模板
        Graphics2D g = imageLocal.createGraphics();
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g.setComposite(ac);
        g.setBackground(Color.WHITE);
        // 头像
        g.drawImage(billUtil.setClip(headImg, 120), ((imageLocal.getWidth()/2)-(120/2)), 160, 120, 120, null);
        // 二维码
        g.drawImage(imageCode, (imageLocal.getWidth()/2)-(270/2), imageLocal.getHeight() - (200*2), 270, 270, null);
        // 姓名
        g.setFont(new Font("楷体", Font.PLAIN, 28));
        g.setColor(Color.WHITE);
        g.drawString(name, ((imageLocal.getWidth()/2)-(28*name.length()/2)), 320);
        // 诚邀您一起加入安纹
        g.setFont(new Font("楷体", Font.BOLD, 40));
        g.setColor(Color.WHITE);
        g.drawString("诚邀您一起加入安纹", (imageLocal.getWidth()/2)-(270/2)-55, (imageLocal.getHeight()/2-40));
        // 合作共赢，共创美好未来
        g.setFont(new Font("楷体", Font.BOLD, 40));
        g.setColor(Color.WHITE);
        g.drawString("合作共赢，共创美好未来", (imageLocal.getWidth()/2)-(270/2)-90, (imageLocal.getHeight()/2+30));
        // 长按识别二维码
        g.setFont(new Font("楷体",  Font.PLAIN, 28));
        g.setColor(Color.WHITE);
        g.drawString("长按识别二维码", (imageLocal.getWidth()/2)-(270/2)+32, imageLocal.getHeight() - 60);
        InputStream inputStream = billUtil.bufferedImageToInputStream(imageLocal);


        String path = filePath +File.separator + "qrcode";
        String fileName = ID.nextGUID() + ".jpg";
        billUtil.saveFile(inputStream, filePath+ File.separator+"qrcode"+ File.separator+fileName);
        return mapping + "/"+"qrcode"+ "/" + fileName;
    }

    public static void saveFile(InputStream is, String fileName) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(is);
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName))) {
            int len;
            byte[] b = new byte[1024];
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }
        }
    }

    public static InputStream bufferedImageToInputStream(BufferedImage backgroundImage) throws IOException {
        return bufferedImageToInputStream(backgroundImage, "png");
    }

    /**
     * backgroundImage 转换为输出流
     *
     * @param backgroundImage
     * @param format
     * @return
     * @throws IOException
     */
    public static InputStream bufferedImageToInputStream(BufferedImage backgroundImage, String format) throws IOException {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        try (
                ImageOutputStream
                        imOut = ImageIO.createImageOutputStream(bs)) {
            ImageIO.write(backgroundImage, format, imOut);
            InputStream is = new ByteArrayInputStream(bs.toByteArray());
            return is;
        }
    }

    public static BufferedImage setClip(BufferedImage srcImage, int radius){
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gs = image.createGraphics();

        gs.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gs.setClip(new RoundRectangle2D.Double(0, 0, width, height, radius, radius));
        gs.drawImage(srcImage, 0, 0, null);
        gs.dispose();
        return image;
    }


    public static String generateImg2(String originalImg,String name,String time,String filePath,String mapping,String url) throws Exception {
        // 加背景图片
        BufferedImage imageLocal = ImageIO.read(new URL(originalImg));
        //章
        BufferedImage zhangImp = ImageIO.read(new URL(url));
        // 以原图片为模板
        Graphics2D g = imageLocal.createGraphics();
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g.setComposite(ac);
        g.setBackground(Color.WHITE);
        // 姓名
        g.setFont(new Font("楷体", Font.BOLD, 45));
        g.setColor(Color.black);
        g.drawString(name, ((imageLocal.getWidth()/2)-(80*name.length()/2)), imageLocal.getHeight()/2-95);
        // 日期
        g.setFont(new Font("楷体", Font.PLAIN, 24));
        g.setColor(Color.black);
        g.drawString(time, (imageLocal.getWidth()/2)+70, (imageLocal.getHeight()/2+378));
        //章
        g.drawImage(zhangImp, (imageLocal.getWidth()/2)+100,  (imageLocal.getHeight()/2+250), 320, 320, null);

        InputStream inputStream = billUtil.bufferedImageToInputStream(imageLocal);
        String path = filePath +File.separator + "qrcode";
        String fileName = ID.nextGUID() + ".jpg";
        billUtil.saveFile(inputStream, filePath+ File.separator+"qrcode"+ File.separator+fileName);
        return mapping + "/"+"qrcode"+ "/" + fileName;
    }

    /**
     * test
     *
     * @param args
     * @throws
     */
    public static void main(String[] args) {
        long starttime = System.currentTimeMillis();
        System.out.println("开始：" + starttime);
        try {
            var url="https://aw.wisehuitong.com/lockagentDownFile/auth.jpg";
            var a="https://aw.wisehuitong.com/lockagentDownFile/zhang.png";
            generateImg2(url,"胡虓","2018年10月05至2019年10月05日","","",a);
           // System.out.println("生成完毕,url=" + img);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("结束：" + (System.currentTimeMillis() - starttime) / 1000);

    }


}
