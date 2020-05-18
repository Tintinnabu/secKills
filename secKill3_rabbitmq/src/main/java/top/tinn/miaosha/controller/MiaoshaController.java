package top.tinn.miaosha.controller;

import org.springframework.beans.factory.InitializingBean;
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
import top.tinn.miaosha.rabbitmq.MQSender;
import top.tinn.miaosha.rabbitmq.MiaoshaMessage;
import top.tinn.miaosha.redis.GoodsKey;
import top.tinn.miaosha.redis.MiaoshaKey;
import top.tinn.miaosha.redis.OrderKey;
import top.tinn.miaosha.redis.RedisService;
import top.tinn.miaosha.result.CodeMsg;
import top.tinn.miaosha.result.Result;
import top.tinn.miaosha.service.GoodsService;
import top.tinn.miaosha.service.MiaoshaService;
import top.tinn.miaosha.service.MiaoshaUserService;
import top.tinn.miaosha.service.OrderService;
import top.tinn.miaosha.vo.GoodsVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

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

	@Autowired
	private MQSender sender;

	private Map<Long, Boolean> localOverMap;

	/**
	 * initialize
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		localOverMap = new HashMap<>();
		if (goodsList == null) return;

		//加载商品库存
		for (GoodsVo goods : goodsList){
			localOverMap.put(goods.getId(), false);
			redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goods.getId(), goods.getStockCount());
		}

	}

	@RequestMapping(value="/reset", method=RequestMethod.GET)
	@ResponseBody
	public Result<Boolean> reset(Model model) {
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		for(GoodsVo goods : goodsList) {
			goods.setStockCount(10);
			redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), 10);
			localOverMap.put(goods.getId(), false);
		}
		redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
		redisService.delete(MiaoshaKey.isGoodsOver);
		miaoshaService.reset(goodsList);
		return Result.success(true);
	}

    @RequestMapping(value = "/do_miaosha", method = RequestMethod.POST)
	@ResponseBody
    public Result<Integer> miaosha(Model model,
								  MiaoshaUser user,
								  @RequestParam("goodsId")long goodsId) {
    	model.addAttribute("user", user);
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}

    	//内存标记
    	if (localOverMap.get(goodsId)) return Result.error(CodeMsg.MIAO_SHA_OVER);

    	//预减库存
		long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
		if (stock < 0){
			localOverMap.put(goodsId, true);
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}
		//判断是否已经秒杀到了
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
		if(order != null) {
			return Result.error(CodeMsg.REPEATE_MIAOSHA);
		}
		//入队
		MiaoshaMessage message = new MiaoshaMessage();
		message.setUser(user);
		message.setGoodsId(goodsId);
		sender.sendMiaoshaMessage(message);
		return Result.success(0);//排队

    	/*
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
    	*/
    }

	/**
	 * orderId：成功
	 * -1：秒杀失败
	 * 0： 排队中
	 * */
	@RequestMapping(value="/result", method=RequestMethod.GET)
	@ResponseBody
	public Result<Long> miaoshaResult(Model model,MiaoshaUser user,
									  @RequestParam("goodsId")long goodsId) {
		model.addAttribute("user", user);
		if(user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		long result  = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
		return Result.success(result);
	}
}
