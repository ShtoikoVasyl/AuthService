package edu.shtoiko.authservice.service.implementation;

import edu.shtoiko.authservice.model.Role;
import edu.shtoiko.authservice.model.dto.RoleRequest;
import edu.shtoiko.authservice.repository.RoleRepository;
import edu.shtoiko.authservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    @Override
    public List<Role> getRolesByNames(List<String> roleNames) {
        return roleRepository.findAllByNameIn(roleNames);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role create(RoleRequest role) {
        return roleRepository.save(new Role(role.getName()));
    }
}
