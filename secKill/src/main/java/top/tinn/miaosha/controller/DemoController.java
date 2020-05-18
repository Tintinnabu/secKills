package top.tinn.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.tinn.miaosha.domain.User;
import top.tinn.miaosha.redis.BasePrefix;
import top.tinn.miaosha.redis.RedisService;
import top.tinn.miaosha.redis.UserKey;
import top.tinn.miaosha.result.CodeMsg;
import top.tinn.miaosha.result.Result;
import top.tinn.miaosha.service.UserService;
import top.tinn.miaosha.service.impl.UserServiceImpl;


@Controller
@RequestMapping("/demo")
public class DemoController {
	@Autowired
	private UserService userService;
	@Autowired
	private RedisService redisService;
	
	@RequestMapping("/")
	@ResponseBody
	public String home() {
		return "Hello World!";
	}
	//1.rest api json输出 2.页面
	@RequestMapping("/hello")
	@ResponseBody
	public Result<String> hello() {
		return Result.success("hello,imooc");
	   // return new Result(0, "success", "hello,imooc");
	}

	@RequestMapping("/helloError")
	@ResponseBody
	public Result<String> helloError() {
		return Result.error(CodeMsg.SERVER_ERROR);
		//return new Result(500102, "XXX");
	}

	@RequestMapping("/thymeleaf")
	public String thymeleaf(Model model) {
		model.addAttribute("name", "Tinn");
		return "hello";
	}

	@RequestMapping("/db/get")
	@ResponseBody
	public Result<User> dbGet(){
		User user = userService.getUserById(1);
		return Result.success(user);
	}

	@RequestMapping("/db/tx")
	@ResponseBody
	public Result<Boolean> doTx(){
		userService.tx();
		return Result.success(true);
	}

	@RequestMapping("/redis/test")
	@ResponseBody
	public Result<Long> redisGet(){
		Long v1 = 100L;
		UserKey key = UserKey.getById;

		redisService.set(key, "111", v1);
		Long v2 = redisService.get(key, "111", long.class);
		return Result.success(v2);
	}

	 	
}
