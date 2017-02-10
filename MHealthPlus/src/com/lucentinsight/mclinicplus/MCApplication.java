package com.lucentinsight.mclinicplus;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.lucentinsight.mclinicplus.activity.BaseActivity;
import com.lucentinsight.mclinicplus.common.Constant;
import com.lucentinsight.mclinicplus.service.BaseService;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


//@ReportsCrashes(mailTo = "lwinmoethu25@gmail.com", mode = ReportingInteractionMode.SILENT)
@ReportsCrashes(
    formUri = "https://collector.tracepot.com/0701bdca",
    httpMethod = HttpSender.Method.POST,
    customReportContent = {
            ReportField.APP_VERSION_CODE,
            ReportField.APP_VERSION_NAME,
            ReportField.ANDROID_VERSION,
            ReportField.PHONE_MODEL,
            ReportField.CUSTOM_DATA,
            ReportField.STACK_TRACE,
            ReportField.LOGCAT,
            ReportField.REPORT_ID,
            ReportField.PACKAGE_NAME,
            ReportField.USER_APP_START_DATE,
            ReportField.USER_CRASH_DATE
    }
)
public class MCApplication extends Application{
    public static String id = null;
    public static int callCount;
    public static int appointmentCount;
    public static int offlineCallCount;
    public static int offlineAppointmentCount;
    public static String dataVersion;
    private SharedPreferences sharedPreference;
    private boolean isFirstTime = false;

    public boolean isFirstTime(){
        return isFirstTime;
    }

    public void setFirstTime(boolean firstTime){
        isFirstTime = firstTime;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setFontAttrId(R.attr.fontPath).build());
        this.sharedPreference = this.getSharedPreferences(Constant.PREFERENCE_KEY, MODE_PRIVATE);

        id = getStringSharedPreferrence(Constant.ID);

        //check first time or not
        if(id == null || id.isEmpty()){
            initDefaultData();
            isFirstTime = true;
        }
        else{
            isFirstTime = false;
            callCount = getIntSharedPreferrence(Constant.CALL_COUNT);
            appointmentCount = getIntSharedPreferrence(Constant.APPOINTMENT_COUNT);
            dataVersion = getStringSharedPreferrence(Constant.DATA_VERSION);
            offlineCallCount = getIntSharedPreferrence(Constant.OFFLINE_CALL_COUNT);
            offlineAppointmentCount = getIntSharedPreferrence(Constant.OFFLINE_APPOINTMENT_COUNT);
        }

    }

    public void call(String phno, BaseActivity activity){
        if(activity.isNetworkAvailable()){
            callCount=callCount+1;
        }else{
            offlineCallCount=offlineCallCount+1;
        }
        saveDataToPreference();
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phno));
        activity.startActivity(callIntent);
    }

    public void makeAppointment(BaseActivity activity){
        if(activity.isNetworkAvailable()){
            appointmentCount=appointmentCount+1;
        }else{
            offlineAppointmentCount=offlineAppointmentCount+1;
        }
        saveDataToPreference();
    }


    private void initDefaultData(){
        id = UUID.randomUUID().toString();
        callCount = 0;
        appointmentCount = 0;
        dataVersion = "1";
        offlineCallCount = 0;
        offlineAppointmentCount = 0;
        saveDataToPreference();
    }

    public void saveDataToPreference(){
        saveStringSharedPreferrence(Constant.ID, id);
        saveIntSharedPreferrence(Constant.CALL_COUNT, callCount);
        saveIntSharedPreferrence(Constant.APPOINTMENT_COUNT, appointmentCount);
        saveStringSharedPreferrence(Constant.DATA_VERSION, dataVersion);
        saveIntSharedPreferrence(Constant.OFFLINE_CALL_COUNT, offlineCallCount);
        saveIntSharedPreferrence(Constant.OFFLINE_APPOINTMENT_COUNT, offlineAppointmentCount);
    }

    public void saveStringSharedPreferrence(String key,String value){
        sharedPreference.edit().putString(key,value).commit();
    }

    public String getStringSharedPreferrence(String key){
        return sharedPreference.getString(key, "");
    }

    public void saveIntSharedPreferrence(String key,int value){
        sharedPreference.edit().putInt(key,value).commit();
    }

    public int getIntSharedPreferrence(String key){
        return sharedPreference.getInt(key, -1);
    }

    public void clearSharedPreference(String key){
        sharedPreference.edit().remove(key).commit();
    }

    public void clearSharedPreferrence(){
        sharedPreference.edit().clear().commit();
    }


}
