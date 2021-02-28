package com.atguigu.crowd.test;

import org.junit.Test;

import com.atguigu.crowd.util.CrowdUtil;

public class StringTest {

	@Test
	public void testMd5() {
		String source = "123123";
		String result = CrowdUtil.md5(source);
		System.out.println(result);
	}
}
