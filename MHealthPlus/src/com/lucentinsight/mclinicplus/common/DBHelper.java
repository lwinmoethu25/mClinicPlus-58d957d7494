package com.lucentinsight.mclinicplus.common;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.lucentinsight.mclinicplus.MCApplication;
import com.lucentinsight.mclinicplus.model.Clinic;
import com.lucentinsight.mclinicplus.model.Doctor;
import com.lucentinsight.mclinicplus.model.Schedule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper{
    private static DBHelper mInstance = null;

    private static final String DB_NAME = "mclinicplus.sqlite";
    private Context mContext;


    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_SHORT_NAME = "shortName";
    private static final String COLUMN_CLINIC_NAME = "clinicName";
    private static final String COLUMN_SPECIALIZATION = "specialization";
    private static final String COLUMN_QUALIFICATION = "qualification";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_LAT = "lat";
    private static final String COLUMN_LONG = "lang";
    private static final String COLUMN_CONTACT_NO = "contactNo";
    private static final String COLUMN_IMAGE_PATH = "imagePath";
    private static final String COLUMN_MAP_IMAGE_PATH = "mapImagePath";

    private static final String COLUMN_DAY = "day";
    private static final String COLUMN_START_TIME = "startTime";
    private static final String COLUMN_END_TIME = "endTime";


    private boolean requiredUpdate = false;


    public static synchronized DBHelper getInstance(Context context) {
        if (mInstance == null)
            mInstance = new DBHelper(context.getApplicationContext());

        return mInstance;
    }

    public DBHelper(Context context) {
        super(context, DB_NAME, null, Integer.parseInt(MCApplication.dataVersion));
        this.mContext = context;

        SQLiteDatabase db = null;

        try {
            db = getReadableDatabase();
            if (db != null) {
                db.close();
            }

            if(requiredUpdate) {
                copyDatabase();
            }
        }
        catch (SQLiteException e) {
        }
        finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        requiredUpdate = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        requiredUpdate = true;
    }

    private void copyDatabase() {
        InputStream in = null;
        OutputStream out = null;
        try {
            File file = new File(mContext.getCacheDir(), "mclinicplus.sqlite");
            //check file is ready.
            if(file.exists()) {
                in = new FileInputStream(file);
            }
            //otherwise use db file from asset
            else{
                AssetManager assetManager = mContext.getResources().getAssets();
                in = assetManager.open(DB_NAME);
            }
            out = new FileOutputStream(mContext.getDatabasePath(DB_NAME));
            System.out.println(mContext.getDatabasePath(DB_NAME).getAbsolutePath());
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

        }
        catch (IOException e) {
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        setDatabaseVersion();
        requiredUpdate = false;
    }

    private void setDatabaseVersion() {
        SQLiteDatabase db = null;
        try {
            db = SQLiteDatabase.openDatabase(mContext.getDatabasePath(DB_NAME).getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
            db.execSQL("PRAGMA user_version = " + MCApplication.dataVersion);
        }
        catch (SQLiteException e) {
        }
        finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    @Override
    public synchronized void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    private Doctor cursorToDoctor(Cursor cursor){
        Doctor doctor = new Doctor();
        doctor.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
        doctor.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
        doctor.setQualification(cursor.getString(cursor.getColumnIndex(COLUMN_QUALIFICATION)));
        doctor.setSpecialization(cursor.getString(cursor.getColumnIndex(COLUMN_SPECIALIZATION)));

        return doctor;
    }

    private Schedule cursorToSchedule(Cursor cursor){
        Schedule schedule = new Schedule();
        schedule.setDay(cursor.getString(cursor.getColumnIndex(COLUMN_DAY)));
        schedule.setStartTime(cursor.getString(cursor.getColumnIndex(COLUMN_START_TIME)));
        schedule.setEndTime(cursor.getString(cursor.getColumnIndex(COLUMN_END_TIME)));

        return schedule;
    }

    private Clinic cursorToClinic(Cursor cursor){
        Clinic clinic = new Clinic();

        if(cursor.getColumnIndex(COLUMN_CLINIC_NAME) != -1) {
            clinic.setName(cursor.getString(cursor.getColumnIndex(COLUMN_CLINIC_NAME)));
        }
        else{
            clinic.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
        }
        clinic.setShortName(cursor.getString(cursor.getColumnIndex(COLUMN_SHORT_NAME)));
        clinic.setAddress(cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)));
        clinic.setContactNo(cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_NO)));
        clinic.setLang(cursor.getDouble(cursor.getColumnIndex(COLUMN_LONG)));
        clinic.setLat(cursor.getDouble(cursor.getColumnIndex(COLUMN_LAT)));
        clinic.setImagePath(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH)));
        clinic.setMapImagePath(cursor.getString(cursor.getColumnIndex(COLUMN_MAP_IMAGE_PATH)));

        return clinic;
    }

    /**
     * getting doctor list
     * @param searchName optional search text
     * @return Doctor List
     */
    public List<Doctor> getDoctorList(String searchName){
        SQLiteDatabase db = getReadableDatabase();
        StringBuilder sql = new StringBuilder("SELECT d.id, d.name, d.qualification, d.specialization,s.day,");
        sql.append("s.startTime, s.endTime, c.name as clinicName,c.shortName as shortName,");
        sql.append("c.address,c.lat, c.lang,c.contactNo,c.imagePath,c.mapImagePath FROM cp_doctor d ");
        sql.append("LEFT JOIN cp_schedule as s ON d.id = s.doctorId ");
        sql.append("INNER JOIN cp_clinic as c ON c.id = s.clinicId ");


        String []params = new String[]{};
        if(searchName != null && !searchName.isEmpty()){
            sql.append("WHERE name like ? COLLATE NOCASE AND s.day IS NOT NULL AND TRIM(s.day) <> '' AND s.day <> '-'");
            params = new String[]{"%" + searchName + "%"};
        }
        else{
            sql.append("WHERE s.day IS NOT NULL AND TRIM(s.day) <> '' AND s.day <> '-'");
        }

        sql.append(" ORDER BY d.name,c.name");
        System.out.println(sql.toString());
        Cursor cursor = db.rawQuery(sql.toString(), params);
        List<Doctor> doctors = new ArrayList<Doctor>();

        cursor.moveToFirst();
        Doctor lastDoctor = null;
        Clinic lastClinic = null;
        while (!cursor.isAfterLast()) {
            Doctor doctor = cursorToDoctor(cursor);
            if(lastDoctor == null || !lastDoctor.getName().equals(doctor.getName())){
                doctors.add(doctor);
                lastDoctor = doctor;
                lastDoctor.setClinics(new ArrayList<Clinic>());
            }

            Clinic clinic = cursorToClinic(cursor);
            if(lastDoctor.getClinics().isEmpty() || !lastClinic.getName().equals(clinic.getName())){
                lastDoctor.getClinics().add(clinic);
                clinic.setSchedules(new ArrayList<Schedule>());
                lastClinic = clinic;
            }

            Schedule schedule = cursorToSchedule(cursor);
            lastClinic.getSchedules().add(schedule);
            cursor.moveToNext();
        }

        for(Doctor d : doctors) {
            for(Clinic c : d.getClinics()){
                Collections.sort(c.getSchedules());
            }
        }

        // make sure to close the cursor
        cursor.close();
        db.close();

        return doctors;
    }

    /**
     * getting clinic list
     * @param searchName optional search text
     * @return Clinic List
     */
    public List<Clinic> getClinicList(String searchName){
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM cp_clinic";
        String []params = new String[]{};
        if(searchName != null && !searchName.isEmpty()){
            sql += "WHERE name like ? COLLATE NOCASE";
            params = new String[]{"%" + searchName + "%"};
        }
        Cursor cursor = db.rawQuery(sql, params);
        List<Clinic> clinics = new ArrayList<Clinic>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Clinic clinic = cursorToClinic(cursor);
            clinics.add(clinic);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        db.close();

        return clinics;
    }



    /**
     * getting schedule list by doctor id
     * @param docId Doctor Id
     * @return Schedule List
     */
    public List<Schedule> getScheduleList(int docId){
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT s.day, s.startTime, s.endTime, c.name FROM cp_schedule s INNER JOIN cp_clinic c ON s.clinicId = c.id WHERE doctorId = ? AND s.day IS NOT NULL AND TRIM(s.day) <> '' AND s.day <> '-' ORDER BY day, startTime" ;
        String []params = new String[]{"" + docId};
        List<Schedule> scheduleList = new ArrayList<Schedule>();
        Cursor cursor = db.rawQuery(sql, params);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Schedule schedule = cursorToSchedule(cursor);
            schedule.setClinicName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            System.out.println(schedule.getDay());
            scheduleList.add(schedule);
            cursor.moveToNext();
        }

        Collections.sort(scheduleList);

        return scheduleList;
    }


}
