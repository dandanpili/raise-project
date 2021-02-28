package com.atguigu.crowd.mvc.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.crowd.entity.Menu;
import com.atguigu.crowd.service.api.MenuService;
import com.atguigu.crowd.util.ResultEntity;


//@ResponseBody
//@Controller
@RestController
public class MenuHandler {

	@Autowired
	private MenuService menuService;
	
	@RequestMapping("/menu/remove.json")
	public ResultEntity<String> removeMenu(@RequestParam("id") Integer id){
		menuService.removeMenu(id);
		return ResultEntity.successWithoutData();
	}
	
	@RequestMapping("/menu/update.json")
	public ResultEntity<String> updateMenu(Menu menu){
		menuService.updateMenu(menu);
		return ResultEntity.successWithoutData();
	}
	
	@RequestMapping("/menu/save.json")
	public ResultEntity<String> saveMenu(Menu menu){
		
		menuService.saveMenu(menu);
		return ResultEntity.successWithoutData();
	}
	
	@RequestMapping("/menu/get/whole/tree.json")
	public ResultEntity<Menu> getWholeTreeNew(){
		
		//1.查询全部Menu对象
		List<Menu> menuList = menuService.getAll();
		
		//2.声明一个变量用来存储找到的根节点
		Menu root = null;
		
		//3.创建Map对象用来存储id和Menu对象对应关系，便于查找父结点
		Map<Integer, Menu> menuMap = new HashMap<>();
		
		//4.遍历menuList填充menuMap
		for(Menu menu : menuList) {
			Integer id = menu.getId();
			menuMap.put(id, menu);
		}
		
		//5.再次遍历menuList
		for(Menu menu : menuList) {
			//6.获取当前menu对象的pid属性值
			Integer pid = menu.getPid();
			
			//7.检查pid是否为null，为null说明当前节点是根节点，没有父节点
			if(pid == null) {
				//8.把当前正在遍历的这个menu对象赋值给root
				root = menu;
				
				//9.停止本次循环，继续执行下一次循环
				continue;
			}
			
			//10.获取父节点
			Menu father = menuMap.get(pid);
			//11.将当前节点塞入父结点的子结点列表中
			father.getChildren().add(menu);
		}
		//12.将树形结构的根节点返回
		return ResultEntity.successWithData(root);
	}
}
