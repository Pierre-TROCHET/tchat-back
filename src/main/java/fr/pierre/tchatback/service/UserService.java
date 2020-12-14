package fr.pierre.tchatback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.pierre.tchatback.entity.User;
import fr.pierre.tchatback.respository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    UserRepository userRepository;

    public Optional<User> getByProviderId(String providerId){
        return userRepository.findByProviderId(providerId);
    }

    public boolean existsProviderId(String providerId){
        return userRepository.existsByProviderId(providerId);
    }

    public User save(User user){
        return userRepository.save(user);
    }
    
    public List<User> findAllUsers(){
        return userRepository.findAll();
    }
    
    public User getById(int id){
        return userRepository.findById(id);
    }
}
