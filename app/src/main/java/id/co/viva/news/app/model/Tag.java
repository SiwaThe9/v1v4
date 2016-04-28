package id.co.viva.news.app.model;

/**
 * Created by reza on 08/07/15.
 */
public class Tag {

    private String url;
    private String key;

    public String getKey() {
        return key;
    }

    public String getUrl() {
        return url;
    }

    public Tag(String url, String key) {
        this.url = url;
        this.key = key;
    }

}
