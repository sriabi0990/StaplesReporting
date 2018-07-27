package com.staples.weeklyreport.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.mongodb.repository.Query;

import com.staples.weeklyreport.Model.StepData;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StepDataRepository extends MongoRepository < StepData, String> {

    /**

     * This method will retrieve the step data of particular BU

     *

     * @param businessUnit

     *            business unit id

     * @return an instance of {@link StepData}

     */

    @Query("{ 'businessUnit' : ?0 }")
    public StepData findByBusinessUnit (final String businessUnit);


    void deleteById(String bu);


//    @Query(value="{'businessUnit' : $0}", delete = true)
//    public void deleteById(String businessUnit);



}
