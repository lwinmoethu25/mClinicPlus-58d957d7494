package com.lucentinsight.mclinicplus.dao.impl;


import java.util.HashMap;

import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.lucentinsight.mclinicplus.dao.ClinicDAO;
import com.lucentinsight.mclinicplus.model.Clinic;
import com.lucentinsight.mclinicplus.model.Doctor;
import com.lucentinsight.mclinicplus.model.Schedule;

public class ClinicJSONDAO implements ClinicDAO {

	public Map<String, Clinic> getAllClinics(String json) {
		JSONParser parser = new JSONParser();
		JSONArray a=null;
//		InputStream is = getAssets().open(""clinics.json"")
		
		Map<String, Clinic> clinicsMap=new HashMap<String, Clinic>();
			try {
//				System.out.println("json:"+json);
				a = (JSONArray) parser.parse(json);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		  for (Object o : a)
		  {
		    JSONObject jsonClinic = (JSONObject) o;
		    Clinic clinic = new Clinic();
		    clinic.setName((String) jsonClinic.get("name"));
		    clinic.setAddress((String) jsonClinic.get("address"));
		    clinic.setLat(Double.parseDouble((String) jsonClinic.get("lat")));
		    clinic.setLang(Double.parseDouble((String) jsonClinic.get("lang")));
		    clinic.setContactNo((String) jsonClinic.get("contactNo"));
		    clinicsMap.put(clinic.getName(), clinic);
		    JSONArray doctors =(JSONArray) jsonClinic.get("doctors");
		    doctors.remove(0);
		    for (Object objDoctor:doctors){
		    	JSONObject jsonDoctor = (JSONObject)objDoctor;
		    	Doctor doc = new Doctor();
		    	doc.setName((String)jsonDoctor.get("name"));
		    	doc.setQualification((String)jsonDoctor.get("qualification"));
		    	doc.setSpecialization((String)jsonDoctor.get("specialization"));
		    	clinic.addSpecialization((String)jsonDoctor.get("specialization"));
		    	JSONArray schedules =(JSONArray) jsonDoctor.get("schedule");
		    	schedules.remove(0);
		    	 for (Object objSchedule:schedules){
		    		 JSONObject jsonSchedule = (JSONObject)objSchedule; 
		    		 Schedule schedule = new Schedule();
		    		 schedule.setDay((String)jsonSchedule.get("day"));
		    		 schedule.setStartTime((String)jsonSchedule.get("startTime"));
		    		 schedule.setEndTime((String)jsonSchedule.get("endTime"));
//		    		 System.out.println("Schedule"+schedule.getDay()+schedule.getStartTime()+schedule.getEndTime());
		    		 doc.addSchedule(schedule);
		    	 }
		    	clinic.addDoctor(doc.getName()+doc.getSpecialization(), doc);
		    	
		    }
//		    System.out.println(clinic.getName());schedule
//		    System.out.println(clinic.getAddress());
//		    System.out.println(clinic.getLat());
//		    System.out.println(clinic.getLang());
//		    System.out.println(clinic.getContactNo());
	
		  }
		return clinicsMap;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
