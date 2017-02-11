package com.danielcudnik;

/**
 * Created by Bidzis on 1/17/2017.
 */
import com.danielcudnik.uzytkownik.ob.UzytkownikOB;
import com.danielcudnik.uzytkownik.repository.IUzytkownikRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public SecurityConfig(IUzytkownikRepository iUzytkownikRepository){
        this.iUzytkownikRepository = iUzytkownikRepository;
    }
    private IUzytkownikRepository iUzytkownikRepository;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService())
                .and()
                .inMemoryAuthentication()
                    .withUser("admin")
                    .password("12345")
                    .roles("ADMIN");


    }

    @Bean
    protected UserDetailsService userDetailsService() {
        return new UserDetailsService(){

            @Override
            public UserDetails loadUserByUsername(String username)
                    throws UsernameNotFoundException {
                UzytkownikOB uzytkownikOB = iUzytkownikRepository.znajdzPoNicku(username);
                if(uzytkownikOB!= null){
                    return new User(uzytkownikOB.getNick(),
                            uzytkownikOB.getHaslo(),
                            true,true,true,true,
                            getAuthorities());

                }
                else {
                    throw new  UsernameNotFoundException("Nie znalazlo uzytkownika '"
                            + username + "'");
                }

            }
        };

    }
    private Collection<? extends GrantedAuthority> getAuthorities(){
        List<GrantedAuthority> authList = new ArrayList<>();
        authList.add(new SimpleGrantedAuthority("USER"));
        return authList;
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.httpBasic().and().authorizeRequests()
                .antMatchers("/quizAndroid/uzytkownicy/zapiszUzytkownika").permitAll()
                .antMatchers("/quizAndroid/uzytkownicy/logowanieAdmin").access("hasRole('ADMIN')")
                .anyRequest().fullyAuthenticated().and().

//                .anyRequest().permitAll().and().
                httpBasic().and().
                csrf().disable();

    }
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }
}
