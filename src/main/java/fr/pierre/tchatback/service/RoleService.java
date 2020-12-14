package fr.pierre.tchatback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.pierre.tchatback.entity.Role;
import fr.pierre.tchatback.enums.RoleName;
import fr.pierre.tchatback.respository.RoleRepository;

import java.util.Optional;

@Service
@Transactional
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    public Optional<Role> getByRoleName(RoleName roleName){
        return roleRepository.findByRoleName(roleName);
    }

    public boolean existsRoleName(RoleName roleName){
        return roleRepository.existsByRoleName(roleName);
    }

    public void save(Role role){
        roleRepository.save(role);
    }
}
