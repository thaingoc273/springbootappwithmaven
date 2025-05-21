package com.example.demotestmaven.controller;

import com.example.demotestmaven.dto.RoleDTO;
import com.example.demotestmaven.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<RoleDTO>> getRolesByUsername(@PathVariable String username) {
        return ResponseEntity.ok(roleService.getRolesByUsername(username));
    }

    @PostMapping("/user/{username}")
    public ResponseEntity<RoleDTO> assignRole(
            @RequestHeader("X-Current-User") String currentUsername,
            @PathVariable String username,
            @RequestBody RoleDTO roleDTO) {
        return ResponseEntity.ok(roleService.assignRole(currentUsername, username, roleDTO));
    }

    @DeleteMapping("/user/{username}/{rolecode}")
    public ResponseEntity<Void> removeRole(
            @RequestHeader("X-Current-User") String currentUsername,
            @PathVariable String username,
            @PathVariable String rolecode) {
        roleService.removeRole(currentUsername, username, rolecode);
        return ResponseEntity.ok().build();
    }
} 