package com.ilyassan.medicalteleexpertise.service;

import com.ilyassan.medicalteleexpertise.enums.Role;
import com.ilyassan.medicalteleexpertise.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserService {

    public List<User> getAllSpecialists() {
        return User.all().stream()
                .filter(u -> u.getRole() == Role.SPECIALIST)
                .collect(Collectors.toList());
    }

    public User findById(Long id) {
        return User.find(id);
    }
}
