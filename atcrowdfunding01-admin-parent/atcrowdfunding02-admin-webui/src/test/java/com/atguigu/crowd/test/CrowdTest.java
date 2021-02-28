package com.atguigu.crowd.test;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.atguigu.crowd.entity.Admin;
import com.atguigu.crowd.entity.Role;
import com.atguigu.crowd.mapper.AdminMapper;
import com.atguigu.crowd.mapper.RoleMapper;
import com.atguigu.crowd.service.api.AdminService;



//在类上标记必要的注解，Spring整合Junit
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-persist-mybatis.xml", "classpath:spring-persist-tx.xml"})
public class CrowdTest {
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private AdminMapper adminMapper;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private RoleMapper roleMapper;
	
	@Test
	public void testRole() {
		for(int i = 0; i < 235; i++) {
			roleMapper.insert(new Role(null, "role" + i));
		}
	}
	
	@Test
	public void test() {
		for(int i = 0; i < 238; i++) {
			adminMapper.insert(new Admin(null, "loginAcct" + i, "userPswd" + i, "userName" + i, "email" + i, null));
		}
	}
	
	@Test
	public void testTx() {
		Admin admin = new Admin(null, "jerry", "123456", "杰瑞", "jerry@qq.com", null);
		adminService.saveAdmin(admin);
	}
	
	@Test
	public void testLog() {
		//1.获取Logger对象，这里传入的Class对象就是当前打印日志的类
		Logger logger = LoggerFactory.getLogger(CrowdTest.class);
		//2.根据不同日志级别打印日志
		logger.debug("Debug level!!!");
		logger.debug("Debug level!!!");
		logger.debug("Debug level!!!");
		
		logger.info("Info level!!!");
		logger.info("Info level!!!");
		logger.info("Info level!!!");
		
		logger.warn("Warn level!!!");
		logger.warn("Warn level!!!");
		logger.warn("Warn level!!!");
		
		logger.error("Error level!!!");
		logger.error("Error level!!!");
		logger.error("Error level!!!");
	}
	
	@Test
	public void testInsertAdmin() {
		Admin admin = new Admin(null, "tom", "123123", "邵哥", "shao@qq.com", null);
		int count = adminMapper.insert(admin);
		System.out.println("受影响的行数" + count);
	}
	
	@Test
	public void testConnection() throws SQLException {
		Connection connection = dataSource.getConnection();
		System.out.println(connection);
	}
}
