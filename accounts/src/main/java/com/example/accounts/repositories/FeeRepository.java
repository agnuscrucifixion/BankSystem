package com.example.accounts.repositories;

import com.example.accounts.entities.Fee;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FeeRepository extends JpaRepository<Fee, Integer> {
    @Query(value = "SELECT * FROM fees ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<Fee> getLatestFee();
}
