package com.lucentinsight.mclinicplus.activity;


import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lucentinsight.mclinicplus.R;
import com.lucentinsight.mclinicplus.common.DBHelper;
import com.lucentinsight.mclinicplus.common.StringUtil;
import com.lucentinsight.mclinicplus.fragment.ContactListDialogFragment;
import com.lucentinsight.mclinicplus.model.Clinic;
import com.lucentinsight.mclinicplus.model.Doctor;
import com.lucentinsight.mclinicplus.model.Schedule;
import com.lucentinsight.mclinicplus.view.RoundedLetterView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DoctorDetailActivity extends BaseActivity {

    public static final String DOCTOR = "doctor";
    @InjectView(R.id.name)
    TextView tvName;

    @InjectView(R.id.specialization)
    TextView tvSpecializtion;

    @InjectView(R.id.qualification)
    TextView tvQualification;

    @InjectView(R.id.scheduleContainer)
    LinearLayout scheduleContainer;


    private Map<String, ArrayList<Schedule>> scheduleMap = new HashMap<String, ArrayList<Schedule>>();
    private Doctor doctor;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DOCTOR, doctor);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.inject(this);

        if(savedInstanceState != null){
            doctor = savedInstanceState.getParcelable(DOCTOR);
        }
        else if(getIntent() != null){
            doctor = getIntent().getParcelableExtra(DOCTOR);
        }

        if(doctor != null){
            List<Schedule> scheduleList = DBHelper.getInstance(this).getScheduleList(doctor.getId());
            tvName.setText(doctor.getName());
            tvSpecializtion.setText(doctor.getSpecialization());
            tvQualification.setText(doctor.getQualification());



            for(Schedule s : scheduleList){
                ArrayList<Schedule> list = scheduleMap.get(s.getDay());
                if(list == null){
                    list = new ArrayList<Schedule>();
                    scheduleMap.put(s.getDay().trim().toUpperCase(), list);
                }
                list.add(s);
            }

            int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
            int lineHeight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());


            //resort keys with day map
            List<String> keys = new ArrayList<String>(scheduleMap.keySet());

            Collections.sort(keys, new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    return Schedule.dayMap.get(lhs.toUpperCase().trim()).compareTo(Schedule.dayMap.get(rhs.trim().toUpperCase().trim()));
                }
            });

            for(String key : keys){
                ArrayList<Schedule> schedules = scheduleMap.get(key);
                LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.schedule_item, null);
                RoundedLetterView dayView = (RoundedLetterView)layout.findViewById(R.id.day);

//                String dayFlag = StringUtil.capitalizeString(schedules.get(0).getDay().isEmpty() ? "" : (schedules.get(0).getDay().substring(0, 3) + ""));
//                dayView.setTitleText(dayFlag);
                dayView.setTitleText(schedules.get(0).getDay());

                LinearLayout timeLayout = (LinearLayout)layout.findViewById(R.id.timeContainer);
                LinearLayout nameLayout = (LinearLayout)layout.findViewById(R.id.nameContainer);

                int i = 0;
                for(Schedule s : schedules){
                    TextView tvTime = new TextView(this);
                    tvTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    tvTime.setTextColor(getResources().getColor(R.color.darkgray));
                    String time = s.getStartTime() + " - ";
                    if(s.getEndTime() != null && !s.getEndTime().isEmpty() && !s.getEndTime().equals("-")){
                        time += s.getEndTime();
                    }
                    tvTime.setText(time);
                    tvTime.setPadding(0, padding, padding, padding);
                    tvTime.setTag(R.string.key, key);
                    tvTime.setTag(R.string.index, i + "");
                    timeLayout.addView(tvTime);
                    tvTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String key = (String) v.getTag(R.string.key);
                            int index = Integer.parseInt(v.getTag(R.string.index).toString());
                            Schedule s = scheduleMap.get(key).get(index);
                            Clinic clinic = null;
                            for(Clinic c : doctor.getClinics()){
                                if(c.getName().equals(s.getClinicName())){
                                    clinic = c;
                                    break;
                                }

                            }

                            String []contacts = clinic.getContactNo().split(",");
                            ArrayList<String> list = new ArrayList<String>(Arrays.asList(contacts));
                            ContactListDialogFragment cFragment = ContactListDialogFragment.newInstance(list, doctor, s);
                            FragmentManager fm = getFragmentManager();
                            cFragment.show(fm, "contactDialogFragment");
                        }
                    });



                    TextView tvName = new TextView(this);
                    tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    tvName.setTextColor(Color.BLACK);
                    tvName.setText(s.getClinicName());
                    tvName.setPadding(0, padding, padding, padding);
                    tvName.setTag(R.string.key, key);
                    tvName.setTag(R.string.index, i + "");
                    tvName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String key = (String) v.getTag(R.string.key);
                            int index = Integer.parseInt(v.getTag(R.string.index).toString());
                            Schedule s = scheduleMap.get(key).get(index);
                            Clinic clinic = null;
                            for(Clinic c : doctor.getClinics()){
                                if(c.getName().equals(s.getClinicName())){
                                    clinic = c;
                                    break;
                                }

                            }
                            Intent intent = new Intent(DoctorDetailActivity.this, ClinicDetailActivity.class);
                            intent.putExtra(ClinicDetailActivity.CLINIC, clinic);
                            startActivity(intent);
                        }
                    });
                    nameLayout.addView(tvName);
                    i++;
                }
                scheduleContainer.addView(layout);
                View line = new View(this);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, lineHeight);
                line.setLayoutParams(params);
                line.setBackgroundColor(getResources().getColor(R.color.lightgray));
                scheduleContainer.addView(line);
            }
        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
