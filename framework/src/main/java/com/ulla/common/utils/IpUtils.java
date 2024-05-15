package com.ulla.common.utils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.alibaba.cloud.commons.lang.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description IpUtils
 * @author zhuyongdong
 * @since 2022-12-30 21:24:33
 */
@Slf4j
public class IpUtils {

    /**
     * 获取本机IP
     *
     * @return ip
     */
    public static String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("获取本机IP错误", e);
            return null;
        }
    }

    /**
     * 获取客户端IP地址
     *
     * @param request
     *            请求
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "106.124.130.167";
        }
        return ip;
    }

    /**
     * 获取客户端IP地址, 不判断是否代理
     *
     * @return
     */
    public static String getIpAddressWithoutCheck() {
        return UserUtil.getIp();
    }

    /**
     * 获取ip属地
     *
     * @param ip
     *            ip
     * @return String String
     * @throws Exception
     *             Exception
     */
    public static String getCityInfo(String ip) throws Exception {
        // 获得文件流时，因为读取的文件是在打好jar文件里面，不能直接通过文件资源路径拿到文件，但是可以在jar包中拿到文件流
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("ip2region.db");
        Resource resource = resources[0];
        InputStream is = resource.getInputStream();
        File target = new File("ip2region.db");
        FileUtils.copyInputStreamToFile(is, target);
        is.close();
        if (com.alibaba.cloud.commons.lang.StringUtils.isEmpty(String.valueOf(target))) {
            log.error("Error: Invalid ip2region.db file");
            return null;
        }
        DbConfig config = new DbConfig();
        DbSearcher searcher = new DbSearcher(config, String.valueOf(target));
        // 查询算法
        // B-tree, B树搜索（更快）
        int algorithm = DbSearcher.BTREE_ALGORITHM;
        try {
            // define the method
            Method method;
            method = searcher.getClass().getMethod("btreeSearch", String.class);
            DataBlock dataBlock;
            if (!Util.isIpAddress(ip)) {
                log.error("Error: Invalid ip address");
            }
            dataBlock = (DataBlock)method.invoke(searcher, ip);
            String ipInfo = dataBlock.getRegion();
            if (!com.alibaba.cloud.commons.lang.StringUtils.isEmpty(ipInfo)) {
                ipInfo = ipInfo.replace("|0", "");
                ipInfo = ipInfo.replace("0|", "");
            }
            return ipInfo;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 获取详细地址*
     *
     * @param ip
     *            ip
     * @return String String
     * @throws Exception
     */
    public static String getIpPossession(String ip) throws Exception {
        String cityInfo = getCityInfo(ip);
        if (!StringUtils.isEmpty(cityInfo)) {
            cityInfo = cityInfo.replace("|", " ");
            String[] cityList = cityInfo.split(" ");
            if (cityList.length > 0) {
                // 国内的显示到具体的省
                if ("中国".equals(cityList[0])) {
                    if (cityList.length > 1) {
                        return cityList[1];
                    }
                }
                // 国外显示到国家
                return cityList[0];
            }
        }
        return "未知";
    }

    // public static void main(String[] args) {
    // System.out.println(IpUtils.getLocalIp());
    // }
}
