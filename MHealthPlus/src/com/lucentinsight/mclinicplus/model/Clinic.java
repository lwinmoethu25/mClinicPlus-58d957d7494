package com.lucentinsight.mclinicplus.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.lucentinsight.mclinicplus.common.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Clinic implements Parcelable{

    private String name;
    private String shortName;
    private String address;
    private double lat;
    private double lang;
    private String contactNo;
    private String imagePath;
    private String mapImagePath;

    public Clinic(){

    }

    public Clinic(Parcel in){
        name = StringUtil.emptyToNull(in.readString());
        shortName = StringUtil.emptyToNull(in.readString());
        address = StringUtil.emptyToNull(in.readString());
        lat = in.readDouble();
        lang = in.readDouble();
        contactNo = StringUtil.emptyToNull(in.readString());
        imagePath = StringUtil.emptyToNull(in.readString());
        mapImagePath = StringUtil.emptyToNull(in.readString());
        schedules = in.readArrayList(Schedule.class.getClassLoader());

    }

    private Map<String, Doctor> doctors = new HashMap<String, Doctor>();
    private Set<String> specializations = new HashSet<String>();
    private List<Schedule> schedules = new ArrayList<Schedule>();

    public void addSpecialization(String specialization){
        this.specializations.add(specialization);
    }

    public Set<String> getSpecialization() {
        return specializations;
    }

    public void setSpelications(Set<String> specialization) {
        this.specializations = specialization;
    }

    public Map<String, Doctor> getDoctors() {
        return doctors;
    }

    public void addDoctor(String specialization,Doctor doctor){
        doctors.put(specialization, doctor);
    }
    public void setDoctors(Map<String, Doctor> doctors) {
        this.doctors = doctors;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public double getLang() {
        return lang;
    }
    public void setLang(double lang) {
        this.lang = lang;
    }
    public String getContactNo() {
        return contactNo;
    }
    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public Set<String> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(Set<String> specializations) {
        this.specializations = specializations;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getMapImagePath() {
        return mapImagePath;
    }

    public void setMapImagePath(String mapImagePath) {
        this.mapImagePath = mapImagePath;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(StringUtil.nullSafeString(name));
        out.writeString(StringUtil.nullSafeString(shortName));
        out.writeString(StringUtil.nullSafeString(address));
        out.writeDouble(lat);
        out.writeDouble(lang);
        out.writeString(StringUtil.nullSafeString(contactNo));
        out.writeString(StringUtil.nullSafeString(imagePath));
        out.writeString(StringUtil.nullSafeString(mapImagePath));
        if(schedules == null){
            schedules = new ArrayList<Schedule>();
        }
        out.writeList(schedules);
    }

    public static final Parcelable.Creator<Clinic> CREATOR
            = new Parcelable.Creator<Clinic>() {
        public Clinic createFromParcel(Parcel in) {
            return new Clinic(in);
        }

        public Clinic[] newArray(int size) {
            return new Clinic[size];
        }
    };



}
