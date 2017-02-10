
package com.lucentinsight.mclinicplus.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final Context context;
    private final ViewPager viewPager;
    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

    static final class TabInfo {
        private final Class<?> clss;
        private final Bundle args;
        private final String category;


        TabInfo(Class<?> clss, Bundle args, String category) {
            this.clss = clss;
            this.args = args;
            this.category = category;
        }
    }



    public ViewPagerAdapter(FragmentActivity activity, ViewPager viewPager){
        super(activity.getSupportFragmentManager());
        this.context = activity;
        this.viewPager = viewPager;
        this.viewPager.setAdapter(this);
    }

    public void addTab(Class<?> clss, Bundle args, String category) {
        TabInfo info = new TabInfo(clss, args, category);
        mTabs.add(info);
        notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        TabInfo info = mTabs.get(position);
        return info.category;
    }

    @Override
    public Fragment getItem(int position) {
        TabInfo info = mTabs.get(position);
        return Fragment.instantiate(context, info.clss.getName(), info.args);
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }


}
