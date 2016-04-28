package id.co.viva.news.app.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.interfaces.OnCompleteListener;
import id.co.viva.news.app.interfaces.OnDoneListener;
import id.co.viva.news.app.interfaces.OnPathListener;
import id.co.viva.news.app.model.Path;

/**
 * Created by reza on 01/12/14.
 */
public class UserAccount {

    private OnCompleteListener mListener;

    private static final int STATUS_SUCCESS = 1;
    private static final int STATUS_FAILED = 0;
    private static final int STATUS_DELAY = 2;
    private static final String EMAIL = "email";
    private static final String FULLNAME = "fullname";
    private static final String MESSAGE = "message";
    private static final String STATUS = "status";
    private static final String TYPE = "type";
    private static final String USER_ID = "user_id";
    private static final String ACCESS_TOKEN = "access_token";

    private Context mContext;
    private String article_id;
    private String comment_text;
    private String app_id;
    private String mEmail;
    private String mPassword;
    private String mUsername;
    private String mRate;
    private String mUserSocialId;

    public UserAccount(Context mContext) {
        this.mContext = mContext;
    }

    public UserAccount(OnCompleteListener mListener, String mEmail) {
        this.mEmail = mEmail;
        this.mListener = mListener;
    }

    public UserAccount(String user_social_id, String app_id, String article_id, String mEmail, String mUsername, String comment_text,
                       OnCompleteListener mListener, Context mContext) {
        this.mUserSocialId = user_social_id;
        this.article_id = article_id;
        this.mEmail = mEmail;
        this.mUsername = mUsername;
        this.comment_text = comment_text;
        this.app_id = app_id;
        this.mListener = mListener;
        this.mContext = mContext;
    }

    public UserAccount(String mEmail, String mPassword, OnCompleteListener mListener, Context mContext) {
        this.mEmail = mEmail;
        this.mPassword = mPassword;
        this.mListener = mListener;
        this.mContext = mContext;
    }

    public UserAccount(String mUsername, String mEmail, String article_id, String mRate,
                       OnCompleteListener mListener, Context mContext) {
        this.article_id = article_id;
        this.mEmail = mEmail;
        this.mUsername = mUsername;
        this.mRate = mRate;
        this.mListener = mListener;
        this.mContext = mContext;
    }

    public UserAccount(String mUsername, String mEmail, String mPassword,
                       OnCompleteListener mListener, Context mContext) {
        this.mEmail = mEmail;
        this.mPassword = mPassword;
        this.mUsername = mUsername;
        this.mListener = mListener;
        this.mContext = mContext;
    }

