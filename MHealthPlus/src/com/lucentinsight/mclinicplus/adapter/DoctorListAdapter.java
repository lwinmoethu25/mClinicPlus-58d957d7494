package com.lucentinsight.mclinicplus.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lucentinsight.mclinicplus.R;
import com.lucentinsight.mclinicplus.model.Clinic;
import com.lucentinsight.mclinicplus.model.Doctor;
import com.lucentinsight.mclinicplus.model.Schedule;

import java.util.ArrayList;
import java.util.List;

public class DoctorListAdapter extends ArrayAdapter<Doctor> {

    private final Object mLock = new Object();
    private List<Doctor> doctors;
    private List<Doctor> originalItems;
    private DoctorFilter filter;


    public DoctorListAdapter(Context context, List<Doctor> objects) {
        super(context, R.layout.doctor_list_item, objects);
        doctors = objects;
        originalItems = new ArrayList<Doctor>();
        originalItems.addAll(objects);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return doctors.size();
    }

    @Override
    public Doctor getItem(int position) {
        return doctors.get(position);
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new DoctorFilter();
        }
        return filter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Doctor doctor = doctors.get(position);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.doctor_list_item, null);
        }

        LinearLayout container = (LinearLayout) convertView.findViewById(R.id.scheduleContainer);
        container.removeAllViews();
        for(Clinic clinic : doctor.getClinics()) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int verticalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getContext().getResources().getDisplayMetrics());
            int horizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getContext().getResources().getDisplayMetrics());
            params.setMargins(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
            LinearLayout shLayout = new LinearLayout(getContext());
            shLayout.setLayoutParams(params);
            shLayout.setOrientation(LinearLayout.VERTICAL);

            TextView tvClinic = new TextView(getContext());
            if(clinic.getShortName() != null) {
                tvClinic.setText(clinic.getShortName().trim());
            }
            else{
                tvClinic.setText(clinic.getName().trim());
            }
            tvClinic.setTextColor(getContext().getResources().getColor(R.color.darkgray));
            tvClinic.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvClinic.setSingleLine(true);
            tvClinic.setGravity(Gravity.LEFT);

            shLayout.addView(tvClinic);

            LinearLayout dayLayout = new LinearLayout(getContext());
            dayLayout.setOrientation(LinearLayout.HORIZONTAL);
            dayLayout.setGravity(Gravity.LEFT);

            int i = 0;
            for(Schedule s : clinic.getSchedules()){
                TextView tvDay = new TextView(getContext());
                if(i != 0){
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    int leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getContext().getResources().getDisplayMetrics());
                    p.setMargins(leftMargin, 0, 0, 0);
                    tvDay.setLayoutParams(p);
                }

//                String dayFlag = StringUtil.capitalizeString(s.getDay().isEmpty()?"" : (s.getDay().substring(0, 3) + ""));
                tvDay.setText(s.getDay());
                tvDay.setTextColor(Color.BLACK);
                tvDay.setBackgroundColor(getContext().getResources().getColor(R.color.SkyBlue));
                tvDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
                int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getContext().getResources().getDisplayMetrics());
                tvDay.setPadding(padding, 0, padding, 0);
                dayLayout.addView(tvDay);
                i++;
            }

            shLayout.addView(dayLayout);

            container.addView(shLayout);

            //capturing click event for horizontalscrollview
            final View hItemView = convertView;
            final int hPosition = position;
            final ListView listView = (ListView)parent;

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listView.performItemClick(hItemView, hPosition, hItemView.getId());
                }
            });

        }

        TextView tvName = (TextView)convertView.findViewById(R.id.name);
        TextView tvQualification = (TextView)convertView.findViewById(R.id.qualification);
        TextView tvSpecial = (TextView)convertView.findViewById(R.id.specialization);

        tvName.setText(doctor.getName());
        tvQualification.setText(doctor.getQualification());
        tvSpecial.setText(doctor.getSpecialization());


        return convertView;
    }


    private class DoctorFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            // Initiate our results object
            FilterResults results = new FilterResults();
            // If the adapter array is empty, check the actual items array and use it
            if (doctors == null) {
                synchronized (mLock) { // Notice the declaration above
                    doctors = new ArrayList<Doctor>(originalItems);
                }
            }
            // No prefix is sent to filter by so we're going to send back the original array
            if (prefix == null || prefix.length() == 0) {
                synchronized (mLock) {
                    results.values = originalItems;
                    results.count = originalItems.size();
                }
            } else {
                // Compare lower case strings
                String prefixString = prefix.toString().toLowerCase();
                // Local to here so we're not changing actual array
                final List<Doctor> items = originalItems;
                final int count = items.size();
                final ArrayList<Doctor> newItems = new ArrayList<Doctor>(count);
                for (int i = 0; i < count; i++) {
                    final Doctor item = items.get(i);
                    final String itemName = item.getName().toString().toLowerCase();
                    // First match against the whole, non-splitted value
//                    if (itemName.startsWith(prefixString)) {
                    if (itemName.contains(prefixString)) {
                        newItems.add(item);
                    }
//                    else {
//                            final String[] words = itemName.split(" ");
//                            final int wordCount = words.length;
//                            for (int k = 0; k < wordCount; k++) {
//                                if (words[k].startsWith(prefixString)) {
//                                    newItems.add(item);
//                                    break;
//                                }
//                            }
//                     }
                }
                // Set and return
                results.values = newItems;
                results.count = newItems.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            doctors = (ArrayList<Doctor>) results.values;
            // Let the adapter know about the updated list
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }


}
