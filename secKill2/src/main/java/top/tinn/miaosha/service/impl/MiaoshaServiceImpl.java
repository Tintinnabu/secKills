package top.tinn.miaosha.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.tinn.miaosha.domain.MiaoshaUser;
import top.tinn.miaosha.domain.OrderInfo;
import top.tinn.miaosha.service.GoodsService;
import top.tinn.miaosha.service.MiaoshaService;
import top.tinn.miaosha.service.OrderService;
import top.tinn.miaosha.vo.GoodsVo;

/**
 * @ClassName MiaoshaServiceImpl
 * @Description
 * @Author Tintinnabu
 * @Date 2020/5/14 14:58
 */
@Service
public class MiaoshaServiceImpl implements MiaoshaService {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Override
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        //减库存 下订单 写入秒杀订单
        goodsService.reduceStock(goods);
        //order_info maiosha_order
        return orderService.createOrder(user, goods);
    }
}
