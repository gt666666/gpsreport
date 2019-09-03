package com.ynzhongxi.gpsreport.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则匹配工具类
 *
 * @author lixingwu
 */
public class PatternUtils {

    /**
     * 匹配script脚本对，启用不区分大小写的匹配
     */
    public static Pattern matchScripts = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);

    /**
     * 匹配script脚本右边的，启用不区分大小写的匹配
     */
    public static Pattern matchScriptEnd = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);

    /**
     * 匹配script脚本左边的，防止在script里写了类型，匹配任意字符
     */
    public static Pattern matchScriptBegin = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    /**
     * 匹配eval
     */
    public static Pattern matchEval = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    /**
     * 匹配javascript:，不区分大小写
     */
    public static Pattern matchJavascript = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);

    /**
     * 匹配vbscript:，不区分大小写
     */
    public static Pattern matchVbscript = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);

    /**
     * 匹配onload:，不区分大小写
     */
    public static Pattern matchOnload = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    /**
     * 匹配 src='xxx' ，不区分大小写
     */
    public static Pattern matchSrcSingle = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    /**
     * 匹配 src="xxx" ，不区分大小写
     */
    public static Pattern matchSrcDouble = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    /**
     * 移动号段正则表达式
     */
    public static Pattern matchYdMobileNO = Pattern.compile("^((13[4-9])|(147)|(15[0-2,7-9])|(178)|(18[2-4,7-8]))\\d{8}|(1705)\\d{7}$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    /**
     * 联通号段正则表达式
     */
    public static Pattern matchLtMobileNO = Pattern.compile("^((13[0-2])|(145)|(15[5-6])|(176)|(18[5,6]))\\d{8}|(1709)\\d{7}$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    /**
     * 电信号段正则表达式
     */
    public static Pattern matchYDxMobileNO = Pattern.compile("^((133)|(153)|(177)|(18[0,1,9])|(149)|(199))\\d{8}$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    /**
     * 虚拟运营商正则表达式
     */
    public static Pattern matchXnMobileNO = Pattern.compile("^((170))\\d{8}|(1718)|(1719)\\d{7}$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);


    /**
     * 方法描述：验证手机号是否合法.
     * 创建时间：2018-11-14 13:16:46
     *
     * @param mobile 手机号码
     * @return the boolean
     * @author "lixingwu"
     */
    private static boolean isMobileNO(String mobile) {
        mobile = mobile.trim();
        if (mobile.length() != 11) {
            return false;
        } else {
            Matcher matchYd = matchYdMobileNO.matcher(mobile);
            Matcher matchLt = matchLtMobileNO.matcher(mobile);
            Matcher matchDx = matchYDxMobileNO.matcher(mobile);
            Matcher matchXn = matchXnMobileNO.matcher(mobile);
            return matchYd.matches() || matchLt.matches() || matchDx.matches() || matchXn.matches();
        }
    }

    /**
     * 方法描述：验证多个手机号是否合法.
     * 创建时间：2018-11-14 13:16:46
     *
     * @param mobiles 多个手机号码用逗号隔开
     * @return the boolean
     * @author "lixingwu"
     */
    public static String isMobileNOs(String mobiles) {
        mobiles = mobiles.trim();
        String[] mobileArr = StringUtils.split(mobiles, ",");
        StringBuilder sb = new StringBuilder();
        for (String mobile : mobileArr) {
            if (!isMobileNO(mobile)) {
                sb.append(mobile).append(" ");
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(isMobileNOs("11288447113"));
    }

}

