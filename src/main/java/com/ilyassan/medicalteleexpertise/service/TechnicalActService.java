package com.ilyassan.medicalteleexpertise.service;

import com.ilyassan.medicalteleexpertise.model.TechnicalAct;

import java.util.List;

public class TechnicalActService {

    public List<TechnicalAct> getAllTechnicalActs() {
        return TechnicalAct.all();
    }
}
