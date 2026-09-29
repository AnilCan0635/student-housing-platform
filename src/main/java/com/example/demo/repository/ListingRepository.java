package com.example.demo.repository;

import com.example.demo.entity.Listing;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
	List<Listing> findByLandlordId(Long landlordId); // Ev Sahibi kendi ilanları
	/*  SQL
     * SELECT * FROM listings WHERE landlord_id = ?;
     * SELECT * FROM listing_images WHERE listing_id = ?)
     */
}

