package id.co.viva.news.app.model;

/**
 * Created by reza on 21/10/14.
 */
public class RelatedArticle {

    private String id;
    private String article_id;
    private String related_article_id;
    private String related_title;
    private String related_channel_level_1_id;
    private String channel_id;
    private String related_date_publish;
    private String image;
    private String kanal;
    private String shared_url;

    public String getShared_url() {
        return shared_url;
    }

    public void setShared_url(String shared_url) {
        this.shared_url = shared_url;
    }

    public String getKanal() {
        return kanal;
    }

    public void setKanal(String kanal) {
        this.kanal = kanal;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArticle_id() {
        return article_id;
    }

    public void setArticle_id(String article_id) {
        this.article_id = article_id;
    }

    public String getRelated_article_id() {
        return related_article_id;
    }

    public void setRelated_article_id(String related_article_id) {
        this.related_article_id = related_article_id;
    }

    public String getRelated_title() {
        return related_title;
    }

    public void setRelated_title(String related_title) {
        this.related_title = related_title;
    }

    public String getRelated_channel_level_1_id() {
        return related_channel_level_1_id;
    }

    public void setRelated_channel_level_1_id(String related_channel_level_1_id) {
        this.related_channel_level_1_id = related_channel_level_1_id;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getRelated_date_publish() {
        return related_date_publish;
    }

    public void setRelated_date_publish(String related_date_publish) {
        this.related_date_publish = related_date_publish;
    }

    public RelatedArticle(String id, String article_id, String related_article_id, String related_title,
                          String related_channel_level_1_id, String channel_id, String related_date_publish,
                          String image, String kanal, String shared_url) {
        this.id = id;
        this.article_id = article_id;
        this.related_article_id = related_article_id;
        this.related_title = related_title;
        this.related_channel_level_1_id = related_channel_level_1_id;
        this.channel_id = channel_id;
        this.related_date_publish = related_date_publish;
        this.image = image;
        this.kanal = kanal;
        this.shared_url = shared_url;
    }

}
