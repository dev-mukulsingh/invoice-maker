package com.invoicemaker.service;

import com.invoicemaker.dto.UserProfileDTO;
import com.invoicemaker.entity.User;
import com.invoicemaker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User getOrCreateDefaultUser() {
        return userRepository.findFirstByOrderByIdAsc().orElseGet(() -> {
            User defaultUser = User.builder()
                    .businessName("Studio Tonari Studio & Co.")
                    .email("billing@tonari-studio.com")
                    .phone("+91 98765 43210")
                    .gstNumber("27AABCT3518Q1ZY")
                    .address("Suite 108, Hinoki Woods Creative Studio, Pune 411001, MH, India")
                    .build();
            return userRepository.save(defaultUser);
        });
    }

    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfile() {
        User user = getOrCreateDefaultUser();
        return toDTO(user);
    }

    @Transactional
    public UserProfileDTO updateUserProfile(UserProfileDTO dto) {
        User user = getOrCreateDefaultUser();
        user.setBusinessName(dto.getBusinessName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGstNumber(dto.getGstNumber());
        user.setAddress(dto.getAddress());
        user = userRepository.save(user);
        return toDTO(user);
    }

    public UserProfileDTO toDTO(User user) {
        if (user == null) return null;
        return UserProfileDTO.builder()
                .id(user.getId())
                .businessName(user.getBusinessName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .gstNumber(user.getGstNumber())
                .address(user.getAddress())
                .build();
    }
}
