package com.promineotech.jeep.service;

import java.util.List;

import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;

public interface JeepSalesService {

	/**
	 * @param model
	 * @param trim
	 * @return
	 */

	List<Jeep> fetchJeep(JeepModel model, String trim);

}
