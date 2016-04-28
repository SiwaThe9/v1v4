package id.co.viva.news.app;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by rezarachman on 02/10/14.
 */
public class Constant {
    // Standard Status
    public static final int STATUS_FAILED = 0;
    public static final int STATUS_OK = 1;

    // Standard Action
    public static final String ACTION_BROWSE = "browse";
    public static final String ACTION_OPEN = "open";
    public static final String ACTION_CONTENT = "content";
//    public static final String ACTION_LIST_PHOTO = "list-photo";
//    public static final String ACTION_LIST_VIDEO = "list-video";
//    public static final String ACTION_WEB_CONTENT = "web-content";

    // Category
    public static final String CATEGORY_BERITA = "berita";
    public static final String CATEGORY_BOLA = "bola";
    public static final String CATEGORY_LIFE = "life";
    public static final String CATEGORY_OTOMOTIF = "otomotif";

    //Kind of Timeout
    public static final int TIME_OUT = 6000;
    public static final int TIME_OUT_LONG = 15000;
    public static final int TIME_OUT_REGISTRATION = 15000;


    //G+ Picture Size
    public static final int PROFILE_PIC_SIZE = 400;

    //Amount Visible List Items
    public final static int NUMBER_OF_TOP_LIST_ITEMS_BIG_CARD = 4;
    public final static int NUMBER_OF_TOP_LIST_ITEMS_SMALL_CARD = 3;

    //Facebook Image Url
    public final static String URL_FACEBOOK_PHOTO = "http://graph.facebook.com/";

    //Social Media Login Code
    public static final String CODE_VIVA = "10";
    public static final String CODE_FACEBOOK = "11";
    public static final String CODE_PATH = "12";
    public static final String CODE_G_PLUS = "13";

    //Search result label
    public static final String SEARCH_RESULT_LABEL = "Hasil Pencarian : ";
    public static final String SEARCH_RESULT_NUMBER_LABEL = "Total Berita ";
    public static final String numFound = "numFound";

    //Minimal time location update
    public static final int MIN_TIME_BW_UPDATES = 20000;

    //Separate list and grid
    public static String DYNAMIC_SIZE_GRID_TYPE = "grid_type";
    public static String DYNAMIC_SIZE_LIST_TYPE = "list_type";
    public static String DYNAMIC_SIZE_SLIDER_TYPE = "slider_type";

    //Label for support
    public static String SUPPORT_EMAIL = "mobile-developer@viva.co.id";
    public static String EMAIL_SCHEME = "mailto";

    //Data Location Tag
    public static final String TAG_LOCATION_NAME = "nama";
    public static final String TAG_LOCATION_PROVINCE_ID = "id_propinsi";
    public static final String TAG_LOCATION_KABUPATEN_ID = "id_kabupaten";

    //Tag adapter for province and city
    public static final String ADAPTER_PROVINCE = "adapter_province";
    public static final String ADAPTER_CITY = "adapter_city";

    //Tag for tutorial and coach-mark
    public static final String FIRST_INSTALL_PROFILE = "first_install_profile";
    public static final String FIRST_INSTALL_TUTORIAL = "first_install_tutorial";

    //Tag checking first installation
    public static final String MOVE_TUTORIAL = "move_tutorial";
    public static final String MOVE_APPLICATION = "move_application";

    //Menu list
    public static final String NEWS_AROUND_MENU = "Berita Sekitar";
    public static final String NEWS_FAVORITE_MENU = "Berita Favorit";
    public static final String SCAN_QR_CODE_MENU = "Pindai Kode QR";
    public static final String TAG_POPULAR = "Tag Terpopuler";
    public static final String INFO_MENU_SECTION = "INFO";
    public static final String CONTACT_MENU = "Kontak Kami";
    public static final String RATE_MENU = "Beri Peringkat";
    public static final String ABOUT_US_MENU = "Tentang Kami";

