package id.co.viva.news.app.model;

/**
 * Created by reza on 23/10/14.
 */
public class ChannelList {

    private String id;
    private String title;
    private String kanal;
    private String image_url;
    private String date_publish;
    private String url;
    private String timestamp;

    public String getTimestamp() {
        return timestamp;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ChannelList(String id, String title, String kanal,
                       String image_url, String date_publish,
                       String url, String timestamp) {
        this.id = id;
        this.title = title;
        this.kanal = kanal;
        this.image_url = image_url;
        this.date_publish = date_publish;
        this.url = url;
        this.timestamp = timestamp;
    }

}
