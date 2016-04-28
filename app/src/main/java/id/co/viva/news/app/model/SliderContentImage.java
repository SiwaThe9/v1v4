package id.co.viva.news.app.model;

/**
 * Created by reza on 05/01/15.
 */
public class SliderContentImage {

    private String imgUrl;
    private String title;

    public String getTitle() {
        return title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public SliderContentImage(String imgUrl, String title) {
        this.imgUrl = imgUrl;
        this.title = title;
    }

}
