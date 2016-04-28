package id.co.viva.news.app.share;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;

import id.co.viva.news.app.object.Ads;
import id.co.viva.news.app.object.PhotoCategory;
import id.co.viva.news.app.object.Photo;
import id.co.viva.news.app.object.PhotoDetail;
import id.co.viva.news.app.object.PhotoItem;
import id.co.viva.news.app.object.ScreenUpdate;
import id.co.viva.news.app.object.SubChannel;
import id.co.viva.news.app.object.Video;
import id.co.viva.news.app.object.VideoCategory;

public class DB extends SQLiteOpenHelper {
    public static final String TAG = DB.class.getSimpleName();
    private static DB instance;

    public static DB getInstance(Context context) {
        if (instance == null)
            instance = new DB(context);
        return instance;
    }

    private static SQLiteDatabase db;

    private static final int DATABASE_VERSION = 11;
    private static final String DATABASE_NAME = "viva";

    private static final String TABLE_SCREEN_UPDATE = "table_screen_update";
    private static final String TABLE_ADS = "table_ads";
    private static final String INDEX_ADS = "index_ads";
    private static final String TABLE_SUB_CHANNEL = "table_sub_channel";
    private static final String INDEX_SUB_CHANNEL = "index_sub_channel";
    private static final String TABLE_PHOTO = "table_photo";
    private static final String TABLE_PHOTO_CATEGORY = "table_photo_category";
    private static final String INDEX_PHOTO_CATEGORY = "index_photo_category";
    private static final String TABLE_PHOTO_ITEM = "table_photo_item";
    private static final String TABLE_PHOTO_DETAIL = "table_photo_detail";
    private static final String INDEX_PHOTO_DETAIL = "index_photo_detail";
    private static final String TABLE_VIDEO = "table_video";
    private static final String TABLE_VIDEO_CATEGORY = "table_video_category";
    private static final String INDEX_VIDEO_CATEGORY = "index_video_category";

    private static final String FIELD_SCREEN_NAME = "screen_name";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_POSITION = "position";
    private static final String FIELD_UNIT_ID = "unit_id";
    private static final String FIELD_PHOTO_ID = "photo_id";
    private static final String FIELD_VIDEO_ID = "video_id";
    private static final String FIELD_PHOTO_ITEM_ID = "photo_item_id";
    private static final String FIELD_CHANNEL = "channel";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_STORY = "story";
    private static final String FIELD_API_URL = "api_url";
    private static final String FIELD_IMAGE_URL = "image_url";
    private static final String FIELD_LOGO_URL = "logo_url";
    private static final String FIELD_VIDEO_URL = "video_url";
    private static final String FIELD_ACTION = "action";
    private static final String FIELD_AB_COLOR = "ab_color";
    private static final String FIELD_NUM = "num";
    private static final String FIELD_SOURCE = "source";
    private static final String FIELD_PUBLISH_DATE = "publish_date";
    private static final String FIELD_CATEGORY = "category";
    private static final String FIELD_UPDATE_DATE = "update_date";
    private static final String FIELD_URL_CONTENT = "url_content";
    private static final String FIELD_HTML_CONTENT = "html_content";
    private static final String FIELD_TS = "ts";

    private static final String[] FIELDS_SCREEN_UPDATE = new String[] { FIELD_SCREEN_NAME, FIELD_UPDATE_DATE };
    private static final String[] FIELDS_ADS = new String[] { FIELD_SCREEN_NAME, FIELD_TYPE, FIELD_POSITION, FIELD_UNIT_ID };
    private static final String[] FIELDS_SUB_CHANNEL = new String[] { FIELD_NAME, FIELD_TITLE, FIELD_API_URL, FIELD_IMAGE_URL, FIELD_URL_CONTENT, FIELD_HTML_CONTENT, FIELD_ACTION, FIELD_AB_COLOR, FIELD_TYPE };
    private static final String[] FIELDS_PHOTO = new String[] {FIELD_PHOTO_ID, FIELD_TITLE, FIELD_STORY, FIELD_API_URL, FIELD_IMAGE_URL, FIELD_LOGO_URL, FIELD_NUM, FIELD_PUBLISH_DATE };
    private static final String[] FIELDS_PHOTO_CATEGORY = new String[] { FIELD_CATEGORY, FIELD_PHOTO_ID};
    private static final String[] FIELDS_PHOTO_ITEM = new String[] {FIELD_PHOTO_ITEM_ID, FIELD_DESCRIPTION, FIELD_IMAGE_URL, FIELD_SOURCE };
    private static final String[] FIELDS_PHOTO_DETAIL = new String[] { FIELD_PHOTO_ID, FIELD_PHOTO_ITEM_ID};
    private static final String[] FIELDS_VIDEO = new String[] {FIELD_VIDEO_ID, FIELD_TITLE, FIELD_STORY, FIELD_IMAGE_URL, FIELD_LOGO_URL, FIELD_VIDEO_URL, FIELD_PUBLISH_DATE };
    private static final String[] FIELDS_VIDEO_CATEGORY = new String[] { FIELD_CATEGORY, FIELD_VIDEO_ID};

