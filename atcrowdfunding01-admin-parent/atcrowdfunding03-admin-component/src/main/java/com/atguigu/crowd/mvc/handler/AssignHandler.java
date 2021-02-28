package com.atguigu.crowd.mvc.handler;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.atguigu.crowd.entity.Auth;
import com.atguigu.crowd.entity.Role;
import com.atguigu.crowd.service.api.AdminService;
import com.atguigu.crowd.service.api.AuthService;
import com.atguigu.crowd.service.api.RoleService;
import com.atguigu.crowd.util.ResultEntity;

@Controller
public class AssignHandler {
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private AuthService authService;
	
	@ResponseBody
	@RequestMapping("/assign/do/role/assign/auth.json")
	public ResultEntity<String> saveRoleAuthRelationship(
				@RequestBody Map<String, List<Integer>> map
			){
		authService.saveRoleAuthRelationship(map);
		return ResultEntity.successWithoutData();
	}
	
	@ResponseBody
	@RequestMapping("/assign/get/assigned/auth/id/by/role/id.json")
	public ResultEntity<List<Integer>> getAssignedAuthIdByRoleId(
			@RequestParam("roleId") Integer roleId
			){
		List<Integer> authIdList = authService.getAssignedAuthIdByRoleId(roleId);
		
		return ResultEntity.successWithData(authIdList);
	}
	
	@ResponseBody
	@RequestMapping("/assign/get/all/auth.json")
	public ResultEntity<List<Auth>> getAllAuth(){
		List<Auth> authList = authService.getAll();
		return ResultEntity.successWithData(authList);
	}
	
	@RequestMapping("assign/do/role/assgin.html")
	public String saveAdminRoleRelationship(
				@RequestParam("adminId") Integer adminId,
				@RequestParam("pageNum") Integer pageNum,
				@RequestParam("keyword") String keyword,
				//设置required=false就表示这个请求参数不是必须的
				@RequestParam(value="roleIdList", required=false) List<Integer> roleIdList
			) {
		adminService.saveAdminRoleRelationship(adminId, roleIdList);
		return "redirect:/admin/get/page.html?pageNum=" + pageNum + "&keyword=" + keyword;
	}
	
	@RequestMapping("assign/to/assign/role/page.html")
	public String toAssignRolePage(
				@RequestParam("adminId") Integer adminId,
				ModelMap modelMap
			) {
		
		//1.查询已分配角色
		List<Role> assignedRoleList = roleService.getAssignedRole(adminId);
		
		//2.查询为分配角色
		List<Role> unAssignedRoleList = roleService.getUnAssignedRole(adminId);
		//3.存入模型(本质上其实是：request.setAttribute("attrName", attrValue);
		modelMap.addAttribute("assignedRoleList", assignedRoleList);
		modelMap.addAttribute("unAssignedRoleList", unAssignedRoleList);
		
		return "assign-role";
	}
	
}
