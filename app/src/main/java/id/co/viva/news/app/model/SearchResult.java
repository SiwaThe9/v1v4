package id.co.viva.news.app.model;

/**
 * Created by reza on 16/10/14.
 */
public class SearchResult {

    private String id;
    private String kanal;
    private String image_url;
    private String title;
    private String slug;
    private String date_publish;
    private String url;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public SearchResult(String id, String kanal, String image_url,
                    String title, String slug, String date_publish, String url) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.kanal = kanal;
        this.image_url = image_url;
        this.date_publish = date_publish;
        this.url = url;
    }

}
