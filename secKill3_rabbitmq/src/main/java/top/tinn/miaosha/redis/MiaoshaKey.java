package top.tinn.miaosha.redis;

/**
 * @ClassName MiaoshaKey
 * @Description
 * @Author Tintinnabu
 * @Date 2020/5/15 15:03
 */
public class MiaoshaKey extends BasePrefix {

    private MiaoshaKey(String prefix) {
        super(prefix);
    }

    public static MiaoshaKey isGoodsOver = new MiaoshaKey("go");

}
