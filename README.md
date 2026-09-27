# 🏮 INVOICE KOUBOU (請求工房)
### Multi-Template Invoice Generator & Billing Management System
*Production-Ready Final Year Diploma Project*

---

## 📖 Overview
**Invoice Koubou (請求工房)** is a full-stack billing and invoice management system built with **Spring Boot 3**, **Spring Data JPA**, **MariaDB / MySQL**, **Thymeleaf**, **Flying Saucer (OpenPDF)**, and an embedded single-page application (SPA) styled in a **strict 90s retro anime / Studio Ghibli cel-shaded paper aesthetic**.

The system features **10 distinct selectable invoice layout engines** that render pixel-perfect live previews in the browser and export to vector-grade downloadable PDF documents.

---

## 🎨 Retro Anime Aesthetic & Design Specifications
- **Color Palette**:
  - Parchment Backgrounds: `#FAF7F2`, `#F4EAD4`
  - Charcoal Ink Borders & Text: `#2B2D42`
  - Soft Matcha Green: `#5B8266` (Paid status, positive totals)
  - Terracotta Rust: `#D46A43` (Unpaid status, primary accents)
  - Retro Denim Blue: `#3A506B` (Navigation headers, blueprint lines)
  - Mustard / Ochre: `#E9C46A` (Tax tags, highlight badges)
- **Hard-Edged Borders & Tactile Cel Shadows**:
  - Hard borders: `border-2 border-[#2B2D42]`
  - Solid offset shadows: `box-shadow: 4px 4px 0px #2B2D42`
  - Tactile button press animations: `active:translate(2px, 2px)`
  - Traditional Japanese Hanko seal stamps (`領収済 PAID`, `未払 UNPAID`)

---

## 🗂️ 10 Selectable Design Templates
1. **Classic Ghibli Parchment**: Warm cream paper `#FBF8F1`, dark charcoal double borders `#2B2D42`, and rustic ink stamps.
2. **Vintage Ledger**: Retro accounting sheet with light green grid rules, boxed summary lines, and monospace typography.
3. **Cozy Cottage**: Matcha green `#5B8266` headers, soft wood tones, curved borders, and organic styling.
4. **Tokyo Monospace**: Clean typewriter font, dashed borders `border-dashed`, and receipt ticket layout.
5. **Sunset Terracotta**: Earthen warm terracotta `#D46A43` banner headers and bold rule lines.
6. **Pastel Kraft**: Kraft cardboard background `#F4EAD4` with muted contrast table bands and postage stamp stickers.
7. **Japanese Hanko Stamp**: Minimalist Japanese typography with authentic red circular Hanko seal stamp (`【 領収済 】` / `【 請求書 】`).
8. **Artisan Blueprint**: Technical grid layout with warm denim blue `#3A506B` header blocks and dimension marks.
9. **Editorial Notebook**: Left-margin crimson accent binding line with ruled notebook rows.
10. **Modern Minimal Chic**: High-contrast clean black-on-ivory `#111111` on `#FCFAF7` layout with bold stark hairlines.

---

## ⚙️ Technology Stack
- **Backend Framework**: Spring Boot 3.3.4 (Java 17+)
- **Persistence & ORM**: Spring Data JPA & Hibernate 6
- **Database**: MariaDB / MySQL (with H2 in-memory profile for instant zero-configuration demo/testing)
- **PDF Generation**: Flying Saucer (`org.xhtmlrenderer:flying-saucer-pdf-openpdf:9.3.1`) + LibrePDF OpenPDF
- **Template Engine**: Thymeleaf (XHTML strict compilation for PDF engine)
- **Frontend**: Single-Page Application (HTML5, Vanilla JavaScript, Tailwind CSS via CDN, Google Fonts)

---

## 🗄️ Database Schema & Entities
1. **`users` (Business Issuer)**:
   - `id`, `business_name`, `email`, `gst_number`, `address`, `phone`
2. **`clients` (Billed Client)**:
   - `id`, `client_name`, `client_email`, `client_phone`, `billing_address`, `gst_number`, `created_at`
3. **`invoices`**:
   - `id`, `invoice_number` (unique), `invoice_date`, `due_date`, `subtotal`, `cgst` (9%), `sgst` (9%), `grand_total`, `status` (`DRAFT`, `PAID`, `UNPAID`), `selected_template` (1-10), `notes`, `client_id`, `user_id`, `created_at`
4. **`invoice_items`**:
   - `id`, `description`, `quantity`, `unit_price`, `tax_rate`, `item_total`, `invoice_id`

---

## 🚀 How to Run the Application

### Option A: Running with In-Memory H2 (Instant, Zero Setup)
Run directly from terminal without setting up MySQL:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```
Access the application at:
👉 **http://localhost:8080**

### Option B: Running with MariaDB / MySQL
1. Ensure your local MySQL / MariaDB server is active on port `3306`.
2. Configure credentials in `src/main/resources/application.properties` (defaults to `root`/`root` on database `invoicedb`):
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/invoicedb?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=root
   spring.jpa.hibernate.ddl-auto=update
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### Option C: Build and Run Standalone JAR
```bash
mvn clean package
java -jar target/invoice-maker-1.0.0.jar --spring.profiles.active=h2
```

---

## 📡 REST API Documentation

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/dashboard/stats` | Retrieve revenue, invoice counts, and recent transactions |
| `GET` | `/api/invoices` | List all invoices ordered by latest |
| `GET` | `/api/invoices/{id}` | Get detailed invoice by ID |
| `POST` | `/api/invoices` | Create new invoice with auto CGST/SGST calculations |
| `PATCH` | `/api/invoices/{id}/status` | Toggle status between `PAID` and `UNPAID` |
| `DELETE` | `/api/invoices/{id}` | Delete invoice record |
| `GET` | `/api/invoices/{id}/pdf` | **Download or stream vector PDF with exact CSS template** |
| `GET` | `/api/clients` | List all registered clients |
| `POST` | `/api/clients` | Add a new client |
| `DELETE` | `/api/clients/{id}` | Delete a client |
| `GET` | `/api/user/profile` | Get current business profile |
| `PUT` | `/api/user/profile` | Update business name, GSTIN, and contact details |

---

## 🧪 Running Automated Tests
```bash
mvn test
```
All integration tests verify the database persistence, real-time GST tax math (Subtotal + 9% CGST + 9% SGST = Grand Total), and PDF byte stream rendering across all 10 templates.
