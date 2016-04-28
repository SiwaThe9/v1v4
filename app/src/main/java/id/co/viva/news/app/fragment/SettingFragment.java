package id.co.viva.news.app.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActivityAboutUs;
import id.co.viva.news.app.adapter.SettingAdapter;
import id.co.viva.news.app.object.ItemView;

public class SettingFragment extends Fragment {
    public final static String TAG = SettingFragment.class.getSimpleName();

    private final static String ACTION_ABOUT_US = "about_us";
    private final static String ACTION_RATE = "rate";
    private final static String ACTION_CONTACT_US = "contact_us";

    private ArrayList<ItemView> datas = new ArrayList<ItemView>();

    private SettingAdapter adapter;
    private LinearLayoutManager layoutManager;

    private LinearLayout viewLayout;
    private RecyclerView viewRecycler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new SettingAdapter(getActivity(), this);
        layoutManager = new LinearLayoutManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        viewLayout = (LinearLayout) rootView.findViewById(R.id.layout);
        viewRecycler = (RecyclerView) rootView.findViewById(R.id.recycler);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewRecycler.setHasFixedSize(true);
        viewRecycler.setLayoutManager(layoutManager);
        viewRecycler.setAdapter(adapter);

        fillData(false);
    }

    private void fillData(boolean is_more) {
        datas.clear();
        datas.add(new ItemView(SettingAdapter.TYPE_HEADER, getString(R.string.label_info)));
        datas.add(new ItemView(SettingAdapter.TYPE_ITEM_TEXT, ACTION_ABOUT_US, getString(R.string.label_about_us)));
        datas.add(new ItemView(SettingAdapter.TYPE_ITEM_TEXT, ACTION_RATE, getString(R.string.label_rate)));
        datas.add(new ItemView(SettingAdapter.TYPE_ITEM_TEXT, ACTION_CONTACT_US, getString(R.string.label_contact_us)));

        adapter.setDatas(datas, is_more);
        adapter.notifyDataSetChanged();
    }

    public void openItem(String id) {
        if(id.equals(ACTION_ABOUT_US)) {
            openAboutUs();
        } else if(id.equals(ACTION_RATE)) {
            rate();
        } else if(id.equals(ACTION_CONTACT_US)) {
            sendEmail();
        }
    }

    private void openAboutUs() {
        Intent intent = new Intent(getActivity(), ActivityAboutUs.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void rate() {
        Uri uri = Uri.parse(getResources().getString(R.string.url_google_play) + getActivity().getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), R.string.label_failed_found_store, Toast.LENGTH_LONG).show();
        }
    }

    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(Constant.EMAIL_SCHEME, Constant.SUPPORT_EMAIL, null));
        startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }

}