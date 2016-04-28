package id.co.viva.news.app.object;

public class ItemView {
    public int type;
    public int int_1;
    public String string_1 = "";
    public String string_2 = "";
    public String string_3 = "";
    public String string_4 = "";
    public String string_5 = "";
    public String string_6 = "";

    public ItemView() {}

    // TYPE_HEADER (int type, String title)
    // TYPE_STATE (int type, String message)
    public ItemView(int type, String string_1) {
        this.type = type;
        this.string_1 = string_1;
    }

    // TYPE_ITEM_TEXT (int type, String id, String title)
    public ItemView(int type, String string_1, String string_2) {
        this.type = type;
        this.string_1 = string_1;
        this.string_2 = string_2;
    }

    // TYPE_PHOTO_DETAIL (int type, String title, String publish_date, String story)
    // TYPE_PHOTO_PREVIEW_SMALL (int type, String photo_id, String title, String image_url)
    public ItemView(int type, String string_1, String string_2, String string_3) {
        this.type = type;
        this.string_1 = string_1;
        this.string_2 = string_2;
        this.string_3 = string_3;
    }

    //TYPE_VIDEO_PREVIEW (int type, String video_id, String title, String story, String image_url, String logo_url, String publish_date)
    public ItemView(int type, String string_1, String string_2, String string_3, String string_4, String string_5, String string_6) {
        this.type = type;
        this.string_1 = string_1;
        this.string_2 = string_2;
        this.string_3 = string_3;
        this.string_4 = string_4;
        this.string_5 = string_5;
        this.string_6 = string_6;
    }

    //TYPE_PHOTO_PREVIEW (int type, String video_id, String title, String story, String image_url, String logo_url, String publish_date, int num)
    public ItemView(int type, String string_1, String string_2, String string_3, String string_4, String string_5, String string_6, int int_1) {
        this.type = type;
        this.string_1 = string_1;
        this.string_2 = string_2;
        this.string_3 = string_3;
        this.string_4 = string_4;
        this.string_5 = string_5;
        this.string_6 = string_6;
        this.int_1 = int_1;
    }
}