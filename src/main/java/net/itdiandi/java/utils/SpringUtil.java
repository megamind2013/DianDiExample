package net.itdiandi.java.utils;

import org.springframework.context.ApplicationContext;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils
* @ClassName SpringUtil
* @Description TODO
* @author 刘吉超
* @date 2016-02-24 19:48:25
*/
public class SpringUtil {
	private static ApplicationContext applicationContext;

	public static Object getBean(String beanName) {
		if (applicationContext != null) {
			return applicationContext.getBean(beanName);
		}

		return null;
	}

	public static <T> T getBean(String beanName, Class<T> clz) {
		if (applicationContext != null) {
			return applicationContext.getBean(beanName, clz);
		}

		return null;
	}

	public static void setApplicationContext(ApplicationContext appContext) {
		applicationContext = appContext;
	}
}