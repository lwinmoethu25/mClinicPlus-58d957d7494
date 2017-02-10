package com.lucentinsight.mclinicplus.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lucentinsight.mclinicplus.R;
import com.lucentinsight.mclinicplus.activity.DoctorDetailActivity;
import com.lucentinsight.mclinicplus.activity.MainActivity;
import com.lucentinsight.mclinicplus.adapter.DoctorListAdapter;
import com.lucentinsight.mclinicplus.common.DBHelper;
import com.lucentinsight.mclinicplus.model.Doctor;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DoctorListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DoctorListFragment extends Fragment implements AdapterView.OnItemClickListener{

    private List<Doctor> doctors;

    @InjectView(R.id.listView)
    ListView listView;

    DoctorListAdapter adapter;

    public DoctorListAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity)getActivity()).setDoctorListFragment(this);
    }


    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity)getActivity()).removeDoctorListFragment();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DoctorListFragment.
     */
    public static DoctorListFragment newInstance() {
        DoctorListFragment fragment = new DoctorListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public DoctorListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        DBHelper dbHelper = DBHelper.getInstance(getActivity());
        doctors = dbHelper.getDoctorList(null);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doctor_list, container, false);
        ButterKnife.inject(this, view);

        adapter = new DoctorListAdapter(getActivity(), doctors);
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
        Doctor doctor = adapter.getItem(position);
        Intent intent = new Intent(getActivity(), DoctorDetailActivity.class);
        intent.putExtra(DoctorDetailActivity.DOCTOR, doctor);
        startActivity(intent);
    }

}
