package com.lucentinsight.mclinicplus.activity;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.widget.EditText;

import com.astuetz.PagerSlidingTabStrip;
import com.lucentinsight.mclinicplus.R;
import com.lucentinsight.mclinicplus.adapter.ViewPagerAdapter;
import com.lucentinsight.mclinicplus.fragment.ClinicListFragment;
import com.lucentinsight.mclinicplus.fragment.DoctorListFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity {

    public static String CLINIC_SEARCH_TEXT = "clinicSearchText";
    public static String DOCTOR_SEARCH_TEXT = "doctorSearchText";

    @InjectView(R.id.pager)
    ViewPager pager;

    @InjectView(R.id.search)
    EditText etSearch;

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabStrip;

    ViewPagerAdapter adapter;

    DoctorListFragment doctorListFragment;
    ClinicListFragment clinicListFragment;

    private String clinicSearchText = "";
    private String doctorSearchText = "";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CLINIC_SEARCH_TEXT, clinicSearchText);
        outState.putString(DOCTOR_SEARCH_TEXT, doctorSearchText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null){
            clinicSearchText = savedInstanceState.getString(CLINIC_SEARCH_TEXT);
            doctorSearchText = savedInstanceState.getString(DOCTOR_SEARCH_TEXT);
        }

        ButterKnife.inject(this);

        adapter = new ViewPagerAdapter(this, pager);
        adapter.addTab(DoctorListFragment.class, new Bundle(), getString(R.string.doctor));
        adapter.addTab(ClinicListFragment.class, new Bundle(), getString(R.string.clinic));
        tabStrip.setViewPager(pager);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                switch (pager.getCurrentItem()){
                    case 0:
                        if(doctorListFragment != null) {
                            doctorListFragment.getAdapter().getFilter().filter(s);
                        }
                        break;

                    case 1:
                        if(clinicListFragment != null) {
                            clinicListFragment.getAdapter().getFilter().filter(s);
                        }
                        break;
                }
            }
        });

       ViewPager.OnPageChangeListener listener  = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        clinicSearchText = etSearch.getText().toString();
                        etSearch.setHint(getString(R.string.search_doctor));

                        etSearch.setText("");
                        if(doctorSearchText != null) {
                            etSearch.append(doctorSearchText);
                        }

                        doctorListFragment.getAdapter().getFilter().filter(doctorSearchText);
                        break;
                    case 1:
                        doctorSearchText = etSearch.getText().toString();
                        etSearch.setHint(getString(R.string.search_clinic));
                        etSearch.setText("");
                        if(clinicSearchText != null) {
                            etSearch.append(clinicSearchText);
                        }

                        clinicListFragment.getAdapter().getFilter().filter(clinicSearchText);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
//        pager.setOnPageChangeListener(listener);
        tabStrip.setOnPageChangeListener(listener);

    }

    public void setClinicListFragment(ClinicListFragment fragment){
        clinicListFragment = fragment;
    }

    public void removeDoctorListFragment(){
        doctorListFragment = null;
    }

    public void setDoctorListFragment(DoctorListFragment fragment){
        doctorListFragment = fragment;
    }

    public void removeClinicListFragment(){
        clinicListFragment = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

}
