package com.lucentinsight.mclinicplus.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Checkable;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.SimpleAdapter.ViewBinder;

import com.lucentinsight.mclinicplus.R;
import com.lucentinsight.mclinicplus.common.ImageUtil;
import com.lucentinsight.mclinicplus.fragment.ContactListDialogFragment;
import com.lucentinsight.mclinicplus.model.Clinic;
import com.lucentinsight.mclinicplus.model.Schedule;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ClinicListAdaptor extends ArrayAdapter<Clinic> {

    private final Object mLock = new Object();
    private List<Clinic> clinics;
    private List<Clinic> originalItems;
    private ClinicFilter filter;

    public ClinicListAdaptor(Context context, List<Clinic> objects) {
        super(context, R.layout.clinic_list_item);
        clinics = objects;
        originalItems = new ArrayList<Clinic>();
        originalItems.addAll(objects);
    }

    @Override
    public Clinic getItem(int position) {
        return clinics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return clinics.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new ClinicFilter();
        }
        return filter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.clinic_list_item, null);
        }

        TextView tvName = (TextView)convertView.findViewById(R.id.name);
        TextView tvAddress = (TextView)convertView.findViewById(R.id.address);
        TextView tvPhone = (TextView)convertView.findViewById(R.id.phone);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.image);

        LinearLayout container = (LinearLayout) convertView.findViewById(R.id.makeappointment);


        Clinic clinic = clinics.get(position);

        if(clinic.getImagePath() != null || !clinic.getImagePath().isEmpty()) {
            Picasso.with(getContext()).load(clinic.getImagePath())
                    .placeholder(R.drawable.hospital).error(R.drawable.hospital).into(imageView);

            //For debug purpose
//            Picasso picasso = new Picasso.Builder(getContext()).listener(new Picasso.Listener() {
//                @Override
//                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
//                    exception.printStackTrace();
//                }
//            }).build();
//            picasso.setLoggingEnabled(true);
//            picasso.load(clinic.getImagePath()).error(R.drawable.hospital).into(imageView, new Callback() {
//                @Override
//                public void onSuccess() {
//
//                }
//
//                @Override
//                public void onError() {
//
//                }
//            });

            //loading with own code
//            ImageUtil.loadImage(clinic.getImagePath(), imageView);
        }

        tvName.setText(clinic.getName());
        tvAddress.setText(clinic.getAddress());
        tvPhone.setText(clinic.getContactNo());
        container.setTag(position);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = (int)v.getTag();
                Log.i("", "" + index);
                Clinic c = clinics.get(index);
                String []contacts = c.getContactNo().split(",");
                ArrayList<String> list = new ArrayList<String>(Arrays.asList(contacts));
                ContactListDialogFragment cFragment = ContactListDialogFragment.newInstance(list, null, null);
                FragmentManager fm = ((Activity)getContext()).getFragmentManager();
                cFragment.show(fm, "contactDialogFragment");
            }
        });

        return convertView;
    }


    private class ClinicFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            // Initiate our results object
            FilterResults results = new FilterResults();
            // If the adapter array is empty, check the actual items array and use it
            if (clinics == null) {
                synchronized (mLock) { // Notice the declaration above
                    clinics = new ArrayList<Clinic>(originalItems);
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
                final List<Clinic> items = originalItems;
                final int count = items.size();
                final ArrayList<Clinic> newItems = new ArrayList<Clinic>(count);
                for (int i = 0; i < count; i++) {
                    final Clinic item = items.get(i);
                    final String itemName = item.getName().toString().toLowerCase();
                    // First match against the whole, non-splitted value
//                    if (itemName.startsWith(prefixString)) {
                    if (itemName.contains(prefixString)) {
                        newItems.add(item);
                    }
//                    else {
//                        final String[] words = itemName.split(" ");
//                        final int wordCount = words.length;
//                        for (int k = 0; k < wordCount; k++) {
//                            if (words[k].startsWith(prefixString)) {
//                                newItems.add(item);
//                                break;
//                            }
//                        }
//                    }
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
            clinics = (ArrayList<Clinic>) results.values;
            // Let the adapter know about the updated list
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
