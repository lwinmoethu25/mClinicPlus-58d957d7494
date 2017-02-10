package com.lucentinsight.mclinicplus.dao;


import java.util.Map;

import com.lucentinsight.mclinicplus.model.Clinic;

public interface ClinicDAO {
	
	public Map<String, Clinic> getAllClinics(String json);

}
