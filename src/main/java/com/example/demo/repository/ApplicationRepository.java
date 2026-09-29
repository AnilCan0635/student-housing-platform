package com.example.demo.repository;

import com.example.demo.entity.Application;
import com.example.demo.entity.Listing;
import com.example.demo.entity.Student; 
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    
    // İlanın sahibine (Landlord ID'sine) göre gelen başvuruları bul
    List<Application> findByListing_LandlordId(Long landlordId);
    /* SQL - JOIN
     * * SELECT a.* FROM applications a
     * LEFT JOIN listings l ON a.listing_id = l.listing_id
     * WHERE l.landlord_id = ?;
     */

    // Bir öğrencinin kendi yaptığı başvuruları listelemesi
    List<Application> findByStudentId(Long studentId);
    /* SQL 
     * SELECT * FROM applications WHERE student_id = ?;
     */

    boolean existsByStudentAndListing(Student student, Listing listing);
    /*  SQL
     * SELECT count(*) FROM applications 
     * WHERE student_id = ? AND listing_id = ? 
     * LIMIT 1;
     */
    
    // Belirli bir ilana yapılan başvuruları bul. (İlan silerken lazım bana)
    List<Application> findByListing_ListingId(Long listingId);
    /* SQL 
     * SELECT * FROM applications WHERE listing_id = ?;
     */
}