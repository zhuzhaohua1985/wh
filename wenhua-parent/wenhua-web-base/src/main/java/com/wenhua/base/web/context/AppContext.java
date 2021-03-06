package com.wenhua.base.web.context;

import com.google.common.collect.Maps;
import com.wenhua.base.web.security.UserExt;
import com.wenhua.util.base.UserAgent;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.util.StringUtils.hasText;

/**
 * 应用的上下文对象。
 * 
 * @author Fuchun
 * @since 1.0
 * @version $Id: AppContext.java 30410 2013-05-28 01:11:32Z C629 $
 */
public final class AppContext implements Serializable {

    private static final long serialVersionUID = 8866916596410260904L;

    private static final String KEY_OF_CLIENT_IP = "AppContext.clientIp";

    private static final String KEY_OF_ACCOUNT_ID = "AppContext.accountId";

    private static final String KEY_OF_USER_AGENT = "AppContext.userAgent";

    private static final ThreadLocal<AppContext> contexts = new ThreadLocal<AppContext>();

    private final Map<String, Object> attrs;

    private AppContext() {
        attrs = Maps.newConcurrentMap();
    }

    /**
     * 初始化并返回应用上下文对象。如果上下文对象不存在，则自动创建。
     */
    public static AppContext initAppContext() {
        AppContext ctx = contexts.get();
        if (ctx == null) {
            ctx = new AppContext();
            contexts.set(ctx);
        }
        return ctx;
    }

    /**
     * 返回应用上下文对象。如果不存在，则返回 {@code null}。
     */
    public static AppContext getContext() {
        return contexts.get();
    }

    /**
     * 设置应用当前上下文对象。如果 {@code ctx == null}，则移除当前上下文对象。
     * 
     * @param ctx 要设置的上下文对象。
     */
    public static void setContext(AppContext ctx) {
        if (ctx == null) {
            contexts.remove();
        } else {
            contexts.set(ctx);
        }
    }

    /**
     * 重置应用当前上下文对象。
     */
    public static void reset() {
        AppContext ctx = contexts.get();
        if (ctx != null) {
            ctx.attrs.clear();
        }
        contexts.remove();
    }

    /**
     * 返回应用当前上下文中的客户端IP地址。如果当前上下文为 {@code null}，或者没设置客户端IP地址，则返回 {@code null}。
     */
    public static String getClientIp() {
        AppContext ctx = getContext();
        if (ctx == null) {
            return null;
        }
        String clientIp = (String) ctx.attrs.get(KEY_OF_CLIENT_IP);
        return clientIp;
    }

    /**
     * 设置应当前上下文中的客户端IP地址。
     * 
     * @param clientIp 客户端IP地址。
     */
    public static void setClientIp(String clientIp) {
        AppContext ctx = getContext();
        if (ctx == null) {
            return;
        }
        ctx.attrs.put(KEY_OF_CLIENT_IP, clientIp);
    }

    /**
     * 返回应用当前上下文中的帐号Id。
     */
    public static String getAccountId() {
        AppContext ctx = getContext();
        if (ctx == null) {
            return null;
        }
        String accountId = (String) ctx.attrs.get(KEY_OF_ACCOUNT_ID);
        return accountId;
    }

    /**
     * 设置应用当前上下文中的帐号Id。
     * 
     * @param accountId 应用当前上下文中的帐号Id。
     */
    public static void setAccountId(String accountId) {
        AppContext ctx = getContext();
        if (ctx == null) {
            return;
        }
        ctx.attrs.put(KEY_OF_ACCOUNT_ID, accountId);
    }

    /**
     * 返回应用当前上下文中的 {@code UserAgent}。
     */
    public static UserAgent getUserAgent() {
        AppContext ctx = getContext();
        if (ctx == null) {
            return null;
        }
        UserAgent ua = (UserAgent) ctx.attrs.get(KEY_OF_USER_AGENT);
        return ua;
    }

    /**
     * 设置应用当前上下文中的 {@code UserAgent}。
     * 
     * @param ua 应用当前上下文中的 {@code UserAgent}。
     */
    public static void setUserAgent(UserAgent ua) {
        AppContext ctx = getContext();
        if (ctx == null) {
            return;
        }
        ctx.attrs.put(KEY_OF_USER_AGENT, ua);
    }

    public static UserDetails getUserDetails() {
        return getUserExt();
    }

    public static UserExt getUserExt() {
        Authentication authToken = SecurityContextHolder.getContext().getAuthentication();
        if (authToken == null) {
            return null;
        }
        UserExt userExt = null;
        if (authToken.getPrincipal() instanceof UserExt) {
            userExt = (UserExt) authToken.getPrincipal();
        }
        return userExt;
    }

    /**
     * 返回应用当前上下文中的指定键的值。
     * 
     * @param key 指定键。
     * @return 返回应用当前上下文中的指定键的值。
     */
    public Object getIfAbsent(String key) {
        if (!hasText(key)) {
            return null;
        }
        Object val = attrs.get(key);
        return val;
    }

    /**
     * 给应用当前上下文添加属性值。如果 {@code key != null && val == null}，则删除指定键的值。
     * 
     * @param key 指定键。
     * @param val 指定的值。
     * @return 返回指定键关联的旧值，如果旧值不存在，则返回 {@code null}。
     * @throws IllegalArgumentException 如果 {@code key == null || key.trim().length() == 0}
     *             ，抛出此异常。
     */
    public Object put(String key, Object val) {
        checkArgument(hasText(key), "the key must not be null or empty.");
        if (val == null) {
            return attrs.remove(key);
        }
        return attrs.put(key, val);
    }
}
