package com.atguigu.crowd.mvc.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.atguigu.crowd.constant.CrowdConstant;
import com.atguigu.crowd.exception.AccessForbiddenException;
import com.atguigu.crowd.exception.LoginAcctAlreadyInUseException;
import com.atguigu.crowd.exception.LoginAcctAlreadyInUseForUpdateException;
import com.atguigu.crowd.exception.LoginFailedException;
import com.atguigu.crowd.util.CrowdUtil;
import com.atguigu.crowd.util.ResultEntity;
import com.google.gson.Gson;

//@ControllerAdvice 表示当前类是一个基于注解的异常处理器类
@ControllerAdvice
public class CrowdExceptionResolver {

	@ExceptionHandler(value = LoginFailedException.class)
	public ModelAndView resolveLoginFailedException(LoginFailedException exception, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String viewName = "admin-login";
		return commonResolve(viewName, exception, request, response);
	}
	
	@ExceptionHandler(value = LoginAcctAlreadyInUseException.class)
	public ModelAndView resolveLoginAcctAlreadyInUse(LoginAcctAlreadyInUseException exception, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String viewName = "admin-add";
		return commonResolve(viewName, exception, request, response);
	}
	@ExceptionHandler(value = LoginAcctAlreadyInUseForUpdateException.class)
	public ModelAndView resolveLoginAcctAlreadyInUseForUpdate(LoginAcctAlreadyInUseForUpdateException exception, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String viewName = "system-error";
		return commonResolve(viewName, exception, request, response);
	}
	
	@ExceptionHandler(value = Exception.class)
	public ModelAndView resolveAccessForbiddenException(Exception exception, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String viewName = "system-error";
		return commonResolve(viewName, exception, request, response);
	}

	private ModelAndView commonResolve(String viewName, Exception exception, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// 1.判断当前请求类型
		boolean judgeResult = CrowdUtil.judgeRequestType(request);
		// 2.如果是Ajax请求
		if (judgeResult) {
			//System.out.println(exception.getMessage());
			// 3.创建ResultEntity对象
			ResultEntity<Object> resultEntity = ResultEntity.failed(exception.getMessage());
			// 4.创建Gson对象
			Gson gson = new Gson();
			// 5.将ResultEntity对象转换为JSON字符串
			String json = gson.toJson(resultEntity);
			// 6.将JSON字符串作为响应体返回给浏览器
			response.getWriter().write(json);
			//System.out.println("执行了");
			// 7.由于上面已经通过原生的response对象返回了响应，所以不提供ModelAndView对象
			return null;
		}
		// 8.如果不是Ajax请求则创建ModelAndView对象
		ModelAndView modelAndView = new ModelAndView();

		// 9.将Exception对象存入模型
		modelAndView.addObject(CrowdConstant.ATTR_NAME_EXCEPTION, exception);

		// 10.设置对应的视图名称
		modelAndView.setViewName(viewName);
		return modelAndView;
	}
}
