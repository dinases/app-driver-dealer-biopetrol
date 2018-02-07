package com.luisbar.waterdelivery.data.model.local;

import io.realm.RealmObject;

public class CreditPayment extends RealmObject {

    private Integer ognumi;
    private Integer ogcred;
    private double total;

    /**
     *
     * @return
     * The ognumi
     */
    public Integer getOgnumi() {
        return ognumi;
    }

    /**
     *
     * @param ognumi
     * The ognumi
     */
    public void setOgnumi(Integer ognumi) {
        this.ognumi = ognumi;
    }

    /**
     *
     * @return
     * The ogcred
     */
    public Integer getOgcred() {
        return ogcred;
    }

    /**
     *
     * @param ogcred
     * The ogcred
     */
    public void setOgcred(Integer ogcred) {
        this.ogcred = ogcred;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}