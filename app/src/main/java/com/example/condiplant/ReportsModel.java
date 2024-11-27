/*
This class is to be used by the reyclerView to store report objects
That is why it stores a count
 */
package com.example.condiplant;


public class ReportsModel {
    private String plantName;
    private String diseaseName;
    private int count;

    public ReportsModel(String plantName, String diseaseName, int count){
        this.plantName = plantName;
        this.diseaseName = diseaseName;
    }

    public String getPlantName(){
        return this.plantName;
    }
    public String getDiseaseName(){
        return this.diseaseName;
    }

    public String toString(){
        return "Plant Name: "+this.getPlantName()+" Disease Name: "+this.getDiseaseName();
    }

}
