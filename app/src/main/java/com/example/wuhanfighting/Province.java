package com.example.wuhanfighting;

public class Province {
    private String province;
    private String suspectedNumber;
    private String totalNumber;
    private String cureNumber;
    private String deathNumber;

    public Province(String pr,String tn,String sn,String cn,String dn){
        province = pr;
        totalNumber = tn;
        suspectedNumber = sn;
        cureNumber = cn;
        deathNumber = dn;
    }
    public String getProvince() {
        return province;
    }

    public String getSuspectedNumber() {
        return suspectedNumber;
    }

    public String getTotalNumber() {
        return totalNumber;
    }

    public String getCureNumber() {
        return cureNumber;
    }

    public String getDeathNumber() {
        return deathNumber;
    }
}
