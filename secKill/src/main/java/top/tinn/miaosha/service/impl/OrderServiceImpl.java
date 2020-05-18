package top.tinn.miaosha.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.tinn.miaosha.dao.OrderDao;
import top.tinn.miaosha.domain.MiaoshaOrder;
import top.tinn.miaosha.domain.MiaoshaUser;
import top.tinn.miaosha.domain.OrderInfo;
import top.tinn.miaosha.service.OrderService;
import top.tinn.miaosha.vo.GoodsVo;

import java.util.Date;

/**
 * @ClassName OrderServiceImpl
 * @Description
 * @Author Tintinnabu
 * @Date 2020/5/14 15:00
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;
    @Override
    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(
            long userId, long goodsId) {
        return orderDao.getMiaoshaOrderByUserIdGoodsId(
                userId, goodsId
        );
    }

    @Override
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());

        long orderId = orderDao.insert(orderInfo);

        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderId);
        miaoshaOrder.setUserId(user.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);
        return orderInfo;
    }
}
