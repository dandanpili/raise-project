package com.atguigu.crowd.handler;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.atguigu.crowd.api.RedisRemoteService;
import com.atguigu.crowd.config.ShortMessageProperties;
import com.atguigu.crowd.constant.CrowdConstant;
import com.atguigu.crowd.util.CrowdUtil;
import com.atguigu.crowd.util.ResultEntity;

@Controller
public class MemberHandler {

	@Autowired
	private ShortMessageProperties shortMessageProperties;
	
	@Autowired
	private RedisRemoteService redisRemoteService;
	
	@ResponseBody
	@RequestMapping("/auth/member/send/short/message.json")
	public ResultEntity<String> sendMessage(@RequestParam("phoneNum") String phoneNum){
		
		//1.发送验证码到phoneNum手机
		ResultEntity<String> sendMessageResultEntity = CrowdUtil.sendCodeByShortMessage(
				phoneNum, 
				shortMessageProperties.getAppCode(), 
				shortMessageProperties.getSign(), 
				shortMessageProperties.getSkin(), 
				shortMessageProperties.getHost(), 
				shortMessageProperties.getPath());
		
		//2.判断短信发送结果
		if(ResultEntity.SUCCESS.equals(sendMessageResultEntity.getResult())) {
			//3.如果发送成功，将验证码存入Redis
			//①从sendMessageResultEntity中获取随机生成的验证码
			String code = sendMessageResultEntity.getData();
			//②拼接一个用于在Redis中存储数据的key
			String key = CrowdConstant.REDIS_CODE_PREFIX + phoneNum;
			//③调用远程接口存入Redis
			ResultEntity<String> saveCodeResultEntity = redisRemoteService.setRedisKeyValueRemoteTimeout(key, code, 15, TimeUnit.MINUTES);
			//④判断结果
			if(ResultEntity.SUCCESS.equals(saveCodeResultEntity.getResult())) {
				return ResultEntity.successWithoutData();
			}else {
				return saveCodeResultEntity;
			}
		}else {
			return sendMessageResultEntity;
		}
		
	}
}
