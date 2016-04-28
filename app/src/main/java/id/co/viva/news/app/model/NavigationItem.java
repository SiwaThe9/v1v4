package id.co.viva.news.app.model;

public class NavigationItem {
    public final static int TYPE_DEFAULT = 0;
    public final static String TYPE_SUB_CHANNEL_TITLE_TOP = "sub-channel-title-top";
    public final static String TYPE_SUB_CHANNEL_TITLE_MIDDLE = "sub-channel-title-middle";

    public final static String ACTION_LIST_NEWS = "list-news";
    public final static String ACTION_LIST_NEWS_BY_LOCATION = "list-news-by-location";
    public final static String ACTION_LIST_NEWS_BY_FAVORITE = "list-news-by-favorite";
    public final static String ACTION_QR_CODE_SCANNER = "qr-code-scanner";
    public final static String ACTION_LIST_TAG = "list-tag";
    public final static String ACTION_LIST_SUB_CHANNEL = "list-sub-channel";
    public final static String ACTION_GRID_SUB_CHANNEL = "grid-sub-channel";
    public final static String ACTION_LIST_PHOTO = "list-photo";
    public final static String ACTION_LIST_VIDEO = "list-video";
    public final static String ACTION_WEB_CONTENT = "web-content";
    public final static String ACTION_SEND_MAIL = "send-mail";
    public final static String ACTION_RATE_APP = "rate-app";
    public final static String ACTION_ABOUT = "about";

    public int type;
    public String name;
    public String title;
    public String icon_url;
    public String action;
    public String url;
    public String ab_color;
    public String json_key;
    public String card;
    public String email;
    public String subject;

    public NavigationItem() {}

}
