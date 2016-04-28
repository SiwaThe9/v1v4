package id.co.viva.news.app.object;

public class Ads {
    public final static int TYPE_NONE = 0;
    public final static int TYPE_INTERSTITIAL = 1;
    public final static int TYPE_BANNER = 2;
    public final static int TYPE_VIDEO = 3;
    public final static int POSITION_NONE = 0;
    public final static int POSITION_INTERSTITIAL_OPEN = 1;
    public final static int POSITION_INTERSTITIAL_CLOSE = 2;
    public final static int POSITION_BANNER_TOP = 1;
    public final static int POSITION_BANNER_BOTTOM = 2;

    public int type = TYPE_NONE;
    public int position = POSITION_NONE;
    public String screen_name = "";
    public String unit_id = "";

    public Ads() {}
}
