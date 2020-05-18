package top.tinn.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.tinn.miaosha.domain.MiaoshaOrder;
import top.tinn.miaosha.domain.MiaoshaUser;
import top.tinn.miaosha.domain.OrderInfo;
import top.tinn.miaosha.redis.RedisService;
import top.tinn.miaosha.result.CodeMsg;
import top.tinn.miaosha.result.Result;
import top.tinn.miaosha.service.GoodsService;
import top.tinn.miaosha.service.MiaoshaService;
import top.tinn.miaosha.service.MiaoshaUserService;
import top.tinn.miaosha.service.OrderService;
import top.tinn.miaosha.vo.GoodsVo;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	MiaoshaService miaoshaService;
	
    @RequestMapping(value = "/do_miaosha", method = RequestMethod.POST)
	@ResponseBody
    public Result<OrderInfo> miaosha(Model model,
								  MiaoshaUser user,
								  @RequestParam("goodsId")long goodsId) {
    	model.addAttribute("user", user);
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	//判断库存
    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId); //一个用户发出多个请求
    	int stock = goods.getStockCount();
    	if(stock <= 0) {
			return Result.error(CodeMsg.MIAO_SHA_OVER);
    	}
    	//判断是否已经秒杀到了
    	MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
    	if(order != null) {
    		return Result.error(CodeMsg.REPEATE_MIAOSHA);
    	}
    	//减库存 下订单 写入秒杀订单
    	OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
    	return Result.success(orderInfo);
    }
}