    private static final String CREATE_TABLE_SCREEN_UPDATE = "CREATE TABLE " + TABLE_SCREEN_UPDATE
            + " (" + FIELD_SCREEN_NAME + " TEXT PRIMARY KEY"
            + ", " + FIELD_UPDATE_DATE + " TEXT"
            + ", " + FIELD_TS + " TEXT)";

    private static final String CREATE_TABLE_ADS = "CREATE TABLE " + TABLE_ADS
            + " (" + FIELD_SCREEN_NAME + " TEXT"
            + ", " + FIELD_TYPE + " INTEGER DEFAULT 0"
            + ", " + FIELD_POSITION + " INTEGER DEFAULT 0"
            + ", " + FIELD_UNIT_ID + " TEXT"
            + ", " + FIELD_TS + " TEXT)";

    private static final String CREATE_INDEX_ADS = "CREATE INDEX  " + INDEX_ADS
            + " ON " + TABLE_ADS
            + " (" + FIELD_SCREEN_NAME + ", " + FIELD_TYPE + ", " + FIELD_POSITION + ")";

    private static final String CREATE_TABLE_SUB_CHANNEL = "CREATE TABLE " + TABLE_SUB_CHANNEL
            + " (" + FIELD_NAME + " TEXT"
            + ", " + FIELD_TITLE + " TEXT"
            + ", " + FIELD_API_URL + " TEXT"
            + ", " + FIELD_IMAGE_URL + " TEXT"
            + ", " + FIELD_URL_CONTENT + " TEXT"
            + ", " + FIELD_HTML_CONTENT + " TEXT"
            + ", " + FIELD_ACTION + " TEXT"
            + ", " + FIELD_AB_COLOR + " TEXT"
            + ", " + FIELD_TYPE + " TEXT"
            + ", " + FIELD_CHANNEL + " TEXT"
            + ", " + FIELD_TS + " TEXT)";

    private static final String CREATE_INDEX_SUB_CHANNEL = "CREATE INDEX  " + INDEX_SUB_CHANNEL
            + " ON " + TABLE_SUB_CHANNEL
            + " (" + FIELD_CHANNEL + ")";

    private static final String CREATE_TABLE_PHOTO = "CREATE TABLE " + TABLE_PHOTO
            + " (" + FIELD_PHOTO_ID + " TEXT PRIMARY KEY"
            + ", " + FIELD_TITLE + " TEXT"
            + ", " + FIELD_STORY + " TEXT"
            + ", " + FIELD_API_URL + " TEXT"
            + ", " + FIELD_IMAGE_URL + " TEXT"
            + ", " + FIELD_LOGO_URL + " TEXT"
            + ", " + FIELD_NUM + " INTEGER DEFAULT 0"
            + ", " + FIELD_PUBLISH_DATE + " TEXT"
            + ", " + FIELD_TS + " TEXT)";

    private static final String CREATE_TABLE_PHOTO_CATEGORY = "CREATE TABLE " + TABLE_PHOTO_CATEGORY
            + " (" + FIELD_CATEGORY + " TEXT"
            + ", " + FIELD_PHOTO_ID + " TEXT"
            + ", " + FIELD_TS + " TEXT)";

    private static final String CREATE_INDEX_PHOTO_CATEGORY = "CREATE INDEX  " + INDEX_PHOTO_CATEGORY
            + " ON " + TABLE_PHOTO_CATEGORY
            + " (" + FIELD_CATEGORY + ")";

    private static final String CREATE_TABLE_PHOTO_ITEM = "CREATE TABLE " + TABLE_PHOTO_ITEM
            + " (" + FIELD_PHOTO_ITEM_ID + " TEXT PRIMARY KEY"
            + ", " + FIELD_DESCRIPTION + " TEXT"
            + ", " + FIELD_IMAGE_URL + " TEXT"
            + ", " + FIELD_SOURCE + " TEXT"
            + ", " + FIELD_TS + " TEXT)";

    private static final String CREATE_TABLE_PHOTO_DETAIL = "CREATE TABLE " + TABLE_PHOTO_DETAIL
            + " (" + FIELD_PHOTO_ID + " TEXT"
            + ", " + FIELD_PHOTO_ITEM_ID + " TEXT"
            + ", " + FIELD_TS + " TEXT)";

