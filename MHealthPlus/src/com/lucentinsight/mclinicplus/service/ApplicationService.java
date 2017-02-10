package com.lucentinsight.mclinicplus.service;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.lucentinsight.mclinicplus.MCApplication;
import com.lucentinsight.mclinicplus.activity.BaseActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ApplicationService extends BaseService{

    public static final String DB_VERSION_URL = "http://mclinicplus.com/dataversion.php";
    public static final String STATUS_UPDATE_URL = "http://mclinicplus.com/php/stats_edit.php";
    public static final String DB_DOWNLOAD_URL = "http://mclinicplus.com/assets/getDB.php";
    private static String TAG = "Application Service";

    protected MCApplication application;

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public ApplicationService(MCApplication application){
        this.application = application;
    }

    public static interface UpdateDBServiceListener{
        public void onFinishedDBUpdate();
        public void onUpdateProgress(int progress);
    }


    protected String getDBVersion(){
        String dataVersion = MCApplication.dataVersion;
        if(isNetworkAvailable()) {
            BufferedReader reader = null;
            InputStream is = null;

            try {
                URL url = new URL(DB_VERSION_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);

                //and connect!
                urlConnection.connect();
                is = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
                dataVersion = reader.readLine();
                Log.i(TAG, "Data Version :" + dataVersion);

            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (reader != null) {
                        reader.close();
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        return dataVersion;
    }

    protected void backupOldDB(){
        File file = new File(application.getCacheDir(), "mclinicplus.sqlite");
        if(file.exists()) {
            File desFile = new File(application.getCacheDir(), "oldmclinicplus.sqlite");
            file.renameTo(desFile);
        }
    }

    protected void restoreOldDB(){
        File corruptedFile = new File(application.getCacheDir(), "mclinicplus.sqlite");
        if(corruptedFile.exists()){
            corruptedFile.delete();
        }

        File file = new File(application.getCacheDir(), "oldmclinicplus.sqlite");
        if(file.exists()) {
            File desFile = new File(application.getCacheDir(), "mclinicplus.sqlite");
            file.renameTo(desFile);
        }
    }

    protected void removeBackupDB(){
        File file = new File(application.getCacheDir(), "oldmclinicplus.sqlite");
        if(file.exists()){
            file.delete();
        }
    }

    protected boolean downloadDB(UpdateDBServiceListener listener){
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(DB_DOWNLOAD_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage());
            }


            // this will be useful to display download percentage
            // might be -1: server did not report the length
//            int fileLength = connection.getContentLength();
            System.out.print(connection.getHeaderField("Content-length"));
            int fileLength = Integer.parseInt(connection.getHeaderField("Content-length"));
            Log.i("", "content-length:" + fileLength);

            // download the file
            input = connection.getInputStream();
            backupOldDB();
            File file = new File(application.getCacheDir(), "mclinicplus.sqlite");

            output = new FileOutputStream(file);

            byte data[] = new byte[4096];
            int count;
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                if (fileLength > 0 && listener != null) // only if total length is known
                    listener.onUpdateProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
            if(fileLength != -1 && total != fileLength) {
                restoreOldDB();
                return false;
            }
            else{
                removeBackupDB();
                Log.i("", "file-size:" + file.length());
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            restoreOldDB();
            return false;

        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
                Log.e(TAG, ignored.getMessage(), ignored);
            }

            if(connection != null)
                connection.disconnect();
        }

    }


    /**
     * updating db
     * @param listener
     */
    public void updateDB(final UpdateDBServiceListener listener){
        new AsyncTask<Void,Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                String dataversion = getDBVersion();
                String odataVersion= MCApplication.dataVersion;
                //	System.out.println(odataVersion);
                if(!dataversion.equals(odataVersion)){
                    Log.i(TAG, "require update");
                    if(downloadDB(listener)) {
                        MCApplication.dataVersion = dataversion;
                        application.saveDataToPreference();
                        Log.i(TAG, "data saved");
                    }
                }
                uploadStats();
                if(isMapAvailable()){
                    Log.i(TAG, "Map avaliable");
                }else{
                    Log.i(TAG, "Map not avaliable");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(listener != null)
                    listener.onFinishedDBUpdate();
            }
        }.execute();

    }

    private boolean isMapAvailable(){
        return  new File(application.getExternalCacheDir(), "cache_vts_com.lucentinsight.mclinicplus.0").exists();
    }


    /**
     * updating status
     * @throws IOException
     */
    public void uploadStats(){
        OutputStream os = null;
        BufferedWriter writer = null;
        InputStream is = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(STATUS_UPDATE_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.connect();
            //and connect!
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("installationId", MCApplication.id));
            params.add(new BasicNameValuePair("callCount", Integer.toString(MCApplication.callCount)));
            params.add(new BasicNameValuePair("appointmentCount", Integer.toString(MCApplication.appointmentCount)));
            params.add(new BasicNameValuePair("offlineCallCount", Integer.toString(MCApplication.offlineCallCount)));
            params.add(new BasicNameValuePair("offlineAppointmentCount", Integer.toString(MCApplication.offlineAppointmentCount)));
            os = urlConnection.getOutputStream();
            writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            System.out.println("upload status : " + getQuery(params));
            writer.write(getQuery(params));
            writer.flush();

            is = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
        }
        catch (IOException e){
           Log.e(TAG, e.getMessage(), e);
        }
        finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (writer != null) {
                    writer.close();
                }
                if (is != null) {
                    is.close();
                }
                if (reader != null) {
                    reader.close();
                }

            } catch (IOException ee) {
                Log.e(TAG, ee.getMessage(), ee);
            }
        }
        //urlConnection.getOutputStream().write( ("installationId=" + sID).getBytes());

    }


}
