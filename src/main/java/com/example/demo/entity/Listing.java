package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "listings")
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "listing_id")
    private Long listingId;
    
    private String title;
    private String city;
    
    @Column(name = "rent_amount")
    private Double rentAmount;
    
    @Column(name = "room_type")
    private String roomType;
    
    @Column(name = "landlord_id")
    private Long landlordId; 
    // -----------------------------

    public Long getListingId() { return listingId; }
    public void setListingId(Long listingId) { this.listingId = listingId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public Double getRentAmount() { return rentAmount; }
    public void setRentAmount(Double rentAmount) { this.rentAmount = rentAmount; }
    
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public Long getLandlordId() { return landlordId; }
    public void setLandlordId(Long landlordId) { this.landlordId = landlordId; }
    
    @Column(name = "amenities")
    private String amenities; 

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }
    
    @Transient 
    private String landlordName;

    public String getLandlordName() { return landlordName; }
    public void setLandlordName(String landlordName) { this.landlordName = landlordName; }
    
    @ElementCollection
    @CollectionTable(name = "listing_images", joinColumns = @JoinColumn(name = "listing_id"))
    @Column(name = "image_data", columnDefinition = "TEXT")
    private List<String> images = new ArrayList<>();

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
}