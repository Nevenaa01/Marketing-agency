package com.bsep2024.MarketingAgency.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bsep2024.MarketingAgency.models.User;
import com.bsep2024.MarketingAgency.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  UserRepository userRepository;

  @Transactional
  public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with id: " + id));

    return UserDetailsImpl.build(user);
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    String name = username.split(" ")[0];
    String surname = username.split(" ")[1];

    User user = userRepository.findByFullName(name, surname)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

    return UserDetailsImpl.build(user);
  }
}
