package net.itdiandi.java.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils
* @ClassName ActionUtil
* @Description action工具类
* @author 刘吉超
* @date 2016-02-24 10:37:33
*/
public class ActionUtil extends ActionSupport {
	private static final long serialVersionUID = 1L;
	
	public static final String ERROR_MSG = "errorMsg";// 错误提示信息
	public static final String SUCCESS_MESSAGE = "successMsg";// 添加或修改操作成功后的提示信息

	/**
	 * 获取request
	 */
	protected HttpServletRequest getRequest() {
		return ServletActionContext.getRequest();
	}

	/**
	 * 获取response
	 */
	protected HttpServletResponse getResponse() {
		return ServletActionContext.getResponse();
	}

	/**
	 * 获取session
	 */
	protected HttpSession getSession() {
		return getRequest().getSession(true);
	}

	/**
	 * 设置成功提示信息
	 * 
	 * @param msg 成功提示信息
	 */
	protected void setSuccessMessage(String successMsg) {
		getRequest().setAttribute(SUCCESS_MESSAGE, successMsg);
	}

	/**
	 * 设置错误提示信息
	 * 
	 * @param errorMsg 错误提示信息
	 */
	protected void setErrorMessage(String errorMsg) {
		getRequest().setAttribute(ERROR_MSG, errorMsg);
	}

	/**
	 * 从request获取参数
	 * 
	 * @param name
	 */
	protected String getRequestParameter(String name) {
		return getRequest().getParameter(name);
	}
	
	/**
	 * 从session取得数据
	 * 
	 * @param name
	 */
	protected String getAttribute(String name) {
		return (String)getSession().getAttribute(name);
	}

	/**
	 * 向session中存放数据
	 * 
	 * @param attr
	 * @param value
	 */
	protected void setAttribute(String attr, String value) {
		getSession().setAttribute(attr, value);
	}
}