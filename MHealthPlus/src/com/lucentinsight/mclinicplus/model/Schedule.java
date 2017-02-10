package com.lucentinsight.mclinicplus.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.lucentinsight.mclinicplus.common.StringUtil;

import org.apache.http.conn.scheme.Scheme;

import java.util.HashMap;
import java.util.Map;

public class Schedule implements Parcelable, Comparable<Schedule>{

    public static Map<String, Integer> dayMap = new HashMap<String, Integer>();

    static {
        dayMap.put("SUN", 1);
        dayMap.put("MON", 2);
        dayMap.put("TUE", 3);
        dayMap.put("WED", 4);
        dayMap.put("THU", 5);
        dayMap.put("FRI", 6);
        dayMap.put("SAT", 7);
    }

    public int dayInDayOfWeek(){
        return  dayMap.get(day);
    }

	private String day;
	private String startTime;
	private String endTime;
    private String clinicName;

	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

    public Schedule(){

    }

    public Schedule(Parcel in){
        day = StringUtil.emptyToNull(in.readString());
        startTime = StringUtil.emptyToNull(in.readString());
        endTime = StringUtil.emptyToNull(in.readString());
        clinicName  = StringUtil.emptyToNull(in.readString());
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(StringUtil.nullSafeString(day));
        out.writeString(StringUtil.nullSafeString(startTime));
        out.writeString(StringUtil.nullSafeString(endTime));
        out.writeString(StringUtil.nullSafeString(clinicName));
    }

    public static final Parcelable.Creator<Schedule> CREATOR
            = new Parcelable.Creator<Schedule>() {
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    @Override
    public int compareTo(Schedule another) {
        return dayMap.get(day.toUpperCase().trim()).compareTo(dayMap.get(another.day.toUpperCase().trim()));
    }


}
