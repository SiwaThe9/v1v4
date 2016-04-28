package id.co.viva.news.app.model;

/**
 * Created by reza on 30/06/15.
 */
public class EntityMain {

    private String id;
    private String title;
    private String kanal;
    private String url;
    private String image_url;
    private String date_publish;
    private String timeStamp;

    public EntityMain(String id, String title, String kanal,
                      String url, String image_url, String date_publish, String timeStamp) {
        this.id = id;
        this.title = title;
        this.kanal = kanal;
        this.url = url;
        this.image_url = image_url;
        this.date_publish = date_publish;
        this.timeStamp = timeStamp;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate_publish() {
        return date_publish;
    }

    public void setDate_publish(String date_publish) {
        this.date_publish = date_publish;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

}