    private static final String CREATE_INDEX_PHOTO_DETAIL = "CREATE INDEX  " + INDEX_PHOTO_DETAIL
            + " ON " + TABLE_PHOTO_DETAIL
            + " (" + FIELD_PHOTO_ID + ")";

    private static final String CREATE_TABLE_VIDEO = "CREATE TABLE " + TABLE_VIDEO
            + " (" + FIELD_VIDEO_ID + " TEXT PRIMARY KEY"
            + ", " + FIELD_TITLE + " TEXT"
            + ", " + FIELD_STORY + " TEXT"
            + ", " + FIELD_API_URL + " TEXT"
            + ", " + FIELD_IMAGE_URL + " TEXT"
            + ", " + FIELD_LOGO_URL + " TEXT"
            + ", " + FIELD_VIDEO_URL + " TEXT"
            + ", " + FIELD_PUBLISH_DATE + " TEXT"
            + ", " + FIELD_TS + " TEXT)";

    private static final String CREATE_TABLE_VIDEO_CATEGORY = "CREATE TABLE " + TABLE_VIDEO_CATEGORY
            + " (" + FIELD_CATEGORY + " TEXT"
            + ", " + FIELD_VIDEO_ID + " TEXT"
            + ", " + FIELD_TS + " TEXT)";

