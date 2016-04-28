package id.co.viva.news.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import id.co.viva.news.app.R;
import id.co.viva.news.app.model.DeviceInfo;

/**
 * Created by reza on 03/11/14.
 */
public class AboutFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AppCompatActivity mActivity = (AppCompatActivity) getActivity();
//        mActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.news_base_color)));
//        mActivity.getSupportActionBar().setIcon(R.drawable.logo_viva_coid_second);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_about, container, false);
        TextView viewTextAppVersion = (TextView) rootView.findViewById(R.id.text_app_version);
        TextView viewTextAboutCopyright = (TextView) rootView.findViewById(R.id.text_about_copyright);

        DeviceInfo deviceInfo = new DeviceInfo(getActivity());
        viewTextAppVersion.setText("Version " + deviceInfo.getAppVersionName());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
        Date date = new Date();
        String text_about_copyright = viewTextAboutCopyright.getText().toString();
        text_about_copyright = text_about_copyright.replace("[YEAR]", sdf.format(date));
        viewTextAboutCopyright.setText(text_about_copyright);

        return rootView;
    }

}
