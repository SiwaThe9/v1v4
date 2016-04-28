package id.co.viva.news.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.TutorialAdapter;
import id.co.viva.news.app.util.GCM;

/**
 * Created by reza on 09/03/15.
 */
public class ActTutorial extends FragmentActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private CirclePageIndicator circlePageIndicator;
    private Button btnSkip;
    private TutorialAdapter adapter;
    private ArrayList<String> imageTutorials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GCM.getInstance(this).checkGCM();

        setContentView(R.layout.coachmark_tutorial);
        //Define Views
        defineViews();
        //Get Image
        getImageTutorial();
    }

    private void defineViews() {
        imageTutorials = new ArrayList<>();
        viewPager = (ViewPager) findViewById(R.id.tutorial_pager);
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.circle_indicator);
        btnSkip = (Button) findViewById(R.id.button_skip_tutorial);
        btnSkip.setOnClickListener(this);
    }

    private void moveToApplication() {
        Intent intent = new Intent(getApplicationContext(), ActLanding.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_skip_tutorial) {
            moveToApplication();
        }
    }

    private void getImageTutorial() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Constant.TUTORIAL_IMAGES_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonarray = jsonObject.getJSONArray(Constant.data);
                    if (jsonarray != null) {
                        if (jsonarray.length() > 0) {
                            for (int i=0; i<jsonarray.length(); i++) {
                                String value = (String) jsonarray.get(i);
                                imageTutorials.add(value);
                            }
                        }
                    }
                    if (imageTutorials.size() > 0) {
                        adapter = new TutorialAdapter(getSupportFragmentManager(), imageTutorials);
                        viewPager.setAdapter(adapter);
                        circlePageIndicator.setViewPager(viewPager);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    moveToApplication();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                moveToApplication();
            }
        });
        stringRequest.setShouldCache(true);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT_LONG,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(this).getRequestQueue().getCache().invalidate(Constant.TUTORIAL_IMAGES_URL, true);
        Global.getInstance(this).getRequestQueue().getCache().get(Constant.TUTORIAL_IMAGES_URL);
        Global.getInstance(this).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
    }

}
