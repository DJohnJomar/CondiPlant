package com.example.condiplant;

public class ReportsModel {
    private String plantName;
    private String diseaseName;
    private String captureDate;

    public ReportsModel(String plantName, String diseaseName, String captureDate){
        this.plantName = plantName;
        this.diseaseName = diseaseName;
        this.captureDate = captureDate;
    }

    public String getPlantName(){
        return this.plantName;
    }
    public String getDiseaseName(){
        return this.diseaseName;
    }

    public String getCaptureDate(){
        return this.captureDate;
    }

    public void setCaptureDate(String date){
        this.captureDate = date;
    }

    public String toString(){
        return "Plant Name: "+this.getPlantName()+" Disease Name: "+this.getDiseaseName();
    }

}
