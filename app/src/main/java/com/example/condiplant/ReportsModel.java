package com.example.condiplant;

public class ReportsModel {
    private String plantName;
    private String diseaseName;
    private String captureDate;

    public ReportsModel(String plantName, String diseaseName){
        this.plantName = plantName;
        this.diseaseName = diseaseName;
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
