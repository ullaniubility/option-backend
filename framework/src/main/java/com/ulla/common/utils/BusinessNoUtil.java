package com.ulla.common.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.SystemClock;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static com.ulla.constant.NumberConstant.EIGHT;

/**
 * @author 1
 */
@Slf4j
public class BusinessNoUtil {
    private static final long MAX_SEQUENCE = 999L;
    private static long lastSecond = -1L;
    private static final AtomicLong SEQUENCE = new AtomicLong();

    /**
     * 获取业务单号 系统后台自动生成格式为前缀加日期时分秒yyyyMMddHHmmss+3位流水号，每毫秒至多生成999条业务订单号
     *
     * @param prefix 前缀
     * @return 业务单号
     */
    public static synchronized String genBusinessNo(String prefix) {
        long now = SystemClock.now();
        if (lastSecond == now) {
            // 相同毫秒内，序列号自增
            SEQUENCE.incrementAndGet();
            if (SEQUENCE.get() > MAX_SEQUENCE) {
                // 同一毫秒的序列数已经达到最大强制进入下1毫秒
                now = tilNextMillis(lastSecond);
                SEQUENCE.set(1);
            }
        } else {
            // 不同秒内，序列号置为 1
            SEQUENCE.set(1);
        }
        lastSecond = now;
        return prefix + DateUtil.format(new Date(now), DatePattern.PURE_DATETIME_MS_FORMAT) + String.format("%03d", SEQUENCE.get());
    }

    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private static long timeGen() {
        return SystemClock.now();
    }

    public static String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    /**
     * 获取短的码
     *
     * @return
     */
    public static String generateShortUuid() {

        StringBuilder shortBuffer = new StringBuilder();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < EIGHT; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();
    }
}
