package com.example.demo.controller;

import com.example.demo.entity.Favorite;
import com.example.demo.entity.Listing;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ListingRepository listingRepository;

    // Favori Ekle / Çıkar (Toggle)
    @PostMapping("/{studentId}/{listingId}")
    @Transactional
    public String toggleFavorite(@PathVariable Long studentId, @PathVariable Long listingId) {
        if (favoriteRepository.existsByStudentIdAndListingId(studentId, listingId)) {
            favoriteRepository.deleteByStudentIdAndListingId(studentId, listingId);
            return "Favorilerden çıkarıldı.";
        } else {
            Favorite fav = new Favorite();
            fav.setStudentId(studentId);
            fav.setListingId(listingId);
            favoriteRepository.save(fav);
            return "Favorilere eklendi.";
        }
    }

    // Öğrencinin Favori İlanlarını Getir
    @GetMapping("/{studentId}")
    public List<Listing> getFavorites(@PathVariable Long studentId) {
        List<Long> listingIds = favoriteRepository.findByStudentId(studentId).stream()
                .map(Favorite::getListingId)
                .collect(Collectors.toList());
        return listingRepository.findAllById(listingIds);
    }
    
    // Öğrencinin favori ID listesini getir
    @GetMapping("/{studentId}/ids")
    public List<Long> getFavoriteIds(@PathVariable Long studentId) {
        return favoriteRepository.findByStudentId(studentId).stream()
                .map(Favorite::getListingId)
                .collect(Collectors.toList());
    }
}