package com.danielcudnik.pytania.service.impl;


import com.danielcudnik.kategorie.dto.KategorieDTO;
import com.danielcudnik.kategorie.ob.KategorieOB;
import com.danielcudnik.kategorie.repository.IKategorieRepository;
import com.danielcudnik.odpowiedzi.ob.OdpowiedziOB;
import com.danielcudnik.odpowiedzi.repository.IOdpowiedziRepository;
import com.danielcudnik.punkty.dto.PunktyDTO;
import com.danielcudnik.punkty.ob.PunktyOB;
import com.danielcudnik.pytania.dto.PytaniaDTO;
import com.danielcudnik.pytania.dto.PytaniaZapiszDTO;
import com.danielcudnik.pytania.ob.PytaniaOB;
import com.danielcudnik.pytania.repository.IPytaniaRepository;
import com.danielcudnik.pytania.service.IPytaniaService;
import com.danielcudnik.utils.MyServerException;
import com.danielcudnik.utils.converters.PytaniaConventer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.apache.coyote.http11.Constants.a;

/**
 * Created by Bidzis on 11/12/2016.
 */
@Service
@Transactional
public class PytaniaServiceImpl implements IPytaniaService {

    private IPytaniaRepository iPytaniaRepository;
    private IKategorieRepository iKategorieRepository;
    private IOdpowiedziRepository iOdpowiedziRepository;

    @Autowired
    public PytaniaServiceImpl(IPytaniaRepository iPytaniaRepository,IKategorieRepository iKategorieRepository, IOdpowiedziRepository iOdpowiedziRepository){
        this.iPytaniaRepository = iPytaniaRepository;
        this.iKategorieRepository = iKategorieRepository;
        this.iOdpowiedziRepository = iOdpowiedziRepository;
    }

    @Override
    public List<PytaniaDTO> znajdzPytaniaPoKategorii(Long aIdTryb){
        List<PytaniaDTO> listaWynikowaPytaniaDTO = new ArrayList<>();
        List<PytaniaOB> listaPytaniaOB = iPytaniaRepository.znajdzPytaniaPoKategorii(aIdTryb);
        for(PytaniaOB punktyOB : listaPytaniaOB)
            listaWynikowaPytaniaDTO.add(PytaniaConventer.pytaniaOBdoPytaniaDTO(punktyOB));
        Collections.sort(listaWynikowaPytaniaDTO, (PytaniaDTO a, PytaniaDTO b) -> a.getId().compareTo(b.getId()));
        return listaWynikowaPytaniaDTO;
    }
    @Override
    public List<PytaniaDTO> znajdzPytaniaPoNazwieKategorii(String nazwaKategorii){
        List<PytaniaDTO> listaWynikowaPytaniaDTO = new ArrayList<>();
        List<PytaniaOB> listaPytaniaOB = iPytaniaRepository.znajdzPoNazwieKategorii(nazwaKategorii);
        for(PytaniaOB punktyOB : listaPytaniaOB)
            listaWynikowaPytaniaDTO.add(PytaniaConventer.pytaniaOBdoPytaniaDTO(punktyOB));
        Collections.sort(listaWynikowaPytaniaDTO, (PytaniaDTO a, PytaniaDTO b) -> a.getId().compareTo(b.getId()));
        return listaWynikowaPytaniaDTO;
    }
    @Override
    public PytaniaDTO zapiszPytania(PytaniaDTO aPytaniaDTO) throws MyServerException {
        KategorieDTO pKategorieDTO = aPytaniaDTO.getKategorieDTO();
        if (pKategorieDTO  == null)  throw new MyServerException("Nie znaleziono pola kategorii",HttpStatus.NOT_FOUND,new HttpHeaders());
        KategorieOB pKategorieOB = pKategorieDTO.getId() == null ? null :
                iKategorieRepository.findOne(pKategorieDTO.getId());
        if(pKategorieOB == null)  throw new MyServerException("Nie znaleziono kategorii",HttpStatus.NOT_FOUND,new HttpHeaders());

        PytaniaOB pPytaniaOB = aPytaniaDTO.getId() == null ? null :
                iPytaniaRepository.findOne(aPytaniaDTO.getId());
        if(pPytaniaOB == null) {
            aPytaniaDTO.setTechDate(aPytaniaDTO.getTechDate());
            aPytaniaDTO.setPytanie(aPytaniaDTO.getPytanie());
            aPytaniaDTO.setKategorieDTO(pKategorieDTO);
            pPytaniaOB = PytaniaConventer.pytaniaDTOdoPytaniaOB(aPytaniaDTO);
        }
        pPytaniaOB.setTechDate(aPytaniaDTO.getTechDate());
        pPytaniaOB.setPytanie(aPytaniaDTO.getPytanie());
        pPytaniaOB.setKategorie(pKategorieOB);
        return PytaniaConventer.pytaniaOBdoPytaniaDTO(iPytaniaRepository.save(pPytaniaOB));
    }
    @Override
    public PytaniaDTO zapiszPytania2(PytaniaZapiszDTO aPytaniaZapiszDTO) throws MyServerException {
        KategorieOB kategorieOB = iKategorieRepository.findOne(aPytaniaZapiszDTO.getKategorieID());
        if (kategorieOB == null) throw new MyServerException("Nie znaleziono trybu",HttpStatus.NOT_FOUND,new HttpHeaders());
        PytaniaOB pytaniaOB  = new PytaniaOB(aPytaniaZapiszDTO.getPytanie(),kategorieOB);
        return PytaniaConventer.pytaniaOBdoPytaniaDTO(iPytaniaRepository.save(pytaniaOB));

    }
    @Override
    public void usunPytania(Long aId) throws MyServerException{
        PytaniaOB pytaniaOB = iPytaniaRepository.findOne(aId);
        if(pytaniaOB == null) throw new MyServerException("Nie znaleziono pytania",HttpStatus.NOT_FOUND,new HttpHeaders());
        List<OdpowiedziOB>odpowiedziOBList = iOdpowiedziRepository.znajdzPunktyPoPytaniu(aId);
        if (!odpowiedziOBList.isEmpty())
            for (OdpowiedziOB odpowiedz:odpowiedziOBList) {
                iOdpowiedziRepository.delete(odpowiedz.getId());
            }
        iPytaniaRepository.delete(aId);
    }
    @Override
    public PytaniaDTO edytujPytanie(PytaniaDTO aPytaniaDTO) throws MyServerException{
        PytaniaOB pytaniaOB = iPytaniaRepository.findOne(aPytaniaDTO.getId());
        if (pytaniaOB == null) throw new MyServerException("Nie znaleziono pytania",HttpStatus.NOT_FOUND,new HttpHeaders());
        pytaniaOB.setPytanie(aPytaniaDTO.getPytanie());
        return PytaniaConventer.pytaniaOBdoPytaniaDTO(iPytaniaRepository.save(pytaniaOB));

    }
    @Override
    public List<PytaniaDTO> losujPytaniaPoKategorii(Long aIdTryb){
        List<PytaniaDTO> listaWynikowaPytaniaDTO = new ArrayList<>();
        List<PytaniaOB> listaPytaniaOB = iPytaniaRepository.znajdzPytaniaPoKategorii(aIdTryb);
        for(PytaniaOB punktyOB : listaPytaniaOB)
            listaWynikowaPytaniaDTO.add(PytaniaConventer.pytaniaOBdoPytaniaDTO(punktyOB));
        long seed = System.nanoTime();
        Collections.shuffle(listaWynikowaPytaniaDTO,new Random(seed));
        return listaWynikowaPytaniaDTO;
    }
}
