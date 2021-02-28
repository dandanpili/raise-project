package com.atguigu.crowd.mvc.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.atguigu.crowd.constant.CrowdConstant;

//表示当前是一个配置类
@Configuration
//启用Web环境下权限控制功能
@EnableWebSecurity
//弃用全局方法权限控制功能，并且设置prePostEnabled = true，保证@PreAuthority、@PostAuthority、@PreFilter生效
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebAppSecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	private UserDetailsService userDetailsService;
	
	/*
	 * 在这里声明无法在xxxService中装配
	 * @Bean public BCryptPasswordEncoder getPasswordEncoder() { return new
	 * BCryptPasswordEncoder(); }
	 */
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
//	@Test
//	public void test() {
//		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//		String res = bCryptPasswordEncoder.encode("123123");
//		System.out.println(res);
//	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder builder) throws Exception {
//		builder
//			.inMemoryAuthentication()
//			.withUser("tom")
//			.password("123123")
//			.roles("ADMIN")
//			;
		
		//正式功能中使用基于数据库的认证
		builder
			.userDetailsService(userDetailsService)
			.passwordEncoder(passwordEncoder);
	}
	
	@Override
	protected void configure(HttpSecurity security) throws Exception {
		security
			.authorizeRequests()	//对请求开始授权
			.antMatchers("/index.jsp")	
			.permitAll()
			.antMatchers ("/admin/to/login/page.html")	
			.permitAll()
			.antMatchers("/bootstrap/**")
			.permitAll()
			.antMatchers("/crowd/**")
			.permitAll()
			.antMatchers("/css/**")
			.permitAll()
			.antMatchers("/fonts/**")
			.permitAll()
			.antMatchers("/img/**")
			.permitAll()
			.antMatchers("/jquery/**")
			.permitAll()
			.antMatchers("/layer/**")
			.permitAll()
			.antMatchers("/script/**")
			.permitAll()
			.antMatchers("/ztree/**")
			.permitAll()
			.antMatchers("/admin/get/page.html")	//针对分页显示Admin数据设定访问控制
			//.hasRole("经理")
			.access("hasRole('经理') OR hasAuthority('user:get')")
			.anyRequest()	//其他任意请求
			.authenticated()	//认证以后访问
			.and()
			.exceptionHandling()
			.accessDeniedHandler(new AccessDeniedHandler() {
				
				@Override
				public void handle(HttpServletRequest request, HttpServletResponse response,
						AccessDeniedException accessDeniedException) throws IOException, ServletException {
					request.setAttribute("exception", new Exception(CrowdConstant.MESSAGE_ACCESS_DENIED));
					request.getRequestDispatcher("/WEB-INF/system-error.jsp").forward(request, response);
				}
			})
			.and()
			.csrf()			//防跨站请求伪造功能
			.disable()		//禁用
			.formLogin()	//开启表单登录的功能
			.loginPage("/admin/to/login/page.html") //指定登录页面
			.loginProcessingUrl("/security/do/login.html")	//指定处理登录请求的地址
			.defaultSuccessUrl("/admin/to/main/page.html")	//指定登录成功后前往的地址
			.usernameParameter("loginAcct")	//账号的请求参数名称
			.passwordParameter("userPswd")  //密码的请求参数名称
			.and()
			.logout()						//开启退出功能
			.logoutUrl("/security/do/logout.html")	//指定退出登录地址
			.logoutSuccessUrl("/admin/to/login/page.html")	//指定退出成功以后前往的地址
			;
	}
}
