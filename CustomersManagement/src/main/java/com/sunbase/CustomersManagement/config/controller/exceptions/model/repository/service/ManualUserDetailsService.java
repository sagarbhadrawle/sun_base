package com.sunBase.CustomersManagement.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sunBase.CustomersManagement.model.Users;
import com.sunBase.CustomersManagement.repository.UserRepository;



@Service
public class ManualUserDetailsService implements UserDetailsService {
	
	
	@Autowired
	private UserRepository uRepo;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Users> user = uRepo.findByEmail(email);
        
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        Users us = user.get();
        
//        System.out.println("hello,I am in manualuserdetails"+ us);
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority autho = new SimpleGrantedAuthority("ROLE_" + us.getRole());
        authorities.add(autho);
        
        User secUser = new User(
                us.getEmail(),
                us.getPassword(),
                authorities
        );

        return secUser;
    }

}
