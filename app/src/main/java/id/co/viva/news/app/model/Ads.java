package id.co.viva.news.app.model;

/**
 * Created by reza on 05/05/15.
 */
public class Ads {

    private String mScreenName;
    private int mType;
    private int mPosition;
    private String mUnitId;

    public String getmScreenName() {
        return mScreenName;
    }

    public int getmType() {
        return mType;
    }

    public int getmPosition() {
        return mPosition;
    }

    public String getmUnitId() {
        return mUnitId;
    }

    public Ads(String screen_name, int type, int position, String unit_id) {
        mScreenName = screen_name;
        mType = type;
        mPosition = position;
        mUnitId = unit_id;
    }

}
