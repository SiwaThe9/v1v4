package id.co.viva.news.app.model;

/**
 * Created by reza on 23/02/15.
 */
public class BeritaSekitar {

    private String id;
    private String kanal;
    private String image_url;
    private String title;
    private String url;
    private String date_publish;

    public BeritaSekitar(String id, String kanal, String image_url,
                         String title, String url, String date_publish) {
        this.id = id;
        this.title = title;
        this.kanal = kanal;
        this.image_url = image_url;
        this.url = url;
        this.date_publish = date_publish;
    }

    public String getDate_publish() {
        return date_publish;
    }

    public void setDate_publish(String date_publish) {
        this.date_publish = date_publish;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
