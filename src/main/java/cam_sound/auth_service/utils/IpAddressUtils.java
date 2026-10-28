package cam_sound.auth_service.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import jakarta.servlet.http.HttpServletRequest;

public final class IpAddressUtils {

    private IpAddressUtils() {
    }

    public static String getClientIp(
            HttpServletRequest request) {

        String ip = getHeader(
                request,
                "CF-Connecting-IP");

        if (isValidIp(ip)) {
            return normalizeIp(ip);
        }

        ip = getHeader(
                request,
                "True-Client-IP");

        if (isValidIp(ip)) {
            return normalizeIp(ip);
        }

        ip = getHeader(
                request,
                "X-Forwarded-For");

        if (isValidIp(ip)) {

            String[] ips = ip.split(",");

            for (String forwardedIp : ips) {

                String candidate = forwardedIp.trim();

                if (isValidIp(candidate)) {
                    return normalizeIp(candidate);
                }
            }
        }

        ip = getHeader(
                request,
                "X-Real-IP");

        if (isValidIp(ip)) {
            return normalizeIp(ip);
        }

        // 5. Other proxy headers
        String[] proxyHeaders = {

                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED"
        };

        for (String header : proxyHeaders) {

            ip = getHeader(request, header);

            if (!isValidIp(ip)) {
                continue;
            }

            String[] ips = ip.split(",");

            for (String candidate : ips) {

                candidate = candidate.trim();

                if (isValidIp(candidate)) {
                    return normalizeIp(candidate);
                }
            }
        }

        // 6. Direct connection
        return normalizeIp(
                request.getRemoteAddr());
    }

    private static String getHeader(
            HttpServletRequest request,
            String header) {

        String value = request.getHeader(header);

        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.isEmpty() ||
                "unknown".equalsIgnoreCase(value)) {

            return null;
        }

        return value;
    }

    private static boolean isValidIp(
            String ip) {

        if (ip == null ||
                ip.isBlank() ||
                "unknown".equalsIgnoreCase(ip)) {

            return false;
        }

        ip = ip.trim();

        ip = ip.replace("\"", "");

        // Remove IPv4/IPv6 port notation
        if (ip.startsWith("[") &&
                ip.contains("]")) {

            ip = ip.substring(
                    1,
                    ip.indexOf("]"));
        }

        try {

            InetAddress.getByName(ip);

            return true;

        } catch (UnknownHostException e) {

            return false;
        }
    }

    private static String normalizeIp(
            String ip) {

        if (ip == null) {
            return null;
        }

        ip = ip.trim()
                .replace("\"", "");

        if (ip.startsWith("[") &&
                ip.contains("]")) {

            ip = ip.substring(
                    1,
                    ip.indexOf("]"));
        }

        try {
            InetAddress address = InetAddress.getByName(ip);

            return address.getHostAddress();

        } catch (UnknownHostException e) {
            return ip;
        }
    }
}
