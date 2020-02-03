package net.itdiandi.java.utils.code.verity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils.code.verity
* @ClassName VerityCodeUtil
* @Description 验证码工具
* @author 刘吉超
* @date 2016-03-08 11:30:04
*/
public class VerityCodeUtil {
	// 日志
	private static Logger logger = Logger.getLogger(VerityCodeUtil.class);
    // 验证码在session中默认的ID
    public static final String VERITY_CODE_KEY = "verityCode";

    private VerityCodeUtil() {
    }

    /**
     * 生成验证码图片文件
     * 
     * @param request request请求
     * @param outputStream OutputStream对象
     * @param length 验证码数字长度
     * @return void
    */
    public static void build(HttpServletRequest request, OutputStream outputStream, int length) {
    	if(outputStream != null){
    		try {
    			outputStream.write(build(request,length,null));
    		} catch (IOException e) {
    			logger.error("", e);
    		}
    	}
    }
    
    /**
     * 生成验证码图片文件
     *
     * @param length 验证码数字长度
     * @param request request请求
     * @param key 数字字符串放入session中的key
     * @return BufferedImage
     */
    public static byte[] build(HttpServletRequest request,int length, String key) {
        //内存中创建图像
        int width = length * 15, height = 20;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        //获取图像上下文
        Graphics graphics = image.getGraphics();

        //随机类
        Random random = new Random();

        //设置背景色
        graphics.setColor(getRandomColor(200, 250));
        graphics.fillRect(0, 0, width, height);

        //设置字体
        graphics.setFont(new Font("Times New Roman", Font.PLAIN, 18));

        //随机产生干扰线
        graphics.setColor(getRandomColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            graphics.drawLine(x, y, x + xl, y + yl);
        }

        // 取随机产生的认证码(4位数字)
        String sRand = "";
        for (int i = 0; i < length; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand += rand;
            // 将认证码显示到图象中
            graphics.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            // 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
            graphics.drawString(rand, 13 * i + 6, 16);
        }

        // 图象生效
        graphics.dispose();

        // 将认证码存入SESSION
        // 默认的session key
        if (request != null) {
            key = (StringUtils.isEmpty(key) ? VERITY_CODE_KEY : key);
            request.getSession().setAttribute(key, sRand);
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
			ImageIO.write(image, "JPEG", baos);
		} catch (IOException e) {
			logger.error("", e);
		}
        return baos.toByteArray();
    }

    /**
     * 获取颜色
     * 
     * @param fontColor
     * @param backColor
     * @return Color
    */
    private static Color getRandomColor(int fontColor, int backColor) {
        Random random = new Random();
        if (fontColor > 255)
            fontColor = 255;
        if (backColor > 255)
            backColor = 255;

        int red = fontColor + random.nextInt(backColor - fontColor);
        int green = fontColor + random.nextInt(backColor - fontColor);
        int blue = fontColor + random.nextInt(backColor - fontColor);

        return new Color(red, green, blue);
    }
}
