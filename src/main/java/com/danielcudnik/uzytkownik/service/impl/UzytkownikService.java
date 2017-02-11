package com.danielcudnik.uzytkownik.service.impl;


import com.danielcudnik.punkty.ob.PunktyOB;
import com.danielcudnik.punkty.repository.IPunktyRepository;
import com.danielcudnik.utils.MyServerException;
import com.danielcudnik.utils.converters.UzytkownikConverter;
import com.danielcudnik.uzytkownik.dto.UzytkownikDTO;
import com.danielcudnik.uzytkownik.dto.UzytkownikLogowanieDTO;
import com.danielcudnik.uzytkownik.ob.UzytkownikOB;
import com.danielcudnik.uzytkownik.repository.IUzytkownikRepository;
import com.danielcudnik.uzytkownik.service.IUzytkownikService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Bidzis on 11/3/2016.
 */
@Service
@Transactional
public class UzytkownikService implements IUzytkownikService {

    private IUzytkownikRepository iUzytkownikRepository;
    private IPunktyRepository iPunktyRepository;

    @Autowired
    public UzytkownikService(IPunktyRepository iPunktyRepository, IUzytkownikRepository iUzytkownikRepository){
        this.iPunktyRepository = iPunktyRepository;
        this.iUzytkownikRepository = iUzytkownikRepository;
    }
    @Override
    public UzytkownikDTO znajdzUzytkownikaPoId(Long aId) throws MyServerException {
        UzytkownikOB pUzytkownikOB = iUzytkownikRepository.findOne(aId);
        if(pUzytkownikOB == null) throw new MyServerException("Nie ma takiego uzytkownika", HttpStatus.NOT_FOUND,new HttpHeaders());
        return UzytkownikConverter.uzytOBdoUzytkDTO(pUzytkownikOB);
    }

    @Override
    public UzytkownikDTO znajdzUzytkownikaPoNicku(String aNick) throws MyServerException {
        UzytkownikOB pUzytkownikOB = iUzytkownikRepository.znajdzPoNicku(aNick);
        if(pUzytkownikOB == null) throw new MyServerException("Nie ma takiego uzytkownika",HttpStatus.NOT_FOUND,new HttpHeaders());
        return UzytkownikConverter.uzytOBdoUzytkDTO(pUzytkownikOB);
    }

    @Override
    public List<UzytkownikDTO> znajdzWszystkichUzytkownikow() {
        List<UzytkownikDTO> listaWynikowaUzytkownikowDTO = new ArrayList<>();
        List<UzytkownikOB> listaUzytkownikowOB = iUzytkownikRepository.findAll();
        for(UzytkownikOB uzytkownik : listaUzytkownikowOB) listaWynikowaUzytkownikowDTO.add(UzytkownikConverter.uzytOBdoUzytkDTO(uzytkownik));

        return listaWynikowaUzytkownikowDTO;
    }
    @Override
    public UzytkownikDTO zapiszUzytkownika(UzytkownikDTO aUzytkownikDTO) throws MyServerException {
        if(aUzytkownikDTO == null){
            throw new MyServerException("Brak pola uzytkownik",HttpStatus.NOT_FOUND,new HttpHeaders());
        }
        UzytkownikOB pUzytkownikOB = aUzytkownikDTO.getId() == null ? null : iUzytkownikRepository.findOne(aUzytkownikDTO.getId());
        if(pUzytkownikOB == null){
            UzytkownikOB pUzytkonikOBEmailVeryfication = aUzytkownikDTO.getNick() == null ? null : iUzytkownikRepository.znajdzPoNicku(aUzytkownikDTO.getNick());
            if(pUzytkonikOBEmailVeryfication != null) throw new MyServerException("Istnieje już użytkownik o takim nicku",HttpStatus.METHOD_NOT_ALLOWED,new HttpHeaders());
            aUzytkownikDTO = UzytkownikConverter.uzytOBdoUzytkDTO(iUzytkownikRepository.save(UzytkownikConverter.uzytDTOdoUzytkOB(aUzytkownikDTO)));

            return aUzytkownikDTO;
        }
        pUzytkownikOB.setNick(aUzytkownikDTO.getNick());
        pUzytkownikOB.setHaslo(aUzytkownikDTO.getHaslo());

        return UzytkownikConverter.uzytOBdoUzytkDTO(iUzytkownikRepository.save(pUzytkownikOB));
    }
    @Override
    public UzytkownikDTO logowanieUzytkownika(UzytkownikLogowanieDTO aUserDTO) throws MyServerException{
        UzytkownikOB user = aUserDTO.getNick() == null ? null : iUzytkownikRepository.znajdzPoNicku(aUserDTO.getNick());
        if(user == null) throw new MyServerException("User not found",HttpStatus.NOT_FOUND, new HttpHeaders());
        if(aUserDTO.getHaslo() == null) throw new  MyServerException("Password not found",HttpStatus.NOT_FOUND,new HttpHeaders());
        if(user.getHaslo().hashCode() != aUserDTO.getHaslo().hashCode()) throw new MyServerException("Password dont match",HttpStatus.METHOD_NOT_ALLOWED,new HttpHeaders());
        return UzytkownikConverter.uzytOBdoUzytkDTO(user);
    }
    @Override
    public void usunUzytkownika(String aNick) throws  MyServerException{
        UzytkownikOB uzytkownikOB = iUzytkownikRepository.znajdzPoNicku(aNick);
        if(uzytkownikOB == null) throw new MyServerException("User with this id not exists",HttpStatus.NOT_FOUND,new HttpHeaders());
        List<PunktyOB> punktyUzytkownika = iPunktyRepository.znajdzPunktyPoUzytkowniku(uzytkownikOB.getId());
        if (!punktyUzytkownika.isEmpty())
            for (PunktyOB punkty:punktyUzytkownika) {
               iPunktyRepository.delete(punkty.getId());
            }
        iUzytkownikRepository.delete(uzytkownikOB.getId());
    }
    public void usunUzytkownikaPoId(Long aId) throws  MyServerException{
        UzytkownikOB uzytkownikOB = iUzytkownikRepository.findOne(aId);
        if(uzytkownikOB == null) throw new MyServerException("User with this id not exists",HttpStatus.NOT_FOUND,new HttpHeaders());
        List<PunktyOB> punktyUzytkownika = iPunktyRepository.znajdzPunktyPoUzytkowniku(uzytkownikOB.getId());
        if (!punktyUzytkownika.isEmpty())
            for (PunktyOB punkty:punktyUzytkownika) {
                iPunktyRepository.delete(punkty.getId());
            }
        iUzytkownikRepository.delete(uzytkownikOB.getId());
    }


}
