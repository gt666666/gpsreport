package com.ynzhongxi.gpsreport.component;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author lixingwu
 */
@Component
public class RedisUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 方法描述：自定义判断是否为空
     * 创建作者：李兴武
     * 创建日期：2017-06-22 19:50:01
     *
     * @param str the str
     * @return the boolean
     * @author "lixingwu"
     */
    public boolean isBlank(String str) {
        if (str != null) {
            str = str.replaceAll("\r\n|\n\r|\n|\r|\f|\t", "");
        }
        if (str == null) {
            return true;
        } else if ("".equals(str)) {
            return true;
        } else if ("null".equals(str)) {
            return true;
        } else if ("NULL".equals(str)) {
            return true;
        } else if ("(null)".equals(str)) {
            return true;
        } else if ("(NULL)".equals(str)) {
            return true;
        } else {
            return str.trim().length() == 0;
        }
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @author "lixingwu"
     */
    public void expire(String key, long time) {
        if (isBlank(key)) {
            throw new RuntimeException("指定缓存失效时间的key值不能为空");
        }
        if (time > 0) {
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键不能为空
     * @return 时间(秒) 返回
     * -1代表没有为键值设置过期时间，
     * -2表示已经过期或者，获取键值不存在，
     * 其他数值为键值的是剩余有效时间（秒）
     */
    public Long getExpire(String key) {
        if (isBlank(key)) {
            throw new RuntimeException("获取过期时间的key值不能为空");
        }
        // 获取过期时间，如果过期时间为null，就设置为-2
        Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (null == expire) {
            return -2L;
        }
        return expire;
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true存在，false不存在==过期
     */
    public Boolean hasKey(String key) {
        Boolean bool = redisTemplate.hasKey(key);
        if (null == bool) {
            return false;
        }
        return bool;
    }

    /**
     * 根据key删除
     *
     * @param key 可以传一个值或多个
     */
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                List<String> list = CollectionUtils.arrayToList(key);
                redisTemplate.delete(list);
            }
        }
    }

    /**
     * 根据key获取值
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 存储一个键值对
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 存储一个键值对，并设置有效时间（秒）
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 计数器，在key对应的值上指定数值
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return 返回累加后的结果
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 计数器，在key对应的值+1
     *
     * @param key 键
     * @return 返回累加后的结果
     */
    public long incr(String key) {
        return redisTemplate.opsForValue().increment(key, 1L);
    }

    /**
     * 计数器，在key对应的值减去指定数值
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return 返回执行后的结果
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * 计数器，在key对应的值+1
     *
     * @param key 键
     * @return 返回累加后的结果
     */
    public long decr(String key) {
        return redisTemplate.opsForValue().increment(key, -1L);
    }

    /**
     * 根据键值和存储的map对象的键值获取值
     *
     * @param key  键
     * @param item 项
     * @return 值
     */
    public Object hget(String key, String item) {
        if (null == key || null == item) {
            throw new RuntimeException("获取的key和key对应的map的key不能为null.");
        }
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 存储一个map对象
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 存储一个map对象，并设置过期时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建，并设置hash表的过期时间
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        if (null == key || null == item) {
            throw new RuntimeException("key和key对应的map的key不能为null.");
        }
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        if (null == key || null == item) {
            throw new RuntimeException("key和key对应的map的key不能为null.");
        }
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个（默认值为0）
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return 新增后的值返回
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减 如果不存在,就会创建一个（默认值为0）
     *
     * @param key  键
     * @param item 项
     * @param by   要减少几(小于0)
     * @return
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }


    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return key对应的set
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断一个value是否存在于指定key存储的set中
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 存储一个set
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存，并设置过期时间
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 根据key获取存储的set的长度
     *
     * @param key 键
     * @return 存储的set的长度
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 在key对应的set中，移除指定的value值
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取指定key对应的list
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return 指定位置的list
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取指定key对应的list
     *
     * @param key 键
     * @return key对应的list
     */
    public List<Object> lGet(String key) {
        try {
            return redisTemplate.opsForList().range(key, 0, -1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取指定key存储的list的长度
     *
     * @param key 键
     * @return list的长度
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引
     *              index>=0时，0 表头， 第二个元素，依次类推；
     *              index<0时，-1 表尾，-2 倒数第二个元素，依次类推：
     * @return 指定索引的对象
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 把一个对象存储到key对应的list中
     *
     * @param key   键
     * @param value 对象
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 把一个对象存储到key对应的list中，设置过期时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 把一个对象的list，追加到指定key的list中去
     *
     * @param key   键
     * @param value 对象list
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 把一个对象的list，追加到指定key的list中去，并设置过期时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 方法描述：左弹出一条数据.
     * 创建时间：2019-04-10 23:29:41
     * 创建作者：李兴武
     *
     * @param key 对应的key
     * @return 返回弹出的数据
     * @author "lixingwu"
     */
    public Object leftPop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}

