package com.example.demo.repository;

import com.example.demo.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
	// Öğrencinin favorilediği evlerin ID'leri
    List<Favorite> findByStudentId(Long studentId);
    /* SQL
     * SELECT * FROM favorites WHERE student_id = ?;
     */
    
    // Kalp dolu mu boş mu kontrolünde kullanıyorum.
    boolean existsByStudentIdAndListingId(Long studentId, Long listingId);
    /* SQL
     * SELECT count(*) FROM favorites 
     * WHERE student_id = ? AND listing_id = ?;
     */
    
    // Favoriden çıkarma
    void deleteByStudentIdAndListingId(Long studentId, Long listingId);
    /* SQL
     * DELETE FROM favorites 
     * WHERE student_id = ? AND listing_id = ?;
     */
}