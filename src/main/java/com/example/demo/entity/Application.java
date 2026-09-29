package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student; 
    
    @ManyToOne
    @JoinColumn(name = "listing_id")
    private Listing listing;
    
    private String status; 
    private LocalDate applicationDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    
    public void setStudent(Student student) { this.student = student; }

    public Listing getListing() { return listing; }
    public void setListing(Listing listing) { this.listing = listing; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDate applicationDate) { this.applicationDate = applicationDate; }
}