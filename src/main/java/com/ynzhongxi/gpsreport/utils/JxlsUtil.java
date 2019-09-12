package com.ynzhongxi.gpsreport.utils;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.poi.util.IOUtils;
import org.jxls.common.Context;
import org.jxls.expression.JexlExpressionEvaluator;
import org.jxls.transform.Transformer;
import org.jxls.transform.poi.PoiTransformer;
import org.jxls.util.JxlsHelper;
/**
 * 描述:
 * <p>
 * 报表导出工具类
 *
 * @author liqiyun
 * @version 1.0
 * @date 18/11/29
 */
public class JxlsUtil {
    private static final JxlsUtil me = new JxlsUtil();

    private JxlsUtil() {
    }

    /**
     * 日期格式化
     *
     * @param date
     * @param fmt
     * @return
     */
    public String dateFmt(Date date, String fmt) {
        if (date == null) {
            return null;
        }

        try {
            SimpleDateFormat dateFmt = new SimpleDateFormat(fmt);

            return dateFmt.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Method description
     *
     * @param xls
     * @param out
     * @param model
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void exportExcel(File xls, File out, Map<String, Object> model)
            throws FileNotFoundException, IOException {
        exportExcel(new FileInputStream(xls), new FileOutputStream(out), model);
    }

    /**
     * Method description
     * @param is
     * @param os
     * @param model
     * @throws IOException
     */
    public static void exportExcel(InputStream is, OutputStream os, Map<String, Object> model) throws IOException {
        Context context = PoiTransformer.createInitialContext();

        if (model != null) {
            for (String key : model.keySet()) {
                context.putVar(key, model.get(key));
            }
        }

        JxlsHelper jxlsHelper = JxlsHelper.getInstance();
        Transformer transformer = jxlsHelper.createTransformer(is, os);

        // 获得配置
        JexlExpressionEvaluator evaluator = (JexlExpressionEvaluator) transformer.getTransformationConfig()
                .getExpressionEvaluator();

        // 设置静默模式，不报警告
        // evaluator.getJexlEngine().setSilent(true);
        // 函数强制，自定义功能
        Map<String, Object> funcs = new HashMap<String, Object>();

        // 添加自定义功能
        funcs.put("utils", new JxlsUtil());
        evaluator.getJexlEngine().setFunctions(funcs);

        // 必须要这个，否者表格函数统计会错乱
        jxlsHelper.setUseFastFormulaProcessor(false).processTemplate(context, transformer);
    }

    /**
     * Method description
     *
     * @param templatePath
     * @param os
     * @param model
     * @throws Exception
     */
    public static void exportExcel(String templatePath, OutputStream os, Map<String, Object> model) throws Exception {
        File template = getTemplate(templatePath);

        if (template != null) {
            exportExcel(new FileInputStream(template), os, model);
        } else {
            throw new Exception("Excel 模板未找到。");
        }
    }



    /**
     * if判断
     *
     * @param b
     * @param o1
     * @param o2
     * @return
     */
    public Object ifelse(boolean b, Object o1, Object o2) {
        return b
                ? o1
                : o2;
    }

    /**
     * 获取工具类实例
     *
     * @return
     */
    public static JxlsUtil me() {
        return me;
    }

    /**
     * 如果数组里包含有{@code null}的元素, 则抛出异常. 注意: 若数组本身为{@code null}则不会进行处理, 直接返回false
     *
     * @param array 要进行检查的数组
     * @param <T>
     * @return
     * @date 18/11/29
     */
    public <T> boolean noNullElements(T[] array) {
        if (array != null) {
            for (T element : array) {
                if (element == null) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 如果集合为{@code null},或者不包含任何元素,则返回false
     *
     * @param collection 要进行检查的集合
     * @return
     * @date 18/11/29
     */
    public boolean notEmpty(Collection<?> collection) {
        return !((collection == null) || collection.isEmpty());
    }

    /**
     * 如果键值对为{@code null},或者不包含任何键值,则返回false
     *
     * @param map 要进行检查的键值对
     * @return
     * @date 18/11/29
     */
    public boolean notEmpty(Map<?, ?> map) {
        return !((map == null) || map.isEmpty());
    }

    /**
     * 如果数组为{@code null}或长度为0, 则返回false
     *
     * @param array 要进行检查的数组
     * @param <T>
     * @return
     * @date 18/11/29
     */
    public <T> boolean notEmpty(T[] array) {
        return !((array == null) || (array.length == 0));
    }

    /**
     * 数字格式化
     *
     * @param number
     * @param format
     * @return
     */
    public String numFmt(Number number, String format) {
        DecimalFormat dFormat = new DecimalFormat(format);

        return dFormat.format(number);
    }

    /**
     * 判断路径是否是绝对路径
     *
     * @param path
     * @return
     */
    public boolean isAbsolutePath(String path) {
        return (path.startsWith("/") || path.contains(":"));
    }

    /**
     * 获取集合中的元素
     *
     * @param index
     * @param list
     * @return
     */
    public Object get(int index, List<?> list) {
        if (notEmpty(list)) {
            return list.get(index);
        }

        return null;
    }

    /**
     * 获取集合中的元素
     *
     * @param index
     * @param array
     * @param <T>
     * @return
     */
    public <T> T get(int index, T[] array) {
        if (notEmpty(array)) {
            return array[index];
        }

        return null;
    }

    /**
     * 获取集合中的元素
     *
     * @param key
     * @param map
     * @return
     */
    public Object get(Object key, Map<?, ?> map) {
        if (notEmpty(map)) {
            return map.get(key);
        }

        return null;
    }

    /**
     * 将图片转成数据
     *
     * @param path 图片绝对路径
     * @return
     * @throws IOException
     */
    public byte[] getImageData(String path) throws IOException {
        try (InputStream ins = new FileInputStream(path)) {
            return IOUtils.toByteArray(ins);
        }
    }

    /**
     * 获取图片后缀
     *
     * @param name 图片路径或名称
     * @return
     */
    public String getImageType(String name) {
        int index = name.lastIndexOf(".");

        if (index > 0) {
            return name.substring(index + 1);
        }

        return null;
    }
    /**
     * 返回第一个不为空的对象
     *
     * @param objs
     * @return
     */
    public Object getNotNull(Object... objs) {
        for (Object o : objs) {
            if (o != null) {
                return o;
            }
        }

        return null;
    }

    // 获取jxls模版文件

    /**
     * Method description
     *
     * @param path
     * @return
     * @date 18/11/29
     */
    public static File getTemplate(String path) {
        File template = new File(path);

        if (template.exists()) {
            return template;
        }

        return null;
    }

    /**
     * 如果字符串为{@code null}、空字符串或仅包含空白字符, 则返回false
     *
     * @param text 要进行检查的字符串
     * @return
     * @date 18/11/29
     */
    public boolean hasText(String text) {
        return !((text == null) || (text.length() == 0));
    }
}
