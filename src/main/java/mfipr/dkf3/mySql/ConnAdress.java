package mfipr.dkf3.mySql;

public class ConnAdress extends ConnUSR {
    private static final String URL = "jdbc:mysql://...";
    private static final String SLL = "?useSSL=true";
    private static final String ENCODE = "&characterEncoding=utf8";

    public static String getURL() {
        return URL;
    }

    public static String getSLL() {
        return SLL;
    }

    public static String getENCODE() {
        return ENCODE;
    }

    public static String getAdress(String DB) {
        return getURL() + DB + getSLL() + getENCODE();
    }
}