    private static final String CREATE_INDEX_VIDEO_CATEGORY = "CREATE INDEX  " + INDEX_VIDEO_CATEGORY
            + " ON " + TABLE_VIDEO_CATEGORY
            + " (" + FIELD_CATEGORY + ")";

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL(CREATE_TABLE_SCREEN_UPDATE);
            db.execSQL(CREATE_TABLE_ADS);
            db.execSQL(CREATE_INDEX_ADS);
            db.execSQL(CREATE_TABLE_SUB_CHANNEL);
            db.execSQL(CREATE_INDEX_SUB_CHANNEL);
            db.execSQL(CREATE_TABLE_PHOTO);
            db.execSQL(CREATE_TABLE_PHOTO_CATEGORY);
            db.execSQL(CREATE_INDEX_PHOTO_CATEGORY);
            db.execSQL(CREATE_TABLE_PHOTO_ITEM);
            db.execSQL(CREATE_TABLE_PHOTO_DETAIL);
            db.execSQL(CREATE_INDEX_PHOTO_DETAIL);
            db.execSQL(CREATE_TABLE_VIDEO);
            db.execSQL(CREATE_TABLE_VIDEO_CATEGORY);
            db.execSQL(CREATE_INDEX_VIDEO_CATEGORY);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion == 10 && newVersion == 11) {
            try {
                db.beginTransaction();
//                db.execSQL("ALTER TABLE " + TABLE_SUB_CHANNEL + " ADD COLUMN " + FIELD_URL_CONTENT + " TEXT");
//                db.execSQL("ALTER TABLE " + TABLE_SUB_CHANNEL + " ADD COLUMN " + FIELD_HTML_CONTENT + " TEXT");
                db.execSQL("ALTER TABLE " + TABLE_SUB_CHANNEL + " RENAME TO old_" + TABLE_SUB_CHANNEL);
                db.execSQL(CREATE_TABLE_SUB_CHANNEL);
                db.execSQL("INSERT INTO " + TABLE_SUB_CHANNEL + " SELECT * FROM old_" + TABLE_SUB_CHANNEL);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    public ContentValues valuesScreenUpdate(ScreenUpdate data) {
        ContentValues values = new ContentValues();
        values.put(FIELD_SCREEN_NAME, data.screen_name);
        values.put(FIELD_UPDATE_DATE, data.update_date);
        values.put(FIELD_TS, System.currentTimeMillis());

        return values;
    }

    public void addScreenUpdate(ScreenUpdate data) {
        ContentValues values = valuesScreenUpdate(data);

        db.insert(TABLE_SCREEN_UPDATE, null, values);
    }

    public void addAllScreenUpdate(ArrayList<ScreenUpdate> datas) {
        for (ScreenUpdate data : datas) {
            addScreenUpdate(data);
        }
    }

    public void deleteScreenUpdate(String id) {
        db.delete(TABLE_SCREEN_UPDATE, FIELD_SCREEN_NAME + "=?", new String[] { id });
    }

    public void deleteAllScreenUpdate() {
        db.delete(TABLE_SCREEN_UPDATE, null, null);
    }

    public ScreenUpdate cursorScreenUpdate(Cursor cursor) {
        ScreenUpdate data = new ScreenUpdate();
        data.screen_name = cursor.getString(Arrays.asList(FIELDS_SCREEN_UPDATE).indexOf(FIELD_SCREEN_NAME));
        data.update_date = cursor.getString(Arrays.asList(FIELDS_SCREEN_UPDATE).indexOf(FIELD_UPDATE_DATE));

        return data;
    }

    public ScreenUpdate getScreenUpdate(String id) {
        ScreenUpdate result = null;

        Cursor cursor = db.query(TABLE_SCREEN_UPDATE,
                FIELDS_SCREEN_UPDATE,
                FIELD_SCREEN_NAME + "=?",
                new String[] { id },
                null, null, FIELD_TS + " DESC");
        if (cursor.moveToFirst()) {
            result = cursorScreenUpdate(cursor);
        }
        cursor.close();

        return result;
    }

    public ArrayList<ScreenUpdate> getAllScreenUpdate() {
        ArrayList<ScreenUpdate> result = new ArrayList<ScreenUpdate>();

        Cursor cursor = db.query(TABLE_SCREEN_UPDATE,
                FIELDS_SCREEN_UPDATE,
                null, null, null, null,
                FIELD_SCREEN_NAME + " ASC");
        if (cursor.moveToFirst()) {
            do {
                result.add(cursorScreenUpdate(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    public ContentValues valuesAds(Ads data) {
        ContentValues values = new ContentValues();
        values.put(FIELD_SCREEN_NAME, data.screen_name);
        values.put(FIELD_TYPE, data.type);
        values.put(FIELD_POSITION, data.position);
        values.put(FIELD_UNIT_ID, data.unit_id);
        values.put(FIELD_TS, System.currentTimeMillis());

        return values;
    }

    public void addAds(Ads data) {
        ContentValues values = valuesAds(data);

        db.insert(TABLE_ADS, null, values);
    }

    public void addAllAds(ArrayList<Ads> datas) {
        for (Ads data : datas) {
            addAds(data);
        }
    }

    public void deleteAds(String id1, String id2, String id3) {
        db.delete(TABLE_ADS, FIELD_SCREEN_NAME + "=? AND " + FIELD_TYPE + "=? AND " + FIELD_POSITION + "=?", new String[] { id1, id2, id3 });
    }

    public void deleteAllAds() {
        db.delete(TABLE_ADS, null, null);
    }

    public Ads cursorAds(Cursor cursor) {
        Ads data = new Ads();
        data.screen_name = cursor.getString(Arrays.asList(FIELDS_ADS).indexOf(FIELD_SCREEN_NAME));
        data.type = cursor.getInt(Arrays.asList(FIELDS_ADS).indexOf(FIELD_TYPE));
        data.position = cursor.getInt(Arrays.asList(FIELDS_ADS).indexOf(FIELD_POSITION));
        data.unit_id = cursor.getString(Arrays.asList(FIELDS_ADS).indexOf(FIELD_UNIT_ID));

        return data;
    }

    public Ads getAds(String id1, String id2, String id3) {
        Ads result = null;

        Cursor cursor = db.query(TABLE_ADS,
                FIELDS_ADS,
                FIELD_SCREEN_NAME + "=? AND " + FIELD_TYPE  + "=? AND " + FIELD_POSITION + "=?",
                new String[] { id1, id2, id3 },
                null, null, FIELD_TS + " DESC");
        if (cursor.moveToFirst()) {
            result = cursorAds(cursor);
        }
        cursor.close();

        return result;
    }

    public ArrayList<Ads> getAllAds() {
        ArrayList<Ads> result = new ArrayList<Ads>();

        Cursor cursor = db.query(TABLE_ADS,
                FIELDS_ADS,
                null, null, null, null,
                FIELD_SCREEN_NAME + " ASC");
        if (cursor.moveToFirst()) {
            do {
                result.add(cursorAds(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    public ContentValues valuesSubChannel(SubChannel data, String id) {
        ContentValues values = new ContentValues();
        values.put(FIELD_NAME, data.name);
        values.put(FIELD_TITLE, data.title);
        values.put(FIELD_API_URL, data.api_url);
        values.put(FIELD_IMAGE_URL, data.image_url);
        values.put(FIELD_URL_CONTENT, data.url_content);
        values.put(FIELD_HTML_CONTENT, data.html_content);
        values.put(FIELD_TYPE, data.type);
        values.put(FIELD_ACTION, data.action);
        values.put(FIELD_AB_COLOR, data.ab_color);
        values.put(FIELD_CHANNEL, id);
        values.put(FIELD_TS, System.currentTimeMillis());

        return values;
    }

    public void addSubChannel(SubChannel data, String id) {
        ContentValues values = valuesSubChannel(data, id);

        db.insert(TABLE_SUB_CHANNEL, null, values);
    }

    public void addAllSubChannel(ArrayList<SubChannel> datas, String id) {
        for (SubChannel data : datas) {
            addSubChannel(data, id);
        }
    }

    public void deleteAllSubChannel(String id) {
        db.delete(TABLE_SUB_CHANNEL, FIELD_CHANNEL + "=?", new String[] { id });
    }

    public void deleteAllSubChannel() {
        db.delete(TABLE_SUB_CHANNEL, null, null);
    }

    public SubChannel cursorSubChannel(Cursor cursor) {
        SubChannel data = new SubChannel();
        data.name = cursor.getString(Arrays.asList(FIELDS_SUB_CHANNEL).indexOf(FIELD_NAME));
        data.title = cursor.getString(Arrays.asList(FIELDS_SUB_CHANNEL).indexOf(FIELD_TITLE));
        data.api_url = cursor.getString(Arrays.asList(FIELDS_SUB_CHANNEL).indexOf(FIELD_API_URL));
        data.image_url = cursor.getString(Arrays.asList(FIELDS_SUB_CHANNEL).indexOf(FIELD_IMAGE_URL));
        data.url_content = cursor.getString(Arrays.asList(FIELDS_SUB_CHANNEL).indexOf(FIELD_URL_CONTENT));
        data.html_content = cursor.getString(Arrays.asList(FIELDS_SUB_CHANNEL).indexOf(FIELD_HTML_CONTENT));
        data.action = cursor.getString(Arrays.asList(FIELDS_SUB_CHANNEL).indexOf(FIELD_ACTION));
        data.ab_color = cursor.getString(Arrays.asList(FIELDS_SUB_CHANNEL).indexOf(FIELD_AB_COLOR));
        data.type = cursor.getString(Arrays.asList(FIELDS_SUB_CHANNEL).indexOf(FIELD_TYPE));

        return data;
    }

    public ArrayList<SubChannel> getAllSubChannel(String id) {
        ArrayList<SubChannel> result = new ArrayList<SubChannel>();

        Cursor cursor = db.query(TABLE_SUB_CHANNEL,
                FIELDS_SUB_CHANNEL,
                FIELD_CHANNEL + "=?",
                new String[] { id },
                null, null,
                FIELD_TS + " ASC");
        if (cursor.moveToFirst()) {
            do {
                result.add(cursorSubChannel(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    public ContentValues valuesPhoto(Photo data) {
        ContentValues values = new ContentValues();
        values.put(FIELD_PHOTO_ID, data.photo_id);
        values.put(FIELD_TITLE, data.title);
        values.put(FIELD_STORY, data.story);
        values.put(FIELD_API_URL, data.api_url);
        values.put(FIELD_IMAGE_URL, data.image_url);
        values.put(FIELD_LOGO_URL, data.logo_url);
        values.put(FIELD_NUM, data.num);
        values.put(FIELD_PUBLISH_DATE, data.publish_date);
        values.put(FIELD_TS, System.currentTimeMillis());

        return values;
    }

    public void addPhoto(Photo data) {
        ContentValues values = valuesPhoto(data);

        db.insert(TABLE_PHOTO, null, values);
    }

    public void addAllPhoto(ArrayList<Photo> datas) {
        for (Photo data : datas) {
            addPhoto(data);
        }
    }

    public void deletePhoto(String id) {
        db.delete(TABLE_PHOTO, FIELD_PHOTO_ID + "=?", new String[] { id });
    }

    public void deleteAllPhoto() {
        db.delete(TABLE_PHOTO, null, null);
    }

    public Photo cursorPhoto(Cursor cursor) {
        Photo data = new Photo();
        data.photo_id = cursor.getString(Arrays.asList(FIELDS_PHOTO).indexOf(FIELD_PHOTO_ID));
        data.title = cursor.getString(Arrays.asList(FIELDS_PHOTO).indexOf(FIELD_TITLE));
        data.story = cursor.getString(Arrays.asList(FIELDS_PHOTO).indexOf(FIELD_STORY));
        data.api_url = cursor.getString(Arrays.asList(FIELDS_PHOTO).indexOf(FIELD_API_URL));
        data.image_url = cursor.getString(Arrays.asList(FIELDS_PHOTO).indexOf(FIELD_IMAGE_URL));
        data.logo_url = cursor.getString(Arrays.asList(FIELDS_PHOTO).indexOf(FIELD_LOGO_URL));
        data.num = cursor.getInt(Arrays.asList(FIELDS_PHOTO).indexOf(FIELD_NUM));
        data.publish_date = cursor.getString(Arrays.asList(FIELDS_PHOTO).indexOf(FIELD_PUBLISH_DATE));

        return data;
    }

    public Photo getPhoto(String id) {
        Photo result = null;

        Cursor cursor = db.query(TABLE_PHOTO,
                FIELDS_PHOTO,
                FIELD_PHOTO_ID + "=?",
                new String[] { id },
                null, null, FIELD_TS + " DESC");
        if (cursor.moveToFirst()) {
            result = cursorPhoto(cursor);
        }
        cursor.close();

        return result;
    }

    public ArrayList<Photo> getAllPhoto() {
        ArrayList<Photo> result = new ArrayList<Photo>();

        Cursor cursor = db.query(TABLE_PHOTO,
                FIELDS_PHOTO,
                null, null, null, null,
                FIELD_PUBLISH_DATE + " DESC");
        if (cursor.moveToFirst()) {
            do {
                result.add(cursorPhoto(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    public ContentValues valuesPhotoCategory(PhotoCategory data) {
        ContentValues values = new ContentValues();
        values.put(FIELD_CATEGORY, data.category);
        values.put(FIELD_PHOTO_ID, data.photo_id);
        values.put(FIELD_TS, System.currentTimeMillis());

        return values;
    }

    public void addPhotoCategory(PhotoCategory data) {
        ContentValues values = valuesPhotoCategory(data);

        db.insert(TABLE_PHOTO_CATEGORY, null, values);
    }

    public void addAllPhotoCategory(ArrayList<PhotoCategory> datas) {
        for (PhotoCategory data : datas) {
            addPhotoCategory(data);
        }
    }

    public void deletePhotoCategory(String id) {
        db.delete(TABLE_PHOTO_CATEGORY, FIELD_CATEGORY + "=?", new String[] { id });
    }

    public void deleteAllPhotoCategory() {
        db.delete(TABLE_PHOTO_CATEGORY, null, null);
    }

    public PhotoCategory cursorPhotoCategory(Cursor cursor) {
        PhotoCategory data = new PhotoCategory();
        data.category = cursor.getString(Arrays.asList(FIELDS_PHOTO_CATEGORY).indexOf(FIELD_CATEGORY));
        data.photo_id = cursor.getString(Arrays.asList(FIELDS_PHOTO_CATEGORY).indexOf(FIELD_PHOTO_ID));

        return data;
    }

    public PhotoCategory getPhotoCategory(String id1, String id2) {
        PhotoCategory result = null;

        Cursor cursor = db.query(TABLE_PHOTO_CATEGORY,
                FIELDS_PHOTO_CATEGORY,
                FIELD_CATEGORY + "=? AND " + FIELD_PHOTO_ID + "=?",
                new String[] { id1, id2 },
                null, null, FIELD_TS + " DESC");
        if (cursor.moveToFirst()) {
            result = cursorPhotoCategory(cursor);
        }
        cursor.close();

        return result;
    }

    public ArrayList<PhotoCategory> getAllPhotoCategory(String id) {
        ArrayList<PhotoCategory> result = new ArrayList<PhotoCategory>();

        Cursor cursor = db.query(TABLE_PHOTO_CATEGORY,
                FIELDS_PHOTO_CATEGORY,
                FIELD_CATEGORY + "=?",
                new String[] { id },
                null, null,
                FIELD_TS + " ASC");
        if (cursor.moveToFirst()) {
            do {
                result.add(cursorPhotoCategory(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    public ArrayList<PhotoCategory> getAllPhotoCategoryOtherThan(String id1, String id2) {
        ArrayList<PhotoCategory> result = new ArrayList<PhotoCategory>();

        Cursor cursor = db.query(TABLE_PHOTO_CATEGORY,
                FIELDS_PHOTO_CATEGORY,
                FIELD_CATEGORY + "=? AND " + FIELD_PHOTO_ID + "<>?",
                new String[] { id1, id2 },
                null, null,
                FIELD_TS + " ASC");
        if (cursor.moveToFirst()) {
            do {
                result.add(cursorPhotoCategory(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    public ContentValues valuesPhotoItem(PhotoItem data) {
        ContentValues values = new ContentValues();
        values.put(FIELD_PHOTO_ITEM_ID, data.photo_item_id);
        values.put(FIELD_DESCRIPTION, data.description);
        values.put(FIELD_IMAGE_URL, data.image_url);
        values.put(FIELD_SOURCE, data.source);
        values.put(FIELD_TS, System.currentTimeMillis());

        return values;
    }

    public void addPhotoItem(PhotoItem data) {
        ContentValues values = valuesPhotoItem(data);

        db.insert(TABLE_PHOTO_ITEM, null, values);
    }

    public void addAllPhotoItem(ArrayList<PhotoItem> datas) {
        for (PhotoItem data : datas) {
            addPhotoItem(data);
        }
    }

    public void deletePhotoItem(String id) {
        db.delete(TABLE_PHOTO_ITEM, FIELD_PHOTO_ITEM_ID + "=?", new String[] { id });
    }

    public void deleteAllPhotoItem() {
        db.delete(TABLE_PHOTO_ITEM, null, null);
    }

    public PhotoItem cursorPhotoItem(Cursor cursor) {
        PhotoItem data = new PhotoItem();
        data.photo_item_id = cursor.getString(Arrays.asList(FIELDS_PHOTO_ITEM).indexOf(FIELD_PHOTO_ITEM_ID));
        data.description = cursor.getString(Arrays.asList(FIELDS_PHOTO_ITEM).indexOf(FIELD_DESCRIPTION));
        data.image_url = cursor.getString(Arrays.asList(FIELDS_PHOTO_ITEM).indexOf(FIELD_IMAGE_URL));
        data.source = cursor.getString(Arrays.asList(FIELDS_PHOTO_ITEM).indexOf(FIELD_SOURCE));

        return data;
    }

    public PhotoItem getPhotoItem(String id) {
        PhotoItem result = null;

        Cursor cursor = db.query(TABLE_PHOTO_ITEM,
                FIELDS_PHOTO_ITEM,
                FIELD_PHOTO_ITEM_ID + "=?",
                new String[] { id },
                null, null, FIELD_TS + " DESC");
        if (cursor.moveToFirst()) {
            result = cursorPhotoItem(cursor);
        }
        cursor.close();

        return result;
    }

    public ArrayList<PhotoItem> getAllPhotoItem() {
        ArrayList<PhotoItem> result = new ArrayList<PhotoItem>();

        Cursor cursor = db.query(TABLE_PHOTO_ITEM,
                FIELDS_PHOTO_ITEM,
                null, null, null, null,
                FIELD_TS + " DESC");
        if (cursor.moveToFirst()) {
            do {
                result.add(cursorPhotoItem(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    public ContentValues valuesPhotoDetail(PhotoDetail data) {
        ContentValues values = new ContentValues();
        values.put(FIELD_PHOTO_ID, data.photo_id);
        values.put(FIELD_PHOTO_ITEM_ID, data.photo_item_id);
        values.put(FIELD_TS, System.currentTimeMillis());

        return values;
    }

    public void addPhotoDetail(PhotoDetail data) {
        ContentValues values = valuesPhotoDetail(data);

        db.insert(TABLE_PHOTO_DETAIL, null, values);
    }

    public void addAllPhotoDetail(ArrayList<PhotoDetail> datas) {
        for (PhotoDetail data : datas) {
            addPhotoDetail(data);
        }
    }

    public void deletePhotoDetail(String id) {
        db.delete(TABLE_PHOTO_DETAIL, FIELD_PHOTO_ID + "=?", new String[] { id });
    }

    public void deleteAllPhotoDetail() {
        db.delete(TABLE_PHOTO_DETAIL, null, null);
    }

    public PhotoDetail cursorPhotoDetail(Cursor cursor) {
        PhotoDetail data = new PhotoDetail();
        data.photo_id = cursor.getString(Arrays.asList(FIELDS_PHOTO_DETAIL).indexOf(FIELD_PHOTO_ID));
        data.photo_item_id = cursor.getString(Arrays.asList(FIELDS_PHOTO_DETAIL).indexOf(FIELD_PHOTO_ITEM_ID));

        return data;
    }

    public ArrayList<PhotoDetail> getAllPhotoDetail(String id) {
        ArrayList<PhotoDetail> result = new ArrayList<PhotoDetail>();

        Cursor cursor = db.query(TABLE_PHOTO_DETAIL,
                FIELDS_PHOTO_DETAIL,
                FIELD_PHOTO_ID + "=?",
                new String[] { id },
                null, null,
                FIELD_TS + " ASC");
        if (cursor.moveToFirst()) {
            do {
                result.add(cursorPhotoDetail(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    public ContentValues valuesVideo(Video data) {
        ContentValues values = new ContentValues();
        values.put(FIELD_VIDEO_ID, data.video_id);
        values.put(FIELD_TITLE, data.title);
        values.put(FIELD_STORY, data.story);
        values.put(FIELD_IMAGE_URL, data.image_url);
        values.put(FIELD_LOGO_URL, data.logo_url);
        values.put(FIELD_VIDEO_URL, data.video_url);
        values.put(FIELD_PUBLISH_DATE, data.publish_date);
        values.put(FIELD_TS, System.currentTimeMillis());

        return values;
    }

    public void addVideo(Video data) {
        ContentValues values = valuesVideo(data);

        db.insert(TABLE_VIDEO, null, values);
    }

    public void addAllVideo(ArrayList<Video> datas) {
        for (Video data : datas) {
            addVideo(data);
        }
    }

    public void deleteVideo(String id) {
        db.delete(TABLE_VIDEO, FIELD_VIDEO_ID + "=?", new String[] { id });
    }

    public void deleteAllVideo() {
        db.delete(TABLE_VIDEO, null, null);
    }

    public Video cursorVideo(Cursor cursor) {
        Video data = new Video();
        data.video_id = cursor.getString(Arrays.asList(FIELDS_VIDEO).indexOf(FIELD_VIDEO_ID));
        data.title = cursor.getString(Arrays.asList(FIELDS_VIDEO).indexOf(FIELD_TITLE));
        data.story = cursor.getString(Arrays.asList(FIELDS_VIDEO).indexOf(FIELD_STORY));
        data.image_url = cursor.getString(Arrays.asList(FIELDS_VIDEO).indexOf(FIELD_IMAGE_URL));
        data.logo_url = cursor.getString(Arrays.asList(FIELDS_VIDEO).indexOf(FIELD_LOGO_URL));
        data.video_url = cursor.getString(Arrays.asList(FIELDS_VIDEO).indexOf(FIELD_VIDEO_URL));
        data.publish_date = cursor.getString(Arrays.asList(FIELDS_VIDEO).indexOf(FIELD_PUBLISH_DATE));

        return data;
    }

    public Video getVideo(String id) {
        Video result = null;

        Cursor cursor = db.query(TABLE_VIDEO,
                FIELDS_VIDEO,
                FIELD_VIDEO_ID + "=?",
                new String[] { id },
                null, null, FIELD_TS + " DESC");
        if (cursor.moveToFirst()) {
            result = cursorVideo(cursor);
        }
        cursor.close();

        return result;
    }

    public ArrayList<Video> getAllVideo() {
        ArrayList<Video> result = new ArrayList<Video>();

        Cursor cursor = db.query(TABLE_VIDEO,
                FIELDS_VIDEO,
                null, null, null, null,
                FIELD_PUBLISH_DATE + " DESC");
        if (cursor.moveToFirst()) {
            do {
                result.add(cursorVideo(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    public ContentValues valuesVideoCategory(VideoCategory data) {
        ContentValues values = new ContentValues();
        values.put(FIELD_CATEGORY, data.category);
        values.put(FIELD_VIDEO_ID, data.video_id);
        values.put(FIELD_TS, System.currentTimeMillis());

        return values;
    }

    public void addVideoCategory(VideoCategory data) {
        ContentValues values = valuesVideoCategory(data);

        db.insert(TABLE_VIDEO_CATEGORY, null, values);
    }

    public void addAllVideoCategory(ArrayList<VideoCategory> datas) {
        for (VideoCategory data : datas) {
            addVideoCategory(data);
        }
    }

    public void deleteVideoCategory(String id) {
        db.delete(TABLE_VIDEO_CATEGORY, FIELD_CATEGORY + "=?", new String[] { id });
    }

    public void deleteAllVideoCategory() {
        db.delete(TABLE_VIDEO_CATEGORY, null, null);
    }

    public VideoCategory cursorVideoCategory(Cursor cursor) {
        VideoCategory data = new VideoCategory();
        data.category = cursor.getString(Arrays.asList(FIELDS_VIDEO_CATEGORY).indexOf(FIELD_CATEGORY));
        data.video_id = cursor.getString(Arrays.asList(FIELDS_VIDEO_CATEGORY).indexOf(FIELD_VIDEO_ID));

        return data;
    }

    public VideoCategory getVideoCategory(String id1, String id2) {
        VideoCategory result = null;

        Cursor cursor = db.query(TABLE_VIDEO_CATEGORY,
                FIELDS_VIDEO_CATEGORY,
                FIELD_CATEGORY + "=? AND " + FIELD_VIDEO_ID + "=?",
                new String[] { id1, id2 },
                null, null, FIELD_TS + " DESC");
        if (cursor.moveToFirst()) {
            result = cursorVideoCategory(cursor);
        }
        cursor.close();

        return result;
    }

    public ArrayList<VideoCategory> getAllVideoCategory(String id) {
        ArrayList<VideoCategory> result = new ArrayList<VideoCategory>();

        Cursor cursor = db.query(TABLE_VIDEO_CATEGORY,
                FIELDS_VIDEO_CATEGORY,
                FIELD_CATEGORY + "=?",
                new String[] { id },
                null, null,
                FIELD_TS + " ASC");
        if (cursor.moveToFirst()) {
            do {
                result.add(cursorVideoCategory(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    public ArrayList<VideoCategory> getAllVideoCategoryOtherThan(String id1, String id2) {
        ArrayList<VideoCategory> result = new ArrayList<VideoCategory>();

        Cursor cursor = db.query(TABLE_VIDEO_CATEGORY,
                FIELDS_VIDEO_CATEGORY,
                FIELD_CATEGORY + "=? AND " + FIELD_VIDEO_ID + "<>?",
                new String[] { id1, id2 },
                null, null,
                FIELD_TS + " ASC");
        if (cursor.moveToFirst()) {
            do {
                result.add(cursorVideoCategory(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }
}