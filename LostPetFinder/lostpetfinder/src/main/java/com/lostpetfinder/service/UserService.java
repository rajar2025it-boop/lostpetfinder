package com.lostpetfinder.service;

import com.lostpetfinder.dto.UserRequest;
import com.lostpetfinder.dto.UserResponse;
import com.lostpetfinder.entity.User;
import com.lostpetfinder.exception.BusinessRuleException;
import com.lostpetfinder.exception.ResourceNotFoundException;
import com.lostpetfinder.mapper.ReportMapper;
import com.lostpetfinder.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) { this.userRepository = userRepository; }

    @Transactional
    public UserResponse create(UserRequest req) {
        if (userRepository.existsByEmailIgnoreCase(req.email())) {
            throw new BusinessRuleException("A user with email " + req.email() + " already exists");
        }
        User u = new User();
        u.setName(req.name().trim());
        u.setEmail(req.email().trim().toLowerCase());
        u.setPhone(req.phone());
        return ReportMapper.toResponse(userRepository.save(u));
    }

    @Transactional(readOnly = true)
    public UserResponse get(Long id) { return ReportMapper.toResponse(find(id)); }

    @Transactional(readOnly = true)
    public List<UserResponse> list() {
        return userRepository.findAll().stream().map(ReportMapper::toResponse).toList();
    }

    public User find(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }
}
