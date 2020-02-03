package net.itdiandi.java.utils.code.qrcode;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

/** 
* @ProjectName Utils
* @PackageName net.itdiandis.utils.code.qrcode
* @ClassName ZxingUtil
* @Description 二维码工具
* @author 刘吉超
* @date 2016-03-08 11:10:19
*/
public class ZxingUtil {
	// 日志
	private static Logger logger = Logger.getLogger(ZxingUtil.class);
	
	// 定义颜色
	private static final int BLACK = 0xFF000000;
	private static final int WHITE = 0xFFFFFFFF;
	
	// 编码
	private static final String UTF8 = "UTF-8";
	// 二维码格式
	private static final String qrCodeFormat = "gif";
	
	/**
	 * 将文字转换为二维码
	 * 
	 * @param text 文字
	 * @param width 宽度
	 * @param height 高度
	 * @return InputStream 返回类型
	*/
	public static InputStream encoder(String text,int width,int height) {
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		// 内容所使用编码
		hints.put(EncodeHintType.CHARACTER_SET, UTF8);
		
		// 字节数组
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try{
			BitMatrix bitMatrix = new MultiFormatWriter().encode(text,BarcodeFormat.QR_CODE, width, height, hints);
			// 生成二维码
			writeToByteArrayOutputStream(bitMatrix, qrCodeFormat, byteArrayOutputStream);
		}catch(Exception e){
			logger.error("生成二维码：",e);
		}
		
		logger.debug("二维码生成完成");
		
		return new ByteArrayInputStream(byteArrayOutputStream.toByteArray()); 
	}
	
	/**
	 * 将文字转换为二维码
	 * 
	 * @param text 文字
	 * @param width 宽度
	 * @param height 高度
	 * @param imgPath 文件位置
	 * @return void
	*/
	public static void encoder(String text,int width,int height,String imgPath) {
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		// 内容所使用编码
		hints.put(EncodeHintType.CHARACTER_SET, UTF8);
		
		try{
			BitMatrix bitMatrix = new MultiFormatWriter().encode(text,BarcodeFormat.QR_CODE, width, height, hints);
			// 生成二维码
			writeToFile(bitMatrix, qrCodeFormat, new File(imgPath));
		}catch(Exception e){
			logger.error("生成二维码：",e);
		}
		
		logger.debug("二维码生成完成");
	}
	
	/**
	 * 将二维码解析为文字
	 * 
	 * @param fileLocation 文件位置
	 * @return String 返回类型
	*/
	public static String decoder(String fileLocation){
		try {
			BufferedImage bufferedImage = ImageIO.read(new File(fileLocation));
			
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));
			
			// 指定编码
			Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
			hints.put(DecodeHintType.CHARACTER_SET, UTF8);
			
			return new MultiFormatReader().decode(bitmap, hints).toString();
		} catch (Exception e) {
			logger.error("解析二维码：", e);
		}
		
		return null;
	}
	
	/**
	 * 将二维码解析为文字
	 * 
	 * @param inputStream 二维码流
	 * @return String 返回类型
	*/
	public static String decoder(InputStream inputStream){
		try {
			BufferedImage bufferedImage = ImageIO.read(inputStream);
			
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));
			
			// 指定编码
			Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
			hints.put(DecodeHintType.CHARACTER_SET, UTF8);
			
			return new MultiFormatReader().decode(bitmap, hints).toString();
		} catch (Exception e) {
			logger.error("解析二维码：", e);
		}
		
		return null;
	}
		
	/**
	 * 生成图片
	 * 
	 * @param matrix
	 * @return BufferedImage
	*/
	public static BufferedImage toBufferedImage(BitMatrix matrix) {
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
			}
		}
		return image;
	}
	
	/**
	 * 生成图片，并写入硬盘
	 * 
	 * @param matrix
	 * @param format
	 * @param file
	 * @throws IOException
	*/
	public static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {
		BufferedImage image = toBufferedImage(matrix);
		if (!ImageIO.write(image, format, file)) {
			throw new IOException("Could not write an image of format " + format + " to " + file);
		}
	}
	
	/**
	 * 生成图片，并写入ByteArrayOutputStream
	 * 
	 * @param matrix
	 * @param format
	 * @param file
	 * @throws IOException
	*/
	public static void writeToByteArrayOutputStream(BitMatrix matrix, String format, ByteArrayOutputStream byteArrayOutputStream) throws IOException {
		BufferedImage image = toBufferedImage(matrix);
		if (!ImageIO.write(image, format, byteArrayOutputStream)) {
			throw new IOException("Could not write an image of format " + format + " to byteArrayOutputStream");
		}
	}
}