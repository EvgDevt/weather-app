package org.project.capstone.weather.api.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.project.capstone.weather.api.dto.UserCreateEditRequest;
import org.project.capstone.weather.api.dto.UserResponse;
import org.project.capstone.weather.api.dto.filter.QPredicate;
import org.project.capstone.weather.api.dto.filter.UserSearchFilter;
import org.project.capstone.weather.api.entity.LocationEntity;
import org.project.capstone.weather.api.entity.UserEntity;
import org.project.capstone.weather.api.entity.UserLocationsEntity;
import org.project.capstone.weather.api.excpetion.LocationNotFoundException;
import org.project.capstone.weather.api.mapper.UserCreateEditMapper;
import org.project.capstone.weather.api.repository.LocationRepository;
import org.project.capstone.weather.api.repository.UserLocationRepository;
import org.project.capstone.weather.api.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.project.capstone.weather.api.entity.QUserEntity.userEntity;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    private final LocationRepository locationRepository;

    private final UserLocationRepository userLocationRepository;

    private final UserCreateEditMapper userMapper;


    public List<UserResponse> getAllUsers(UserSearchFilter filter, Pageable pageable) {
        Predicate predicate = QPredicate.builder()
                .add(filter.email(), userEntity.email::containsIgnoreCase)
                .add(filter.lastname(), userEntity.lastname::containsIgnoreCase)
                .add(filter.createdAt(), localDate -> userEntity.createdAt.after(localDate.atStartOfDay()))
                .build();

        return userRepository.findAll(predicate, pageable)
                .stream()
                .map(userMapper::userToUserResponse)
                .toList();
    }

    public Optional<UserResponse> getUserById(Integer id) {
        return userRepository.findById(id)
                .map(userMapper::userToUserResponse);
    }

    public Optional<UserResponse> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::userToUserResponse);
    }

    public String getUsernameById(Integer id) {
        Optional<UserEntity> userEntityOpt = userRepository.findById(id);

        return userEntityOpt
                .map(UserEntity::getEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public void addLocationToUser(Integer userId, String cityName) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        LocationEntity location = locationRepository.findLocationEntityByCity(cityName).orElseThrow(LocationNotFoundException::new);

        UserLocationsEntity userLocation = UserLocationsEntity.builder()
                .user(user)
                .location(location)
                .build();

        userLocationRepository.save(userLocation);

    }

    @Transactional
    public UserResponse createUser(UserCreateEditRequest createEditRequest) {
        UserEntity userEntity = userMapper.userCreateEditToUser(createEditRequest);
        UserEntity savedUser = userRepository.save(userEntity);

        return userMapper.userToUserResponse(savedUser);
    }

    @Transactional
    public Optional<UserResponse> updateUser(Integer id, UserCreateEditRequest createEditRequest) {
        return userRepository.findById(id)
                .map(entity -> userMapper.update(createEditRequest, entity))
                .map(userRepository::saveAndFlush)
                .map(userMapper::userToUserResponse);
    }

    @Transactional
    public boolean deleteUser(Integer id) {
        return userRepository.findById(id)
                .map(entity -> {
                    userRepository.delete(entity);
                    return true;
                }).orElse(false);
    }
}