    //Preference States
    public static final String FAVORITES_LIST = "favorites_list";
    public static final String FAVORITES_LIST_SIZE = "favorites_list_size";
    public static final String LOGIN_STATES_EMAIL = "login_states_email";
    public static final String LOGIN_STATES_FULL_NAME = "login_states_full_name";
    public static final String LOGIN_STATES_URL_PHOTO = "login_states_url_photo";
    public static final String LOGIN_STATES_USER_SOCIAL_ID = "login_states_user_social_id";
    public static final String LOGIN_STATES_APP_ID = "login_states_app_id";
    public static final String LOGIN_STATES_IS_LOGIN = "isLogin";
    public static final String LOGIN_STATES_GENDER = "login_states_gender";
    public static final String LOGIN_STATES_CITY = "login_states_city";
    public static final String LOGIN_STATES_BIRTH_DATE = "login_states_birth_date";
    public static final String LOGIN_STATES_PROVINCE = "login_states_province";
    public static final String LOGIN_STATES_COUNTRY = "login_states_country";
    public static final String PREFS_ADS_NAME = "prefs_ads_name";
    public static final String ADS_LIST = "ads_list";

    //Separate some URL
    public static final String LINK_YOUTUBE = "www.youtube.com";
    public static final String LINK_VIDEO_VIVA = "video.viva.co.id";
    public static final String LINK_ARTICLE_VIVA = "viva.co.id/news/read";

    //GCM
    //public static final String GCM_SENDER_ID = "702339857576";
    //public static final String GCM_URL_BACKEND_SERVER = "http://api.vivall.tv/rest/gcmreg";
    public static final String GCM_URL_BACKEND_SERVER = "http://api.viva.co.id/v/211/gcmregister/";

    //App Tagging Log
    public static final String TAG = VivaApp.class.getSimpleName();
    public static final String TAG_GCM = "VIVA-GCM";

    //Base URL Production
    private static String BASE_URL_NEW = "http://api.viva.co.id/v/212/";

    //Main Content URL
    public static String MAIN_CONFIG = "http://api.vivaku.mobi/viva/mainconf.json";
//    public static String MAIN_CONFIG = BASE_URL_NEW + "mainconf/app/android";
    public static String TUTORIAL_IMAGES_URL = BASE_URL_NEW + "coach";
    public static String NEWS_AROUND_URL = BASE_URL_NEW + "find/";
    public static String NEWS_CHANNEL = BASE_URL_NEW + "kanal/";
    public static String NEWS_DETAIL = BASE_URL_NEW + "detail/";
    public static String NEWS_SEARCH = BASE_URL_NEW + "search/";
    public static String LOGIN = BASE_URL_NEW + "login";
    public static String REGISTER = BASE_URL_NEW + "register2";
    public static String NEWS_COMMENTS = BASE_URL_NEW + "sendcomment/";
    public static String NEWS_RATES = BASE_URL_NEW + "sendrate/";
    public static String NEWS_LIST_COMMENT = BASE_URL_NEW + "commentlist";
    public static String FORGOT_PASSWORD = BASE_URL_NEW + "forgotpass";
    public static final String NEW_GET_PROVINCE = BASE_URL_NEW + "location/";
    public static final String NEW_UPDATE_PROFILE = BASE_URL_NEW + "updateprofile";
    public static final String ALL_NEWS_URL = "/lv/1/s/0/type/terbaru";
    public static final String ALL_NEWS_URL_PAGING = "/lv/1/published/";
    public static final String SUB_CHANNEL_LV_2_URL = "/lv/2/s/0";
    public static final String SUB_CHANNEL_LV_1_URL = "/lv/1/s/0";
    public static final String SUB_CHANNEL_LV_2_URL_PAGING = "/lv/2/s/";
    public static final String SUB_CHANNEL_LV_1_URL_PAGING = "/lv/1/s/";

    // New URL
    //public static final String PHOTO_ITEMS_URL = "http://api.vivaku.mobi/viva/channel_photo_item.php";

