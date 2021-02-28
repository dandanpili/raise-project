package com.atguigu.crowd.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.crowd.entity.po.MemberPO;
import com.atguigu.crowd.service.api.MemberService;
import com.atguigu.crowd.util.ResultEntity;

@RestController
public class MemberProviderHandler {

	@Autowired
	private MemberService memberService;
	
	@RequestMapping("/get/memberpo/by/login/acct/remote")
	ResultEntity<MemberPO> getMemberPOByLoginAcctRemote(@RequestParam("loginacct") String loginacct){
		
		try {
			
			//1.调用本地Service完成查询
			MemberPO memberPO = memberService.getMemberPOByLoginAcct(loginacct);
			//2.如果没有抛异常，那么就返回成功的结果
			return ResultEntity.successWithData(memberPO);
		}catch(Exception e) {
			e.printStackTrace();
			//3.如果捕获到异常，就返回失败的结果
			return ResultEntity.failed(e.getMessage());
		}
	}
}
