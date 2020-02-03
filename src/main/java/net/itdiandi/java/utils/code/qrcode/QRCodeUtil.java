package net.itdiandi.java.utils.code.qrcode;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.swetake.util.Qrcode;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils.code.qrcode
* @ClassName QRCodeUtil
* @Description TODO
* @Author 刘吉超
* @Date 2016-05-11 15:22:38
*/
public class QRCodeUtil {
	// 日志
	private static Logger logger = Logger.getLogger(QRCodeUtil.class);
	// 二维码格式
	private static final String qrCodeFormat = "gif";
	// 编码
	private static final String UTF8 = "UTF-8";
	
	/**
	 * 将文字转换为二维码
	 * 
	 * @param text 文字
	 * @param width 宽度
	 * @param height 高度
	 * @return InputStream 返回类型
	*/
    public static InputStream encoder(String text,int width,int height){
    	// 实例化Qrcode
        Qrcode qrcode=new Qrcode();
        // 设置二维码的排错率L(7%) M(15%) Q(25%) H(35%)
        qrcode.setQrcodeErrorCorrect('M');
        qrcode.setQrcodeEncodeMode('B');        
        // 设置二维码尺寸(1~49)
        qrcode.setQrcodeVersion(7);
        // 设置图片尺寸
        BufferedImage bufImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        
        // 绘制二维码图片
        Graphics2D gs=bufImg.createGraphics();
        // 设置二维码背景颜色
        gs.setBackground(Color.WHITE);
        // 创建一个矩形区域
        gs.clearRect(0, 0, width, height);
        // 设置二维码的图片颜色值 黑色
        gs.setColor(Color.BLACK);
        
        // 字节数组
     	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 获取内容的字节数组,设置编码集
        try {
            byte[] contentBytes = text.getBytes(UTF8);
            int pixoff=2;
            // 输出二维码
            if(contentBytes.length>0&&contentBytes.length<120){
                boolean[][] codeOut=qrcode.calQrcode(contentBytes);
                for(int i=0;i<codeOut.length;i++){
                    for(int j=0;j<codeOut.length;j++){
                        if(codeOut[j][i]){
                            gs.fillRect(j*3+pixoff, i*3+pixoff, 3, 3);
                        }
                    }
                }    
            }
            gs.dispose();
            bufImg.flush();
            // 生成二维码图片
            writeToByteArrayOutputStream(bufImg,qrCodeFormat,byteArrayOutputStream);
        } catch (IOException e) {
        	logger.error("", e);
        }
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray()); 
    }
	
	/**
	 * 将文字转换为二维码
	 * 
	 * @param text 文字
	 * @param width 宽度
	 * @param height 高度
	 * @param imgPath 文件位置
	 * @return InputStream 返回类型
	*/
    public static void encoder(String text,int width,int height,String imgPath){
        // 实例化Qrcode
        Qrcode qrcode=new Qrcode();
        // 设置二维码的排错率L(7%) M(15%) Q(25%) H(35%)
        qrcode.setQrcodeErrorCorrect('M');
        qrcode.setQrcodeEncodeMode('B');        
        // 设置二维码尺寸(1~49)
        qrcode.setQrcodeVersion(7);
        // 设置图片尺寸
        BufferedImage bufImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        
        // 绘制二维码图片
        Graphics2D gs=bufImg.createGraphics();
        // 设置二维码背景颜色
        gs.setBackground(Color.WHITE);
        // 创建一个矩形区域
        gs.clearRect(0, 0, width, height);
        // 设置二维码的图片颜色值 黑色
        gs.setColor(Color.BLACK);
        
        // 获取内容的字节数组,设置编码集
        try {
            byte[] contentBytes = text.getBytes(UTF8);
            int pixoff=2;
            // 输出二维码
            if(contentBytes.length>0&&contentBytes.length<120){
                boolean[][] codeOut=qrcode.calQrcode(contentBytes);
                for(int i=0;i<codeOut.length;i++){
                    for(int j=0;j<codeOut.length;j++){
                        if(codeOut[j][i]){
                            gs.fillRect(j*3+pixoff, i*3+pixoff, 3, 3);
                        }
                    }
                }    
            }
            gs.dispose();
            bufImg.flush();
            // 生成二维码图片
            writeToFile(bufImg,qrCodeFormat,new File(imgPath));
        } catch (IOException e) {
        	logger.error("", e);
        }
    }
    
    /**
	 * 生成图片，并写入硬盘
	 * 
	 * @param image
	 * @param format
	 * @param file
	 * @throws IOException
	*/
	public static void writeToFile(BufferedImage image, String format, File file) throws IOException {
		if (!ImageIO.write(image, format, file)) {
			throw new IOException("Could not write an image of format " + format + " to " + file);
		}
	}
	
	/**
	 * 生成图片，并写入ByteArrayOutputStream
	 * 
	 * @param image
	 * @param format
	 * @param file
	 * @throws IOException
	*/
	public static void writeToByteArrayOutputStream(BufferedImage image, String format, ByteArrayOutputStream byteArrayOutputStream) throws IOException {
		if (!ImageIO.write(image, format, byteArrayOutputStream)) {
			throw new IOException("Could not write an image of format " + format + " to byteArrayOutputStream");
		}
	}
}
