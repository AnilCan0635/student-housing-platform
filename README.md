# Student Housing & Roommate Matching Platform - Backend & Web UI

A full-stack housing and roommate matching platform designed to streamline student rentals. The system allows landlords to publish and manage listings, while students can search with dynamic filters, bookmark properties, and submit rental applications.

---

## Tech Stack
- Backend: Java 17, Spring Boot 3.5.x
- Persistence: Spring Data JPA, Hibernate
- Database: PostgreSQL
- Frontend: HTML5, Bootstrap 5, Vanilla JavaScript (Fetch API), FontAwesome
- Build Tool: Maven
- Utilities: Lombok

---

## Features
- Role-based Authentication: Dedicated registration and login workflows for Students and Landlords.
- Dynamic Property Search: Multi-criteria filtering by city, room type, budget range, and amenities.
- Application Workflow: Students can submit applications; landlords can review, approve, or reject them.
- Favorites System: Interactive bookmarking engine for students to save prospective properties.
- Listing Lifecycle: Landlords can create, update, filter, and delete their housing posts.
- Profile Management: Role-specific profile management for academic and contact information.

---

## Local Setup

### Prerequisites
- Java Development Kit (JDK) 17+
- Apache Maven 3.8+
- PostgreSQL 14+

### Installation & Run
1. Clone the repository:
   git clone https://github.com/your-username/student-housing.git
   cd student-housing

2. Configure PostgreSQL:
   Make sure a database named studenthousing is created in your PostgreSQL instance:
   CREATE DATABASE studenthousing;

   Update database credentials in src/main/resources/application.properties if needed.

3. Build and start the application:
   mvn clean install
   mvn spring-boot:run

4. Access the application:
   Open your browser and navigate to:
   http://localhost:8080/

---

## API Endpoints Summary

- POST /api/register - Register a new student or landlord
- POST /api/login - Authenticate student or landlord
- GET /api/search - Dynamic listing search with query filters
- POST /api/listings - Create a new property listing
- PUT /api/listings/{id} - Update an existing listing
- DELETE /api/listings/{id} - Delete a listing and associated applications
- POST /api/apply - Apply to a housing listing
- GET /api/applications/{studentId} - Fetch applications submitted by a student
- GET /api/landlord/applications/{landlordId} - Fetch incoming applications for a landlord
- PUT /api/applications/{appId}/status - Update application status
- POST /api/favorites/{studentId}/{listingId} - Toggle listing favorite status
- GET /api/favorites/{studentId} - List favorite properties of a student