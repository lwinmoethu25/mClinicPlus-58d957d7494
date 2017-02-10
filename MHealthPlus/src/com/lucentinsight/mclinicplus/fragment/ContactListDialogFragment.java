package com.lucentinsight.mclinicplus.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lucentinsight.mclinicplus.MCApplication;
import com.lucentinsight.mclinicplus.R;
import com.lucentinsight.mclinicplus.activity.BaseActivity;
import com.lucentinsight.mclinicplus.model.Clinic;
import com.lucentinsight.mclinicplus.model.Doctor;
import com.lucentinsight.mclinicplus.model.Schedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ContactListDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener{

    private static final String CONTACTS = "contacts";
    private static final String DOCTOR = "doctor";
    private static final String SCHEDULE = "schedule";

    @InjectView(R.id.listView)
    ListView listView;

    @InjectView(R.id.doctor)
    TextView tvDoctor;

    @InjectView(R.id.clinic)
    TextView tvClinic;

    @InjectView(R.id.date)
    TextView tvDate;

    @InjectView(R.id.time)
    TextView tvTime;

    @InjectView(R.id.container)
    LinearLayout container;

    @InjectView(R.id.cancel)
    Button btnCancel;

    private ArrayList<String> contacts;

    private Doctor doctor;

    private Schedule schedule;

    public static ContactListDialogFragment newInstance(ArrayList<String> contacts, Doctor doctor, Schedule schedule){
        ContactListDialogFragment fragment = new ContactListDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(CONTACTS, contacts);
        if(doctor != null) {
            bundle.putParcelable(DOCTOR, doctor);
        }
        if(schedule != null){
            bundle.putParcelable(SCHEDULE, schedule);
        }
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(CONTACTS, contacts);
        if(doctor != null){
            outState.putParcelable(DOCTOR, doctor);
        }
        if(schedule != null){
            outState.putParcelable(SCHEDULE, schedule);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MCApplication application = (MCApplication)getActivity().getApplication();
        if(savedInstanceState != null){
            contacts = savedInstanceState.getStringArrayList(CONTACTS);
            if(savedInstanceState.containsKey(DOCTOR)){
                doctor = savedInstanceState.getParcelable(DOCTOR);
            }
            if(getActivity() instanceof BaseActivity) {
                application.makeAppointment((BaseActivity) getActivity());
            }
        }
        else if(getArguments() != null){
            Bundle arguments = getArguments();
            contacts = arguments.getStringArrayList(CONTACTS);
            if(arguments.containsKey(DOCTOR)){
                doctor = arguments.getParcelable(DOCTOR);
            }
            if(arguments.containsKey(SCHEDULE)){
                schedule = arguments.getParcelable(SCHEDULE);
            }
            if(getActivity() instanceof BaseActivity) {
                application.makeAppointment((BaseActivity) getActivity());
            }
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dlg = super.onCreateDialog(savedInstanceState);
//        TextView titleTextView = (TextView) dlg.findViewById(android.R.id.title);
//        titleTextView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
//        titleTextView.setTextSize(getResources().getDimensionPixelSize(R.dimen.normal_text));
        dlg.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dlg;
    }

    @Override
    public void onStart() {
        super.onStart();

        // safety check
        if (getDialog() == null) {
            return;
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = (int)(metrics.widthPixels * 0.8);
        getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);


// ... other stuff you want to do in your onStart() method
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup vg, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list_dialog, container, false);
        ButterKnife.inject(this, view);


        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.contact_list_item, contacts){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.contact_list_item, null);
                }
                String phoneNo = (String)getItem(position);
                TextView tvPhone = (TextView)convertView.findViewById(R.id.phoneNo);
                tvPhone.setText(phoneNo);
                return convertView;
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        if(doctor != null && schedule != null){
            container.setVisibility(View.VISIBLE);
            tvDoctor.setText(doctor.getName());
            tvClinic.setText(schedule.getClinicName());
            String time = schedule.getStartTime() + " - ";
            if(schedule.getEndTime() != null && !schedule.getEndTime().isEmpty() && !schedule.getEndTime().equals("-")){
                time += schedule.getEndTime();
            }
            tvTime.setText(time);
            Calendar c = Calendar.getInstance();

            while(true){
                int dow = c.get(Calendar.DAY_OF_WEEK);
                if(dow == schedule.dayInDayOfWeek()){
                    break;
                }
                else{
                    c.roll(Calendar.DATE, true);
                }
            }

            SimpleDateFormat sf = new SimpleDateFormat("dd-MMM-yyyy EEEE", Locale.ENGLISH);
            tvDate.setText(sf.format(c.getTime()));
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactListDialogFragment.this.dismiss();
            }
        });
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //may use direct call instead of dial
//        Intent it = new Intent(Intent.ACTION_CALL);
        MCApplication application = (MCApplication)getActivity().getApplication();
        String phone = contacts.get(position).replace(" ", "");
        if(getActivity() instanceof BaseActivity) {
            application.call(phone, (BaseActivity) getActivity());
        }
        else {
            Intent it = new Intent(Intent.ACTION_CALL);
            it.setData(Uri.fromParts("tel", phone, null));
            startActivity(it);
        }
    }
}
