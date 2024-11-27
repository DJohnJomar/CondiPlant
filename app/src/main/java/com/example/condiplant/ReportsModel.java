package com.example.condiplant;

public class ReportsModel {
    private String plantName;
    private String diseaseName;
    private int count;
    private String captureDate;

    public ReportsModel(String plantName, String diseaseName, int count){
        this.plantName = plantName;
        this.diseaseName = diseaseName;
        this.count = count;
    }

    public String getPlantName(){
        return this.plantName;
    }
    public String getDiseaseName(){
        return this.diseaseName;
    }
    public int getCount(){
        return this.count;
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
