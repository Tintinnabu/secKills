package top.tinn.miaosha.service;

import top.tinn.miaosha.domain.MiaoshaUser;
import top.tinn.miaosha.domain.OrderInfo;
import top.tinn.miaosha.vo.GoodsVo;

/**
 * @InterfaceName MiaoshaService
 * @Description
 * @Author Tintinnabu
 * @Date 2020/5/14 14:54
 */
public interface MiaoshaService {

    OrderInfo miaosha(MiaoshaUser user, GoodsVo goods);
}
