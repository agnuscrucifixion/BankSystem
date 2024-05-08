package com.example.accounts.services;

import com.example.accounts.config.FeeConfig;
import com.example.accounts.dto.FeeDTO;
import com.example.accounts.entities.Fee;
import com.example.accounts.repositories.FeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AdminService {

    private final FeeRepository feeRepository;

    @Autowired
    public AdminService(FeeRepository feeRepository) {
        this.feeRepository = feeRepository;
    }
    public ResponseEntity<FeeDTO> setFee(FeeDTO feeDTO) {
        if (feeDTO.getFee() < 0 || feeDTO.getFee() > 1) {
            return ResponseEntity.badRequest().build();
        }
        Fee fee = new Fee(feeDTO);
        feeRepository.save(fee);
        FeeConfig.fee = feeDTO.getFee();

        return ResponseEntity.ok(feeDTO);
    }

    public void readFromDB() {
        Optional<Fee> feeOpt = feeRepository.getLatestFee();
        if (feeOpt.isPresent()) {
            Fee fee = feeOpt.get();
            FeeConfig.fee = fee.getFee();
        } else {
            FeeConfig.fee = 0;
        }
    }
}