    //Request Queue Tag
    public static final String JSON_REQUEST = "json_obj_req";

    //JSON Tag
    public static final String AllNews = "Semua Berita";
    public static final String response = "response";
    public static final String timestamp = "timestamp";
    public static final String headlines = "headlines";
    public static final String news = "news";
    public static final String comment_list = "comment_list";
    public static final String related_article = "related_article";
    public static final String detail = "detail";
    public static final String url = "url";
    public static final String search = "search";
    public static final String id = "id";
    public static final String title = "title";
    public static final String slug = "slug";
    public static final String level = "level";
    public static final String kanal = "kanal";
    public static final String image_url = "image_url";
    public static final String date_publish = "date_publish";
    public static final String source = "source";
    public static final String reporter_name = "reporter_name";
    public static final String content = "content";
    public static final String data = "data";

    //JSON Tag Ads
    public static final String adses = "adses";
    public static final String menus = "menus";
    public static final String maps = "maps";
    public static final String screen_name = "screen_name";
    public static final String screen = "screen";
    public static final String unit_id = "unit_id";
    public static final String type = "type";
    public static final String position = "position";
    public static final String hit_url = "hit_url";
    public static final String asset_url = "asset_url";
    public static final String color = "color";
    public static final String parent = "parent";
    public static final String afterzeroindex = "afterzeroindex";
    public static final String layout_list  = "layout-list";

    //Another from JSON
    public static final String name = "name";
    public static final String parent_id = "parent_id";
    public static final String comment_text = "comment_text";
    public static final String app_id = "app_id";
    public static final String submitted_date = "submitted_date";
    public static final String status = "status";
    public static final String image_caption = "image_caption";
    public static final String content_video = "content_video";
    public static final String text = "text";
    public static final String article_id = "article_id";
    public static final String related_article_id = "related_article_id";
    public static final String related_title = "related_title";
    public static final String related_channel_level_1_id = "related_channel_level_1_id";
    public static final String channel_id = "channel_id";
    public static final String related_date_publish = "related_date_publish";
    public static final String image = "image";
    public static final String content_images = "content_images";

    //Calculate time
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    //Bitmap scaling
    private static final float BITMAP_SCALE = 0.4f;
    private static final float BLUR_RADIUS = 7.5f;

    //AT Internet
    public static final String AT_LOG_DOMAIN = ".ati-host.net";
    public static final String AT_SUB_DOMAIN = "logw351";
    public static final String AT_SITE_ID = "551156";
    public static final String AT_SUB_SITE = "1";

    //Path Configuration
    public static final String PATH_REDIRECT = "http://viva.co.id";
    public static final String PATH_CLIENT_ID = "29eb3edd59992f2418946e028774d91004bd05a9";
    public static final String PATH_SECRET_ID = "e8b08b8d51b679a3a33de1089ec373d6f5a38419";
    public static final String PATH_AUTHENTICATE_URL = "https://partner.path.com/oauth2/authenticate";
    public static final String PATH_ACCESS_TOKEN_URL = "https://partner.path.com/oauth2/access_token";
    public static final String PATH_USER_INFO_URL = "https://partner.path.com/1/user/self";
    public static final String ATTRIBUTE_PATH_ACCESS_TOKEN = "access_token";
    public static final String ATTRIBUTE_PATH_USER_ID = "user_id";

    //Analytic Tagging
    public static final String BERITA_SEKITAR_PAGE = "BERITA_SEKITAR_HAL_";
    public static final String BERITA_SEKITAR_DETAIL_PAGE = "BERITA_SEKITAR_DETAIL_";
    public static final String SEARCH_RESULT_PAGE = "SEARCH_RESULT_PAGE_";
    public static final String FROM_SEARCH_RESULT_DETAIL_CONTENT = "FROM_SEARCH_RESULT_DETAIL_CONTENT_";
    public static final String FROM_RELATED_ARTICLE_DETAIL_CONTENT = "FROM_RELATED_ARTICLE_DETAIL_CONTENT_";
    public static final String FROM_EDITOR_CHOICE = "FROM_EDITOR_CHOICE_";
    public static final String FAVORITES_PAGE = "FAVORITES_PAGE";
    public static final String FAVORITES_PAGE_DETAIL = "FAVORITES_PAGE_DETAIL_";
    public static final String COMMENTED_ARTICLE = "COMMENTED_ARTICLE";
    public static final String RATING_ARTICLE = "RATING_ARTICLE";
    public static final String ARTICLE_FROM_NOTIFICATION = "DETAIL_ARTICLE";

