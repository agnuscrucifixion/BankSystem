package com.example.accounts.controllers;

import com.example.accounts.dto.FeeDTO;
import com.example.accounts.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    @PostMapping("/configs")
    public ResponseEntity<FeeDTO> setFee(@RequestBody FeeDTO feeDTO) {

        return adminService.setFee(feeDTO);
    }
}