    public void editProfile(final String username, final String gender, final String city, final String birth, final OnCompleteListener completeListener) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.NEW_UPDATE_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i(Constant.TAG, "Response Updated Profile : " + s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int status = jsonObject.getInt(STATUS);
                            String message = jsonObject.getString(MESSAGE);
                            if(status == STATUS_SUCCESS) {
                                completeListener.onComplete(message);
                            } else if(status == STATUS_FAILED) {
                                completeListener.onFailed(message);
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(mContext, R.string.label_error_post_comment, Toast.LENGTH_SHORT).show();
                        completeListener.onError(mContext.getResources().getString(R.string.label_error_post_comment));
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                if(gender.length() > 0) {
                    params.put("gender", gender);
                }
//                if(city.length() > 0) {
//                    params.put("city", city);
//                }
                if(birth.length() > 0) {
                    params.put("birthdate", birth);
                }
                return params;
            }
        };
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT_LONG,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(mContext).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
    }

    public void sendForgotPassword() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.FORGOT_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i(Constant.TAG, "Response Forgot Password : " + s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int status = jsonObject.getInt(STATUS);
                            String message = jsonObject.getString(MESSAGE);
                            if(status == STATUS_SUCCESS) {
                                mListener.onComplete(message);
                            } else if(status == STATUS_FAILED) {
                                mListener.onFailed(message);
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.getMessage();
                        Toast.makeText(mContext, R.string.label_error_post_comment, Toast.LENGTH_SHORT).show();
                        mListener.onError(mContext.getResources().getString(R.string.label_error_post_comment));
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", mEmail);
                return params;
            }
        };
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT_LONG,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(mContext).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
    }

    public void sendRating() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.NEWS_RATES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i(Constant.TAG, "Response Rates : " + s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int status = jsonObject.getInt(STATUS);
                            String message = jsonObject.getString(MESSAGE);
                            if(status == STATUS_SUCCESS) {
                                mListener.onComplete(message);
                            } else if(status == STATUS_FAILED) {
                                mListener.onFailed(message);
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.getMessage();
                        Toast.makeText(mContext, R.string.label_error_post_comment, Toast.LENGTH_SHORT).show();
                        mListener.onError(mContext.getResources().getString(R.string.label_error_post_comment));
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("article_id", article_id);
                params.put("email", mEmail);
                params.put("username", mUsername);
                params.put("rate", mRate);
                return params;
            }
        };
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT_LONG,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(mContext).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
    }

    public void sendComment() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.NEWS_COMMENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i(Constant.TAG, "Response Comment : " + s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int status = jsonObject.getInt(STATUS);
                            String message = jsonObject.getString(MESSAGE);
                            if(status == STATUS_SUCCESS) {
                                mListener.onComplete(message);
                            } else if(status == STATUS_FAILED) {
                                mListener.onFailed(message);
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.getMessage();
                        Toast.makeText(mContext, R.string.label_error_post_comment, Toast.LENGTH_SHORT).show();
                        mListener.onError(mContext.getResources().getString(R.string.label_error_post_comment));
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("article_id", article_id);
                params.put("email", mEmail);
                params.put("username", mUsername);
                params.put("comment_text", comment_text);
                params.put("app_id", app_id);
                if(mUserSocialId.length() > 0) {
                    params.put("user_social_id", mUserSocialId);
                }
                return params;
            }
        };
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT_LONG,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(mContext).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
    }

    public void signIn(final String app_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i(Constant.TAG, "Response Login : " + s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int status = jsonObject.getInt(STATUS);
                            String email = jsonObject.getString(EMAIL);
                            String fullname = jsonObject.getString(FULLNAME);
                            String message = jsonObject.getString(MESSAGE);
                            if(status == STATUS_SUCCESS) {
                                saveLoginStates(email, fullname, app_id);
                                mListener.onComplete(message);
                            } else if(status == STATUS_FAILED) {
                                mListener.onFailed(message);
                            } else if(status == STATUS_DELAY) {
                                saveLoginStates(email, fullname, app_id);
                                mListener.onDelay(message);
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.getMessage();
                        Toast.makeText(mContext, R.string.label_error_post_comment, Toast.LENGTH_SHORT).show();
                        mListener.onError(mContext.getResources().getString(R.string.label_error_post_comment));
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", mEmail);
                params.put("password", mPassword);
                return params;
            }
        };
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT_LONG,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(mContext).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
    }

    public void getCommentList(final String username, final String article_id, final OnDoneListener mDoneListener) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.NEWS_LIST_COMMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i(Constant.TAG, "Response Comment List : " + s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int status = jsonObject.getInt(STATUS);
                            if(status == STATUS_SUCCESS) {
                                mDoneListener.onCompleteListComment(jsonObject);
                            } else if(status == STATUS_FAILED) {
                                mDoneListener.onFailedListComment();
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.getMessage();
                        mDoneListener.onErrorListComment();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("article_id", article_id);
                return params;
            }
        };
        stringRequest.setShouldCache(true);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT_LONG,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(mContext).getRequestQueue().getCache().invalidate(Constant.NEWS_LIST_COMMENT, true);
        Global.getInstance(mContext).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
    }

    public void signUp() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i(Constant.TAG, "Response Register : " + s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int status = jsonObject.getInt(STATUS);
                            String message = jsonObject.getString(MESSAGE);
                            if(status == STATUS_SUCCESS) {
                                mListener.onComplete(message);
                            } else if(status == STATUS_FAILED) {
                                mListener.onFailed(message);
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.getMessage();
                        Toast.makeText(mContext, R.string.label_error_post_comment, Toast.LENGTH_SHORT).show();
                        mListener.onError(mContext.getResources().getString(R.string.label_error_post_comment));
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", mEmail);
                params.put("password", mPassword);
                params.put("username", mUsername);
                return params;
            }
        };
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT_REGISTRATION,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(mContext).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
    }

    public void requestPathAccessToken(final Path path, final OnPathListener pathListener) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.PATH_ACCESS_TOKEN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i(Constant.TAG, "Response Access Token Path : " + s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            String type = jsonObject.getString(TYPE);
                            if(type.equalsIgnoreCase("OK") || type.equalsIgnoreCase("CREATED") || type.equalsIgnoreCase("ACCEPTED")) {
                                String user_id = jsonObject.getString(USER_ID);
                                String access_token = jsonObject.getString(ACCESS_TOKEN);
                                Log.i(Constant.TAG, "TOKEN " + user_id + " " + access_token);
                                pathListener.onSavePathAttributes(access_token, user_id);
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.getMessage();
                        Log.e(Constant.TAG, volleyError.toString());
                        pathListener.onErrorGetAttributes(volleyError.getMessage());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("grant_type", "authorization_code");
                params.put("client_id", Constant.PATH_CLIENT_ID);
                params.put("client_secret", Constant.PATH_SECRET_ID);
                params.put("code", path.getPath_code());
                return params;
            }
        };
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT_LONG,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(mContext).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
    }

    public void saveLoginStates(String emailState, String fullnameState, String app_id) {
        Global.getInstance(mContext).getDefaultEditor().putString(Constant.LOGIN_STATES_EMAIL, emailState);
        Global.getInstance(mContext).getDefaultEditor().putString(Constant.LOGIN_STATES_FULL_NAME, fullnameState);
        Global.getInstance(mContext).getDefaultEditor().putString(Constant.LOGIN_STATES_APP_ID, app_id);
        Global.getInstance(mContext).getDefaultEditor().putBoolean(Constant.LOGIN_STATES_IS_LOGIN, true);
        Global.getInstance(mContext).getDefaultEditor().commit();
    }

    public void saveLoginStatesSocmed(String userSocialId, String app_id, String emailState, String fullnameState, String urlPhoto) {
        Global.getInstance(mContext).getDefaultEditor().putString(Constant.LOGIN_STATES_USER_SOCIAL_ID, userSocialId);
        Global.getInstance(mContext).getDefaultEditor().putString(Constant.LOGIN_STATES_APP_ID, app_id);
        Global.getInstance(mContext).getDefaultEditor().putString(Constant.LOGIN_STATES_EMAIL, emailState);
        Global.getInstance(mContext).getDefaultEditor().putString(Constant.LOGIN_STATES_FULL_NAME, fullnameState);
        Global.getInstance(mContext).getDefaultEditor().putString(Constant.LOGIN_STATES_URL_PHOTO, urlPhoto);
        Global.getInstance(mContext).getDefaultEditor().putBoolean(Constant.LOGIN_STATES_IS_LOGIN, true);
        Global.getInstance(mContext).getDefaultEditor().commit();
    }

    public void saveAttributesPath(String access_token, String user_id) {
        Global.getInstance(mContext).getDefaultEditor().putString(Constant.ATTRIBUTE_PATH_ACCESS_TOKEN, access_token);
        Global.getInstance(mContext).getDefaultEditor().putString(Constant.ATTRIBUTE_PATH_USER_ID, user_id);
        Global.getInstance(mContext).getDefaultEditor().commit();
    }

    public void saveAttributesUserProfile(String gender, String birthdate, String state, String province, String city) {
        if(gender.length() > 0) {
            Global.getInstance(mContext).getDefaultEditor().putString(Constant.LOGIN_STATES_GENDER, gender);
        }
        if(birthdate.length() > 0) {
            Global.getInstance(mContext).getDefaultEditor().putString(Constant.LOGIN_STATES_BIRTH_DATE, birthdate);
        }
        if(state.length() > 0) {
            Global.getInstance(mContext).getDefaultEditor().putString(Constant.LOGIN_STATES_COUNTRY, state);
        }
        if(province.length() > 0) {
            Global.getInstance(mContext).getDefaultEditor().putString(Constant.LOGIN_STATES_PROVINCE, province);
        }
        if(city.length() > 0) {
            Global.getInstance(mContext).getDefaultEditor().putString(Constant.LOGIN_STATES_CITY, city);
        }
        Global.getInstance(mContext).getDefaultEditor().putBoolean(Constant.LOGIN_STATES_IS_LOGIN, true);
        Global.getInstance(mContext).getDefaultEditor().commit();
    }

    public void deleteLoginStates() {
        Global.getInstance(mContext).getDefaultEditor().remove(Constant.LOGIN_STATES_EMAIL);
        Global.getInstance(mContext).getDefaultEditor().remove(Constant.LOGIN_STATES_FULL_NAME);
        if(Global.getInstance(mContext).getSharedPreferences(mContext)
                .getString(Constant.LOGIN_STATES_USER_SOCIAL_ID, "").length() > 0) {
            Global.getInstance(mContext).getDefaultEditor().remove(Constant.LOGIN_STATES_USER_SOCIAL_ID);
        }
        if(Global.getInstance(mContext).getSharedPreferences(mContext)
                .getString(Constant.LOGIN_STATES_APP_ID, "").length() > 0) {
            Global.getInstance(mContext).getDefaultEditor().remove(Constant.LOGIN_STATES_APP_ID);
        }
        if(Global.getInstance(mContext).getSharedPreferences(mContext)
                .getString(Constant.LOGIN_STATES_URL_PHOTO, "").length() > 0) {
            Global.getInstance(mContext).getDefaultEditor().remove(Constant.LOGIN_STATES_URL_PHOTO);
        }
        if(Global.getInstance(mContext).getSharedPreferences(mContext)
                .getString(Constant.LOGIN_STATES_PROVINCE, "").length() > 0) {
            Global.getInstance(mContext).getDefaultEditor().remove(Constant.LOGIN_STATES_PROVINCE);
        }
        if(Global.getInstance(mContext).getSharedPreferences(mContext)
                .getString(Constant.LOGIN_STATES_COUNTRY, "").length() > 0) {
            Global.getInstance(mContext).getDefaultEditor().remove(Constant.LOGIN_STATES_COUNTRY);
        }
        if(Global.getInstance(mContext).getSharedPreferences(mContext)
                .getString(Constant.LOGIN_STATES_GENDER, "").length() > 0) {
            Global.getInstance(mContext).getDefaultEditor().remove(Constant.LOGIN_STATES_GENDER);
        }
        if(Global.getInstance(mContext).getSharedPreferences(mContext)
                .getString(Constant.LOGIN_STATES_CITY, "").length() > 0) {
            Global.getInstance(mContext).getDefaultEditor().remove(Constant.LOGIN_STATES_CITY);
        }
        if(Global.getInstance(mContext).getSharedPreferences(mContext)
                .getString(Constant.LOGIN_STATES_BIRTH_DATE, "").length() > 0) {
            Global.getInstance(mContext).getDefaultEditor().remove(Constant.LOGIN_STATES_BIRTH_DATE);
        }
        Global.getInstance(mContext).getDefaultEditor().putBoolean(Constant.LOGIN_STATES_IS_LOGIN, false);
        Global.getInstance(mContext).getDefaultEditor().commit();
    }

}
