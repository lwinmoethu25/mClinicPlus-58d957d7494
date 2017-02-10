package com.lucentinsight.mclinicplus.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.lucentinsight.mclinicplus.common.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class Doctor implements Parcelable{

    private int id;
	private String name;
	private String qualification;
	private String specialization;
	private List<Schedule> schdules=new ArrayList<Schedule>();
    private List<Clinic> clinics = new ArrayList<Clinic>();

    public Doctor(){

    }

    public Doctor(Parcel in){
        id= in.readInt();
        name = StringUtil.emptyToNull(in.readString());
        qualification = StringUtil.emptyToNull(in.readString());
        specialization = StringUtil.emptyToNull(in.readString());
        clinics = in.readArrayList(Clinic.class.getClassLoader());
    }

	
	public List<Schedule> getSchdules() {
		return schdules;
	}
	public void setSchdules(List<Schedule> schdules) {
		this.schdules = schdules;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getQualification() {
		return qualification;
	}
	public void setQualification(String qualification) {
		this.qualification = qualification;
	}
	public String getSpecialization() {
		return specialization;
	}
	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}
	
	public void addSchedule(Schedule schedule){
		schdules.add(schedule);
	}

    public List<Clinic> getClinics() {
        return clinics;
    }

    public void setClinics(List<Clinic> clinics) {
        this.clinics = clinics;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(StringUtil.nullSafeString(name));
        out.writeString(StringUtil.nullSafeString(qualification));
        out.writeString(StringUtil.nullSafeString(specialization));
        if(clinics == null){
            clinics = new ArrayList<Clinic>();
        }
        out.writeList(clinics);
    }

    public static final Parcelable.Creator<Doctor> CREATOR
            = new Parcelable.Creator<Doctor>() {
        public Doctor createFromParcel(Parcel in) {
            return new Doctor(in);
        }

        public Doctor[] newArray(int size) {
            return new Doctor[size];
        }
    };
}
