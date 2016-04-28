package id.co.viva.news.app.object;

public class ScreenUpdate {
    public String screen_name = "";
    public String update_date = "";

    public ScreenUpdate() {}

    public ScreenUpdate(String screen_name, String update_date) {
        this.screen_name = screen_name;
        this.update_date = update_date;
    }
}
