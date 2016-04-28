package id.co.viva.news.app.model;

/**
 * Created by reza on 06/07/15.
 */
public class ChannelURLMap {

    private String url;
    private String name;
    private String channel_id;
    private String kanal;
    private String level;

    public ChannelURLMap(String url, String name, String channel_id, String kanal, String level) {
        this.url = url;
        this.name = name;
        this.channel_id = channel_id;
        this.kanal = kanal;
        this.level = level;
    }

    public String getLevel() {
        return level;
    }

    public String getKanal() {
        return kanal;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getChannel_id() {
        return channel_id;
    }

}
