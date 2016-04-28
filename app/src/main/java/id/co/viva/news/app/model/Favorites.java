package id.co.viva.news.app.model;

import java.util.ArrayList;

/**
 * Created by reza on 17/11/14.
 */
public class Favorites {

    private String id;
    private String title;
    private String channel_id;
    private String kanal;
    private String image_url;
    private String date_publish;
    private String reporter_name;
    private String url;
    private String content;
    private String image_caption;
    private ArrayList<SliderContentImage> sliderContentImages;

    public ArrayList<SliderContentImage> getSliderContentImages() {
        return sliderContentImages;
    }

    public String getImage_caption() {
        return image_caption;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getKanal() {
        return kanal;
    }

    public void setKanal(String kanal) {
        this.kanal = kanal;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDate_publish() {
        return date_publish;
    }

    public void setDate_publish(String date_publish) {
        this.date_publish = date_publish;
    }

    public String getReporter_name() {
        return reporter_name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Favorites (String id, String title, String channel_id, String kanal, String image_url,
                      String date_publish, String reporter_name, String url, String content, String image_caption,
                      ArrayList<SliderContentImage> sliderContentImages) {
        this.id = id;
        this.title = title;
        this.channel_id = channel_id;
        this.kanal = kanal;
        this.image_url = image_url;
        this.date_publish = date_publish;
        this.reporter_name = reporter_name;
        this.url = url;
        this.content = content;
        this.image_caption = image_caption;
        this.sliderContentImages = sliderContentImages;
    }

}
