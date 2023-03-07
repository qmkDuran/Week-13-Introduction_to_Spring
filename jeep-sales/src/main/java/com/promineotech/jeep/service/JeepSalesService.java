package com.promineotech.jeep.service;

import java.util.List;

import com.promineotech.jeep.entity.Jeep;

public interface JeepSalesService {

	/**
	 * @param model
	 * @param trim
	 * @return
	 */

	List<Jeep> fetchJeep(String model, String trim);

}
