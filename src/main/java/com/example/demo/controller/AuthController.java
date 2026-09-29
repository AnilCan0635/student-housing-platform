package com.example.demo.controller;

import com.example.demo.entity.Landlord;
import com.example.demo.entity.Student;
import com.example.demo.repository.LandlordRepository;
import com.example.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class AuthController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private LandlordRepository landlordRepository;

    //  KAYIT OLMA (REGISTER) ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        String role = payload.get("role");
        String username = payload.get("username");
        String password = payload.get("password");
        String contact = payload.get("contactInfo");

     // TELEFON KONTROLÜ (ZORUNLU)
     if (contact == null || contact.trim().isEmpty()) {
         return ResponseEntity.badRequest().body("Lütfen telefon numaranızı giriniz (Zorunlu).");
     }

        // Basit validasyon
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Kullanıcı adı ve şifre zorunludur.");
        }

        if ("STUDENT".equals(role)) {
            // Aynı isimde öğrenci var mı?
            if (studentRepository.findByUsername(username) != null) {
            	/* SQL
                 * SELECT * FROM students WHERE username = ?;
                 */
                return ResponseEntity.badRequest().body("Bu kullanıcı adı zaten alınmış (Öğrenci).");
            }
            // Yoksa kaydet
            Student s = new Student();
            s.setUsername(username);
            s.setPassword(password);
            s.setContactInfo(contact);
            studentRepository.save(s);
            /* SQL
             * INSERT INTO students (username, password, contact_info) VALUES (?, ?, ?);
             */
        } 
        else if ("LANDLORD".equals(role)) {
            // Aynı isimde ev sahibi var mı?
            if (landlordRepository.findByUsername(username) != null) {
            	/* SQL
                 * SELECT * FROM landlords WHERE username = ?;
                 */
                return ResponseEntity.badRequest().body("Bu kullanıcı adı zaten alınmış (Ev Sahibi).");
            }
            // Yoksa kaydet
            Landlord l = new Landlord();
            l.setUsername(username);
            l.setPassword(password);
            l.setContactInfo(contact);
            landlordRepository.save(l);
            /* SQL
             * INSERT INTO landlords (username, password, contact_info) VALUES (?, ?, ?);
             */
        } 
        else {
            return ResponseEntity.badRequest().body("Geçersiz Rol Seçimi!");
        }

        return ResponseEntity.ok("Kayıt Başarılı! Giriş yapabilirsiniz.");
    }

    // GİRİŞ YAPMA (LOGIN)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        // 1. Önce Öğrencilerde Ara
        Optional<Student> student = studentRepository.findByUsernameAndPassword(username, password);
        /* SQL
         * SELECT * FROM students WHERE username = ? AND password = ?;
         */
        if (student.isPresent()) {
         Student s = student.get();
         s.setPassword(null); // Şifreyi gizle
         return ResponseEntity.ok(s);
        }   
        // 2. Yoksa Ev Sahiplerinde Ara
        Optional<Landlord> landlord = landlordRepository.findByUsernameAndPassword(username, password);
        /* SQL
         * SELECT * FROM landlords WHERE username = ? AND password = ?;
         */
        if (landlord.isPresent()) {
         Landlord l = landlord.get();
         l.setPassword(null); // Şifreyi gizle
         return ResponseEntity.ok(l);
        }
        
        

        return ResponseEntity.status(401).body("Giriş başarısız!");
    }
 

    // --- YARDIMCI METOD
    @SuppressWarnings("unused")
	private Map<String, Object> createLoginResponse(Long id, String username, String role, String contact) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("username", username);
        response.put("role", role);
        response.put("contactInfo", contact);
        return response;
    }
}