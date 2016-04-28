package id.co.viva.news.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActDetailFavorite;
import id.co.viva.news.app.adapter.FavoriteAdapter;
import id.co.viva.news.app.model.Favorites;
import id.co.viva.news.app.model.SliderContentImage;
import id.co.viva.news.app.services.Analytics;

/**
 * Created by reza on 17/11/14.
 */
public class FavoritesFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ArrayList<Favorites> favoritesArrayList;
    private int favoriteListSize;
    private FavoriteAdapter favoriteAdapter;
    private TextView textNoResult;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatActivity mActionBarActivity = (AppCompatActivity) getActivity();
        mActionBarActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.news_base_color)));
        mActionBarActivity.getSupportActionBar().setIcon(R.drawable.logo_viva_coid_second);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_favorites, container, false);

        //Set analytic
        getAnalytics();

        textNoResult = (TextView) rootView.findViewById(R.id.text_no_result_detail_content_favorite);
        textNoResult.setVisibility(View.GONE);

        DynamicListView listFavorite = (DynamicListView) rootView.findViewById(R.id.list_favorites);
        listFavorite.setOnItemClickListener(this);

        Global.getInstance(getActivity()).getSharedPreferences(getActivity());
        Global.getInstance(getActivity()).getDefaultEditor();

        String favoriteList = Global.getInstance(getActivity()).getSharedPreferences(getActivity())
                .getString(Constant.FAVORITES_LIST, "");
        favoriteListSize = Global.getInstance(getActivity()).getSharedPreferences(getActivity())
                .getInt(Constant.FAVORITES_LIST_SIZE, 0);

        favoritesArrayList = Global.getInstance(getActivity()).getInstanceGson().
                fromJson(favoriteList, Global.getInstance(getActivity()).getTypeFavorites());
        favoriteAdapter = new FavoriteAdapter(getActivity(), favoritesArrayList);

        if (favoriteListSize > 0) {
            for (int i=0; i<favoriteListSize; i++) {
                Log.i(Constant.TAG, "TITLE FAVORITES : " + favoritesArrayList.get(i).getTitle());
            }
        } else {
            textNoResult.setVisibility(View.VISIBLE);
        }
        Global.getInstance(getActivity()).getDefaultEditor().commit();

        SimpleSwipeUndoAdapter simpleSwipeUndoAdapter = new SimpleSwipeUndoAdapter(favoriteAdapter, getActivity(),
                new OnDismissCallback() {
                    @Override
                    public void onDismiss(@NonNull ViewGroup viewGroup, @NonNull int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            favoriteAdapter.removeItem(position);
                            favoriteAdapter.notifyDataSetChanged();
                            String favorite = Global.getInstance(getActivity()).getInstanceGson().toJson(favoritesArrayList);
                            Global.getInstance(getActivity()).getDefaultEditor().putString(Constant.FAVORITES_LIST, favorite);
                            Global.getInstance(getActivity()).getDefaultEditor().putInt(Constant.FAVORITES_LIST_SIZE, favoritesArrayList.size());
                            Global.getInstance(getActivity()).getDefaultEditor().commit();
                            if (favoritesArrayList.size() <= 0) {
                                textNoResult.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

        if (favoriteListSize > 0 || favoritesArrayList != null) {
            listFavorite.setAdapter(favoriteAdapter);
            favoriteAdapter.notifyDataSetChanged();
            //Undo Adapter
            simpleSwipeUndoAdapter.setAbsListView(listFavorite);
            listFavorite.setAdapter(simpleSwipeUndoAdapter);
            listFavorite.enableSimpleSwipeUndo();
        }

        return rootView;
    }

    private void getAnalytics() {
        Analytics analytics = new Analytics(getActivity());
        analytics.getAnalyticByATInternet(Constant.FAVORITES_PAGE);
        analytics.getAnalyticByGoogleAnalytic(Constant.FAVORITES_PAGE);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (favoriteListSize > 0) {
            Favorites favorites = favoritesArrayList.get(position);
            ArrayList<SliderContentImage> sliderContentImages = favorites.getSliderContentImages();
            String listThumbSlider = Global.getInstance(getActivity()).getInstanceGson().toJson(sliderContentImages);
            Bundle bundle = new Bundle();
            bundle.putString("title", favorites.getTitle());
            bundle.putString("image_url", favorites.getImage_url());
            bundle.putString("date_publish", favorites.getDate_publish());
            bundle.putString("content", favorites.getContent());
            bundle.putString("reporter_name", favorites.getReporter_name());
            bundle.putString("image_caption", favorites.getImage_caption());
            bundle.putString("list_thumbnail_body", listThumbSlider);
            bundle.putInt("list_thumbnail_body_size", sliderContentImages.size());
            Intent intent = new Intent(getActivity(), ActDetailFavorite.class);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
        }
    }

}
