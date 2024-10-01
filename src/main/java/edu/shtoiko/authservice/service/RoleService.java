package edu.shtoiko.authservice.service;

import edu.shtoiko.authservice.model.Role;
import edu.shtoiko.authservice.model.dto.RoleRequest;

import java.util.List;

public interface RoleService {
    List<Role> getRolesByNames(List<String> roleNames);

    List<Role> getAllRoles();

    Role create(RoleRequest role);
}
