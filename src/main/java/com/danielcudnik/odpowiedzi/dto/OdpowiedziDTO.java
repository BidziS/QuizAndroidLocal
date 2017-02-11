package com.danielcudnik.odpowiedzi.dto;



import com.danielcudnik.base.dto.BaseDTO;
import com.danielcudnik.pytania.dto.PytaniaDTO;

import java.util.Date;

/**
 * Created by Bidzis on 11/12/2016.
 */
public class OdpowiedziDTO extends BaseDTO {
    private String odpowiedz;
    private boolean poprawna;
    private PytaniaDTO pytania;

    public OdpowiedziDTO(){

    }

    public OdpowiedziDTO(String odpowiedz, boolean poprawna, PytaniaDTO pytania) {
        this.odpowiedz = odpowiedz;
        this.poprawna = poprawna;
        this.pytania = pytania;
    }

    public OdpowiedziDTO(Long id, Date techDate, String odpowiedz, boolean poprawna, PytaniaDTO pytania){
        super(id, techDate);
        this.odpowiedz = odpowiedz;
        this.poprawna = poprawna;
        this.pytania = pytania;
    }

    public String getOdpowiedz() {
        return odpowiedz;
    }

    public void setOdpowiedz(String odpowiedz) {
        this.odpowiedz = odpowiedz;
    }

    public boolean getPoprawna() {
        return poprawna;
    }

    public void setPoprawna(boolean poprawna) {
        this.poprawna = poprawna;
    }

    public PytaniaDTO getPytania() {
        return pytania;
    }

    public void setPytania(PytaniaDTO pytania) {
        this.pytania = pytania;
    }
}
