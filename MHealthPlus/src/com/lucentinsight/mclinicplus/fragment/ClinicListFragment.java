package com.lucentinsight.mclinicplus.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lucentinsight.mclinicplus.R;
import com.lucentinsight.mclinicplus.activity.ClinicDetailActivity;
import com.lucentinsight.mclinicplus.activity.MainActivity;
import com.lucentinsight.mclinicplus.adapter.ClinicListAdaptor;
import com.lucentinsight.mclinicplus.common.DBHelper;
import com.lucentinsight.mclinicplus.model.Clinic;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClinicListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClinicListFragment extends Fragment implements AdapterView.OnItemClickListener{

    List<Clinic> clinics;


    @InjectView(R.id.listView)
    ListView listView;

    ClinicListAdaptor adapter;

    public ClinicListAdaptor getAdapter() {
        return adapter;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ClinicFragment.
     */
    public static ClinicListFragment newInstance() {
        ClinicListFragment fragment = new ClinicListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity)getActivity()).setClinicListFragment(this);
    }



    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity)getActivity()).removeClinicListFragment();
    }

    public ClinicListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        DBHelper dbHelper = DBHelper.getInstance(getActivity());
        clinics = dbHelper.getClinicList(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_clinic_list, container, false);
        ButterKnife.inject(this, view);

        adapter = new ClinicListAdaptor(getActivity(), clinics);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        return view;
    }

    public void filter(String text){
        adapter.getFilter().filter(text);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Clinic clinic = adapter.getItem(position);
        Intent intent = new Intent(getActivity(), ClinicDetailActivity.class);
        intent.putExtra(ClinicDetailActivity.CLINIC, clinic);
        startActivity(intent);
    }
}