    public static final String CHANNEL_INDEX_LIFE = "http://life.viva.co.id/";

    //Url search from mobile site
    public static String SEARCH_KEYWORD = "search.viva.co.id/search?q=";

    //Banner Interstitial Position
    public static final String ADS_TYPE_OPENING = "ads_opening";
    public static final String ADS_TYPE_CLOSING = "ads_closing";
    public static final int ADS_TYPE_INTERSTITIAL = 1;
    public static final int ADS_TYPE_BANNER = 2;
    public static final int ADS_POSITION_OPEN = 1;
    public static final int ADS_POSITION_CLOSE = 2;

    //Banner Position
    public static final int POSITION_BANNER_TOP = 1;
    public static final int POSITION_BANNER_BOTTOM = 2;

    //Additional parameter in detail articles
    public static final String berita_sekitar_detail_screen = "berita_sekitar_detail_screen";
    public static final String search_screen = "search_screen";

    //Type list
    public static final int SMALL_LIST_DEFAULT = 2;
    public static final int NEWS_AROUND_LIST = 3;
    public static final int BIG_CARD_CHANNEL_LIST = 4;
    public static final int BIG_CARD_SEARCH_RESULT = 7;
    public static final int TAG_POPULAR_RESULT_LIST = 5;

    public final static String CHANNEL_LIFE = "vivalife";
    public final static String CHANNEL_BOLA = "bola";
    public final static String CHANNEL_NEWS = "berita";
    public final static String CHANNEL_AUTO = "otomotif";

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blur(Context ctx, Bitmap image) {
        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);
        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);
        RenderScript rs = RenderScript.create(ctx);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "baru saja";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "satu menit yang lalu";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " menit yang lalu";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "satu jam yang lalu";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " jam yang lalu";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "kemarin";
        } else {
            return diff / DAY_MILLIS + " hari yang lalu";
        }
    }

    public static int getDynamicImageSize(Context mCtx, String mType) {
        WindowManager wm = (WindowManager) mCtx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = 0;
        if (mType.equals(Constant.DYNAMIC_SIZE_LIST_TYPE)) {
            int PADDING_DYNAMIC_SIZE = 200;
            width = size.x - PADDING_DYNAMIC_SIZE;
        } else if (mType.equals(Constant.DYNAMIC_SIZE_GRID_TYPE)) {
            int PADDING_DYNAMIC_SIZE_GRID = 500;
            width = size.x - PADDING_DYNAMIC_SIZE_GRID;
        } else if (mType.equals(DYNAMIC_SIZE_SLIDER_TYPE)) {
            int PADDING_DYNAMIC_SIZE_SLIDER = 800;
            width = size.x - PADDING_DYNAMIC_SIZE_SLIDER;
        }
        return width;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static String getArticleViva(String url) {
        String[] separated = url.split("/");
        String urlSplit;
        if (separated.length < 5) {
            urlSplit = separated[3];
        } else {
            urlSplit = separated[5];
        }
        String[] splitter = urlSplit.split("-");
        return splitter[0];
    }

    public static String getSearchKeyword(String url) {
        if (url != null) {
            if (url.length() > 0) {
                String[] separated = url.split("=");
                String keyword;
                keyword = separated[1];
                if (keyword.contains("+")) {
                    return keyword.replaceAll("\\+", " ");
                } else {
                    return keyword;
                }
            }
        }
        return null;
    }

}
