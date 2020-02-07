package com.example.wuhanfighting;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

public class Record extends LitePalSupport {
    private String time;
    private String totalDiagnosed;
    private String totalSuspect;
    private String totalCured;
    private String totalDeath;

    public Record(String t,String td,String ts,String tc,String td1){
        time = t;
        totalDiagnosed = td;
        totalSuspect = ts;
        totalCured = tc;
        totalDeath = td1;
    }

    public String getTime() {
        return time;
    }

    public String getTotalDiagnosed() {
        return totalDiagnosed;
    }

    public String getTotalSuspect() {
        return totalSuspect;
    }

    public String getTotalCured() {
        return totalCured;
    }

    public String getTotalDeath() {
        return totalDeath;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTotalDiagnosed(String totalDiagnosed) {
        this.totalDiagnosed = totalDiagnosed;
    }

    public void setTotalSuspect(String totalSuspect) {
        this.totalSuspect = totalSuspect;
    }

    public void setTotalCured(String totalCured) {
        this.totalCured = totalCured;
    }

    public void setTotalDeath(String totalDeath) {
        this.totalDeath = totalDeath;
    }
}
