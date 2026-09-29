package com.example.demo.repository;
import com.example.demo.entity.Landlord;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LandlordRepository extends JpaRepository<Landlord, Long> {
    Landlord findByUsername(String username); // Kayıt
    /* SQL 
     * SELECT * FROM landlords WHERE username = ?;
     */
    Optional<Landlord> findByUsernameAndPassword(String username, String password); //Login
    /*  SQL
     * Ev sahibi giriş kontrolü:
     * SELECT * FROM landlords 
     * WHERE username = ? AND password = ?;
     */
}