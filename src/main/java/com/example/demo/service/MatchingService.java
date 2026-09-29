package com.example.demo.service;

import com.example.demo.entity.Listing;
import com.example.demo.entity.Landlord; // DİKKAT: User yerine Landlord geldi
import com.example.demo.repository.ListingRepository;
import com.example.demo.repository.LandlordRepository; // DİKKAT: UserRepository yerine LandlordRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    @Autowired
    private ListingRepository listingRepository;
    
    @Autowired
    private LandlordRepository landlordRepository; 

    public List<Listing> searchListings(String city, Double maxPrice, String roomType, String amenities, Long landlordId) {
        return listingRepository.findAll().stream()
            // 1. Şehir Filtresi
            .filter(l -> (city == null || city.isEmpty() || l.getCity().equalsIgnoreCase(city)))
            // 2. Fiyat Filtresi
            .filter(l -> (maxPrice == null || l.getRentAmount() <= maxPrice))
            // 3. Oda Tipi Filtresi
            .filter(l -> (roomType == null || roomType.isEmpty() || l.getRoomType().equalsIgnoreCase(roomType)))
            // 4. İlan Sahibi Filtresi
            .filter(l -> (landlordId == null || (l.getLandlordId() != null && l.getLandlordId().equals(landlordId))))
            // 5. Özellik Filtresi
            .filter(l -> {
                if (amenities == null || amenities.isEmpty()) return true;
                if (l.getAmenities() == null) return false;
                String[] arananlar = amenities.split(",");
                for (String aranan : arananlar) {
                    if (!l.getAmenities().contains(aranan.trim())) return false;
                }
                return true;
            })
            // İSİMLERİ DOLDURMA İŞLEMİ
            .peek(l -> {
                if (l.getLandlordId() != null) {
                    Landlord landlord = landlordRepository.findById(l.getLandlordId()).orElse(null);
                    if(landlord != null) {
                        l.setLandlordName(landlord.getUsername());
                    } else {
                        l.setLandlordName("Bilinmeyen Ev Sahibi");
                    }
                }
            })
            // ---------------------------------------
            .collect(Collectors.toList());
    }
}