package id.co.viva.news.app.adapter;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import id.co.viva.news.app.fragment.DetailMainIndexFragment;
import id.co.viva.news.app.model.EntityMain;

/**
 * Created by reza on 15/10/14.
 */
public class DetailMainAdapter extends FragmentStatePagerAdapter {

    private ArrayList<EntityMain> entityMains;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private FragmentTransaction mCurTransaction = null;
    private final FragmentManager mFragmentManager;
    private int mPosition;
    private ArrayList<Fragment.SavedState> mSavedState = new ArrayList<>();
    private String mDetailParameter;
    private String name;

    public DetailMainAdapter(FragmentManager fragmentManager, ArrayList<EntityMain> entityMains, String dataParameter, String name) {
        super(fragmentManager);
        mFragmentManager = fragmentManager;
        mDetailParameter = dataParameter;
        this.name = name;
        this.entityMains = entityMains;
    }

    @Override
    public Fragment getItem(int position) {
        mPosition = position;
        return DetailMainIndexFragment
                .newInstance(entityMains.get(position).getId(),
                        name.replace(" ", "_").toUpperCase() + "_DETAIL_SCREEN",
                        mDetailParameter.toLowerCase().replace(" ", "_") + "_detail_screen");
    }

    @Override
    public int getItemPosition(Object object) {
//        if (mPosition >= 0) {
//            return mPosition;
//        } else {
            return POSITION_NONE;
//        }
    }

    @Override
    public int getCount() {
        return entityMains.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mFragments.size() > position) {
            Fragment f = mFragments.get(position);
            if (f != null) {
                return f;
            }
        }

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        Fragment fragment = getItem(position);
        if (mSavedState.size() > position) {
            Fragment.SavedState fss = mSavedState.get(position);
            if (fss != null) {
                fragment.setInitialSavedState(fss);
            }
        }

        while (mFragments.size() <= position) {
            mFragments.add(null);
        }

        fragment.setMenuVisibility(false);
        fragment.setUserVisibleHint(false);
        mFragments.set(position, fragment);
        mCurTransaction.add(container.getId(), fragment);

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        while (mSavedState.size() <= position) {
            mSavedState.add(null);
        }

        mSavedState.set(position, mFragmentManager.saveFragmentInstanceState(fragment));
        mFragments.set(position, null);

        mCurTransaction.remove(fragment);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }

    @Override
    public Parcelable saveState() {
        Bundle state = null;
        if (mSavedState.size() > 0) {
            state = new Bundle();
            Fragment.SavedState[] fss = new Fragment.SavedState[mSavedState.size()];
            mSavedState.toArray(fss);
            state.putParcelableArray("states", fss);
        }
        for (int i=0; i<mFragments.size(); i++) {
            Fragment f = mFragments.get(i);
            if (f != null) {
                if (state == null) {
                    state = new Bundle();
                }
                String key = "f" + i;
                mFragmentManager.putFragment(state, key, f);
            }
        }
        return state;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state != null) {
            Bundle bundle = (Bundle)state;
            bundle.setClassLoader(loader);
            Parcelable[] fss = bundle.getParcelableArray("states");
            mSavedState.clear();
            mFragments.clear();
            if (fss != null) {
                for(int i=0; i<fss.length; i++) {
                    mSavedState.add((Fragment.SavedState)fss[i]);
                }
            }
            Iterable<String> keys = bundle.keySet();
            for (String key : keys) {
                if (key.startsWith("f")) {
                    int index = Integer.parseInt(key.substring(1));
                    Fragment f = mFragmentManager.getFragment(bundle, key);
                    if (f != null) {
                        while (mFragments.size() <= index) {
                            mFragments.add(null);
                        }
                        f.setMenuVisibility(false);
                        mFragments.set(index, f);
                    }
                }
            }
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment)object).getView() == view;
    }

}
