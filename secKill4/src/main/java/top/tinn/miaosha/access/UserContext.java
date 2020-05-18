package top.tinn.miaosha.access;

import top.tinn.miaosha.domain.MiaoshaUser;

/**
 * @ClassName UserContext
 * @Description
 * @Author Tintinnabu
 * @Date 2020/5/15 20:35
 */
public class UserContext {

    private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<MiaoshaUser>();

    public static void setUser(MiaoshaUser user) {
        userHolder.set(user);
    }

    public static MiaoshaUser getUser() {
        return userHolder.get();
    }
}
