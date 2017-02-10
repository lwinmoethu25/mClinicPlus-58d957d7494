package com.lucentinsight.mclinicplus.activity;


import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lucentinsight.mclinicplus.R;
import com.lucentinsight.mclinicplus.fragment.ContactListDialogFragment;
import com.lucentinsight.mclinicplus.model.Clinic;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ClinicDetailActivity extends BaseActivity {

    public static final String CLINIC = "clinic";

    @InjectView(R.id.name)
    TextView tvName;

    @InjectView(R.id.address)
    TextView tvAddress;

    @InjectView(R.id.phone)
    TextView tvPhone;

    @InjectView(R.id.makeappointment)
    LinearLayout makeAppointmentContainer;

    @InjectView(R.id.map)
    ImageView ivMap;

    private Clinic clinic;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CLINIC, clinic);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_detail);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.inject(this);

        if(savedInstanceState != null){
            clinic = savedInstanceState.getParcelable(CLINIC);
        }
        else if(getIntent() != null){
            clinic = getIntent().getParcelableExtra(CLINIC);
        }

        if(clinic != null){
            tvName.setText(clinic.getName());
            tvAddress.setText(clinic.getAddress());
            tvPhone.setText(clinic.getContactNo());

            if(clinic.getMapImagePath() != null && !clinic.getMapImagePath().isEmpty()) {
//                Picasso.with(this).load(clinic.getMapImagePath()).into(ivMap);

                //For debug purpose
                Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        exception.printStackTrace();
                    }
                }).build();
                picasso.setLoggingEnabled(true);
                picasso.load(clinic.getMapImagePath()).into(ivMap, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });

            }
//            if(clinic.getName().equals("SSC Hospital")){
//                ivMap.setImageResource(R.drawable.ssc_map);
//            }
//            else if(clinic.getName().equals("Bahosi Clinic")){
//                ivMap.setImageResource(R.drawable.bahosi_map);
//            }
//            else if(clinic.getName().equals("Asia Royal Cardiac & Medical Care Centre")){
//                ivMap.setImageResource(R.drawable.asia_royal_map);
//            }

            makeAppointmentContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String []contacts = clinic.getContactNo().split(",");
                    ArrayList<String> list = new ArrayList<String>(Arrays.asList(contacts));
                    ContactListDialogFragment cFragment = ContactListDialogFragment.newInstance(list, null, null);
                    FragmentManager fm = getFragmentManager();
                    cFragment.show(fm, "contactDialogFragment");
                }
            });
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
