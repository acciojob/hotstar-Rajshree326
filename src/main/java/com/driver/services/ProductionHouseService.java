package com.driver.services;


import com.driver.EntryDto.ProductionHouseEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.repository.ProductionHouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductionHouseService {

    @Autowired
    public ProductionHouseRepository productionHouseRepository;

    public Integer addProductionHouseToDb(ProductionHouseEntryDto productionHouseEntryDto){
        ProductionHouse productionHouse = new ProductionHouse();
        productionHouse.setName(productionHouseEntryDto.getName());
        productionHouse.setRatings(0);

        ProductionHouse savedProductionHouse = productionHouseRepository.save(productionHouse);
        return  savedProductionHouse.getId();
    }



}
