package com.example.demo.repository;
import com.example.demo.entity.Student;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByUsername(String username); // Kayıt Aşaması
    /* SQL 
     * SELECT * FROM students WHERE username = ?;
     */
    Optional<Student> findByUsernameAndPassword(String username, String password); // Login
    /*  SQL 
     * SELECT * FROM students 
     * WHERE username = ? AND password = ?;
     */
}