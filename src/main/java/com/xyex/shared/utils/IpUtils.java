package com.xyex.shared.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;


public class IpUtils {

    public static String getClientIp(String xForwardedFor) {
        if (xForwardedFor == null) {
            return "unknown";
        }

        if (!xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            String ip = xForwardedFor.split(",")[0].trim();
            return ip.replace("::ffff:", "");
        }
        return "unknown";
    }

    /**
     * 获取服务器IP地址
     * 优先获取非回环地址的IPv4地址
     */
    public static String getServerIp() {
        try {
            // 尝试获取所有网络接口
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                // 跳过回环接口和未启用的接口
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    // 获取IPv4地址且非回环地址
                    if (!address.isLoopbackAddress() && address.getHostAddress().indexOf(':') == -1) {
                        return address.getHostAddress();
                    }
                }
            }
            // 如果没有找到合适的地址，使用默认方法
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            // 异常情况下返回localhost
            return "unknown";
        }
    }
}