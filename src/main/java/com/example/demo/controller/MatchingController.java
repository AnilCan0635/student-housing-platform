package com.example.demo.controller;

import com.example.demo.entity.Application;
import com.example.demo.entity.Listing;
import com.example.demo.entity.Student;
import com.example.demo.entity.Landlord;
import com.example.demo.service.MatchingService;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.ListingRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.LandlordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MatchingController {
	
    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private MatchingService matchingService;
    @Autowired private ListingRepository listingRepository;
    
    @Autowired private StudentRepository studentRepository;
    @Autowired private LandlordRepository landlordRepository;



    // 1. DİNAMİK ARAMA API'Sİ
    @GetMapping("/search")
    public List<Listing> search(@RequestParam(required = false) String city,
                                @RequestParam(required = false) Double maxPrice,
                                @RequestParam(required = false) String roomType,
                                @RequestParam(required = false) String amenities,
                                @RequestParam(required = false) Long landlordId) {
        return matchingService.searchListings(city, maxPrice, roomType, amenities, landlordId);
        /* * SQL 
         *  SELECT * FROM listings;
         * Mevcut verim az o yüzden java stream ile filtreleme.
         * SELECT * FROM landlords WHERE id = ?;
         */
    }

    // 2. EV SAHİBİ İLAN EKLEME
    @PostMapping("/listings")
    public Listing createListing(@RequestBody Listing listing) {
        return listingRepository.save(listing);
        /* * SQL
         * INSERT INTO listings (title, city, rent_amount, room_type, amenities, landlord_id) 
         * VALUES (?, ?, ?, ?, ?, ?);
         *
         * INSERT INTO listing_images (listing_id, image_data) VALUES (?, ?);
         */
    }
    
    // 3. BAŞVURU YAPMA 
    @PostMapping("/apply")
    public ResponseEntity<?> makeApplication(@RequestBody Map<String, Long> payload) {
        Long studentId = payload.get("studentId");
        Long listingId = payload.get("listingId");
        
        /* * SQL Kontrol
         * SELECT * FROM students WHERE id = ?;
         * SELECT * FROM listings WHERE id = ?;
         */
        
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Öğrenci bulunamadı"));
        
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("İlan bulunamadı"));

        // 1. KONTROL: Zaten başvurmuş mu?
        /* * [SQL KARŞILIĞI - Count]
         * SELECT count(*) FROM applications 
         * WHERE student_id = ? AND listing_id = ?;
         */
        boolean alreadyApplied = applicationRepository.existsByStudentAndListing(student, listing);
        if (alreadyApplied) {
            return ResponseEntity.badRequest().body("Bu ilana zaten başvurdunuz!");
        }

        // 2. KAYIT
        Application app = new Application();
        app.setStudent(student); 
        app.setListing(listing);
        app.setStatus("Beklemede");
        app.setApplicationDate(LocalDate.now());
        
        /* * SQL
         * INSERT INTO applications (student_id, listing_id, status, application_date) 
         * VALUES (?, ?, 'Beklemede', ?);
         */
        
        applicationRepository.save(app);
        return ResponseEntity.ok("Başvuru başarılı");
    }

    // 4. EV SAHİBİNE GELEN BAŞVURULARI LİSTELE
    @GetMapping("/landlord/applications/{landlordId}")
    public List<Application> getLandlordApplications(@PathVariable Long landlordId) {
    	/* * SQL - JOIN 
         * Başvuru tablosunda 'landlord_id' sütunu yok.
         * * SELECT a.* * FROM applications a 
         * LEFT JOIN listings l ON a.listing_id = l.listing_id 
         * WHERE l.landlord_id = ?;
         */
        return applicationRepository.findByListing_LandlordId(landlordId);
    }

    // 5. BAŞVURU DURUMUNU GÜNCELLE (ONAYLA / REDDET)
    @PutMapping("/applications/{appId}/status")
    public Application updateStatus(@PathVariable Long appId, @RequestBody String newStatus) {
        Application app = applicationRepository.findById(appId).orElseThrow();
        /* * SQL- Select
         * SELECT * FROM applications WHERE id = ?;
         */
        String cleanStatus = newStatus.replace("\"", ""); 
        app.setStatus(cleanStatus); 
        
        return applicationRepository.save(app);
        /* * SQL - Update
         * UPDATE applications 
         * SET status = ? 
         * WHERE id = ?;
         */
    }

    // 6. ÖĞRENCİNİN BAŞVURULARINI GETİR
    @GetMapping("/applications/{studentId}")
    public List<Application> getMyApplications(@PathVariable Long studentId) {
        return applicationRepository.findByStudentId(studentId);
        /* * SQL 
         * SELECT * FROM applications WHERE student_id = ?;
         */
    }
    
    // 7. EV SAHİBİNİN KENDİ İLANLARINI GETİR
    @GetMapping("/listings/landlord/{landlordId}")
    public List<Listing> getLandlordListings(@PathVariable Long landlordId) {
        return listingRepository.findByLandlordId(landlordId);
        /* * SQL
         * SELECT * FROM listings WHERE landlord_id = ?;
         */
    }

    // 8. İLAN SİLME
    @DeleteMapping("/listings/{id}")
    public ResponseEntity<?> deleteListing(@PathVariable Long id) {
        try {
            List<Application> basvurular = applicationRepository.findByListing_ListingId(id);
            /* * SQL - Cascading Logic
             *  SELECT * FROM applications WHERE listing_id = ?;
             */
            if (basvurular != null && !basvurular.isEmpty()) {
                applicationRepository.deleteAll(basvurular);
                /* DELETE FROM applications WHERE listing_id = ?; */
            }
            listingRepository.deleteById(id);
            /* DELETE FROM listings WHERE listing_id = ?; */
            return ResponseEntity.ok("İlan ve ilişkili başvurular başarıyla silindi.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Silme işlemi başarısız: " + e.getMessage());
        }
    }

    // 9. İLAN GÜNCELLEME
    @PutMapping("/listings/{id}")
    public ResponseEntity<?> updateListing(@PathVariable Long id, @RequestBody Listing listingDetails) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("İlan bulunamadı"));
        /*  SQL
         * SELECT * FROM listings WHERE listing_id = ?;
         */

        if (!listing.getLandlordId().equals(listingDetails.getLandlordId())) {
            return ResponseEntity.status(403).body("Bu ilanı düzenleme yetkiniz yok!");
        }

        listing.setTitle(listingDetails.getTitle());
        listing.setCity(listingDetails.getCity());
        listing.setRentAmount(listingDetails.getRentAmount());
        listing.setRoomType(listingDetails.getRoomType());
        listing.setAmenities(listingDetails.getAmenities());
        // Resimleri de güncellemek istersen:
        if(listingDetails.getImages() != null) {
            listing.setImages(listingDetails.getImages());
        }
        
        listingRepository.save(listing);
        /* * [SQL KARŞILIĞI - Update]
         * UPDATE listings 
         * SET title=?, city=?, rent_amount=?, amenities=? ...
         * WHERE listing_id = ?;
         */
        return ResponseEntity.ok(listing);
    }
    
    // SİLME İŞLEMLERİ 

    // 10. ÖĞRENCİ SİLME
    @DeleteMapping("/students/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        try {
            List<Application> basvurular = applicationRepository.findByStudentId(id);
            applicationRepository.deleteAll(basvurular);
            /*  SQL - İlişkili Veri Temizliği
             * DELETE FROM applications WHERE student_id = ?;
             */

            // Öğrenciyi sil
            studentRepository.deleteById(id);
            /*DELETE FROM students WHERE id = ?; */
            return ResponseEntity.ok("Öğrenci silindi.");
        } catch(Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    // 11. EV SAHİBİ SİLME
    @DeleteMapping("/landlords/{id}")
    public ResponseEntity<?> deleteLandlord(@PathVariable Long id) {
        try {
            // Ev sahibinin ilanlarını bul
            List<Listing> ilanlar = listingRepository.findByLandlordId(id);
            /* SELECT * FROM listings WHERE landlord_id = ?; */
            for (Listing ilan : ilanlar) {
                // İlanlara ait başvuruları sil
                List<Application> basvurular = applicationRepository.findByListing_ListingId(ilan.getListingId());          
                applicationRepository.deleteAll(basvurular);
                /*  DELETE FROM applications WHERE listing_id = ?; */
            }
            // İlanları sil
            listingRepository.deleteAll(ilanlar);
            /* DELETE FROM listings WHERE landlord_id = ?; */
            
            // Ev sahibini sil
            landlordRepository.deleteById(id);
            /* DELETE FROM landlords WHERE id = ?; */
            return ResponseEntity.ok("Ev sahibi ve tüm ilanları silindi.");
        } catch(Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }
 // 12. ÖĞRENCİ PROFİL GÜNCELLEME 
    @PutMapping("/students/{id}")
    public ResponseEntity<?> updateStudentProfile(@PathVariable Long id, @RequestBody Student details) {
        Student s = studentRepository.findById(id).orElseThrow();
        /*  SELECT * FROM students WHERE id = ?; */
        
        if(details.getSchoolName() != null) s.setSchoolName(details.getSchoolName());
        if(details.getDepartment() != null) s.setDepartment(details.getDepartment());
        if(details.getStudentNumber() != null) s.setStudentNumber(details.getStudentNumber());
        if(details.getContactInfo() != null) s.setContactInfo(details.getContactInfo());
        
        studentRepository.save(s);
        
        /*  [SQL - Update]
         * UPDATE students 
         * SET school_name=?, department=?, student_number=?, contact_info=?
         * WHERE id = ?;
         */
        return ResponseEntity.ok(s);
    }
   
 // 13. EV SAHİBİ PROFİL GÜNCELLEME
    @PutMapping("/landlords/{id}")
    public ResponseEntity<?> updateLandlordProfile(@PathVariable Long id, @RequestBody Landlord details) {
        Landlord l = landlordRepository.findById(id).orElseThrow();
        /*  SELECT * FROM landlords WHERE id = ?; */
        
        if(details.getContactInfo() != null) l.setContactInfo(details.getContactInfo());
        if(details.getTaxNumber() != null) l.setTaxNumber(details.getTaxNumber());

        landlordRepository.save(l);
        /*  UPDATE landlords SET contact_info=?, tax_number=? WHERE id = ?; */
        return ResponseEntity.ok(l);
    }
}