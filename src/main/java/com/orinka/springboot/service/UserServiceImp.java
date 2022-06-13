package com.orinka.springboot.service;

import com.orinka.springboot.dao.UserDao;
import com.orinka.springboot.entity.EnumRole;
import com.orinka.springboot.entity.Role;
import com.orinka.springboot.entity.User;
import com.orinka.springboot.repository.RoleRepository;
import com.orinka.springboot.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Transactional
@Service
//@Slf4j
//@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserDao userDao;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @PostConstruct
        void init() {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(EnumRole.ROLE_USER));
        User startUser1 = new User("Olga1", "Mironova2", "seller", "admin", "admin");
        User startUser2 = new User("Mark", "Tarkovsky2", "realtor", "user", "user");
        Role role1 = roleRepository.saveAndFlush(new Role(EnumRole.ROLE_ADMIN));
        Role role2 = roleRepository.saveAndFlush(new Role(EnumRole.ROLE_USER));

        startUser1.setPassword(passwordEncoder.encode(startUser1.getPassword()));
        startUser2.setPassword(passwordEncoder.encode(startUser2.getPassword()));

        startUser1.addRole(role1);
        startUser1.addRole(role2);
        startUser2.addRole(role2);

        userRepository.save(startUser1);
        userRepository.save(startUser2);
    }


    @Override
    public void createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

 /*   public void createUser(User user, Set<Role> roles) {

        for (Role role: roles) {
            user.addRole(role);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }*/


    @Override
    public List<User> getAllUsers() { return userRepository.findAll(); }

    @Override
    public User getUserById(Long id) {
        return userRepository.getById(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);    }


    public void saveUser(User user) {
        userRepository.save(user);
    }


    @Override
    public void update(User user) {
        userDao.update(user);
    }

    public void update(User user, Long id) {
        if (user.getFirstName() != null) {
            userRepository.getUserById(id).setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            userRepository.getUserById(id).setLastName(user.getLastName());
        }
        if (user.getJob() != null) {
            userRepository.getUserById(id).setJob(user.getJob());
        }
        if (user.getUsername() != null) { //добавить логику проверки на уникальность
            userRepository.getUserById(id).setUsername(user.getUsername());
        }
        if (user.getPassword() != null) {
            userRepository.getUserById(id).setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getRoles() != null) {
            userRepository.getUserById(id).setRoles(user.getRoles());
        }

    }

    @Override
    public void update(User user, Set<Role> roles) {
        userRepository.getUserByUsername(user.getUsername()).setRoles(roles);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void delete(User user) { userRepository.delete(user);}

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        for (Role role : user.getRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName().toString()));
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);

    }



}







