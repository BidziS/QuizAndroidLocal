package com.danielcudnik.tryb.ob;

import com.danielcudnik.base.ob.BaseOB;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Created by Bidzis on 11/9/2016.
 */
@Entity
@Table(name = "tryby")
@SequenceGenerator(allocationSize = 1, name = "SEQ", sequenceName = "GEN_TRYB_ID")
public class TrybOB extends BaseOB {
    @Column(unique = true)
    private String nazwa;

    public TrybOB(){

    }
    public TrybOB(String nazwaTrybu){
        this.nazwa = nazwaTrybu;
    }

    public String getNazwaTrybu() {
        return nazwa;
    }

    public void setNazwa(String aNazwaTrybu) {
        this.nazwa = aNazwaTrybu;
    }
}
