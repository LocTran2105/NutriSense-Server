# 🤖 NitriSense AI Backend

<p align="center">
  <b>AI-powered Nutrition Tracking & Smart Health Assistant</b><br/>
  Backend Server built with Spring Boot + Gemini AI
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-blue?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Spring_Boot-3.x-brightgreen?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/MySQL-8.0-orange?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Gemini_AI-2.5_Flash_Lite-purple?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/JPA-Hibernate-red?style=for-the-badge"/>
</p>

---

## 🚀 Overview

**NitriSense AI Backend** NitriSense AI là backend server cho ứng dụng theo dõi dinh dưỡng và trợ lý ảo thông minh. Hệ thống sử dụng Gemini API để phân tích món ăn từ văn bản tự nhiên và hình ảnh, trả về các chỉ số dinh dưỡng (calo, protein, vitamin, khoáng chất). Backend được xây dựng bằng Spring Boot, kết nối với MySQL, cung cấp RESTful API cho ứng dụng Android.

💡 Điểm đặc biệt:
- Phân tích món ăn bằng **AI (text + image)**
- Chat AI có **ngữ cảnh cá nhân (RAG)**
- Theo dõi dinh dưỡng + thể lực + lịch trình trong **1 hệ thống duy nhất**

---

## ✨ Key Features

### 🤖 AI Nutrition Analysis
- 📄 Nhập món ăn bằng text tự nhiên
- 📸 Nhận diện món ăn từ hình ảnh
- 🔁 Fallback 2 API key (tăng độ ổn định)
- ❓ AI tự hỏi lại nếu input mơ hồ

---

### 📊 Smart Health Tracking
- 🔥 Tính toán **TDEE + Calorie Goal**
- 💧 Theo dõi lượng nước uống
- 🥗 Phân tích **macro + vi chất**
- 📈 Dashboard 7 ngày

---

### 💬 AI Chat Assistant (RAG)
- 🧠 Hiểu ngữ cảnh người dùng
- 📊 Dựa trên:
  - Chỉ số cơ thể
  - Thiếu hụt dinh dưỡng
  - Lịch sử ăn uống
- 🔔 Cảnh báo và gợi ý thông minh

---

### 🗓️ Smart Scheduling
- ⏰ Nhắc ăn, uống nước, tập luyện
- ✅ Đánh dấu hoàn thành
- 📱 Android AlarmManager

---

### 🏋️ Fitness Tracking
- Ghi nhận:
  - Hít đất
  - Plank
  - Chạy 1km
- AI tư vấn dựa trên hiệu suất

---

## 🏗️ System Architecture

```text
Android App
     ↓
 REST API (Spring Boot)
     ↓
 Service Layer (Business Logic)
     ↓
 AI Service (Gemini API + Fallback)
     ↓
 Database (MySQL)
```
---

## 🛠️ Công nghệ sử dụng

| Thành phần        | Công nghệ |
|------------------|----------|
| Backend          | Spring Boot 3, Spring MVC, Spring Data JPA |
| AI Integration   | Gemini API (Google AI + fallback) |
| Database         | MySQL 8.0 |
| HTTP Client      | RestTemplate (timeout: connect 5s, read 15s) |
| JSON Processing  | Jackson, Gson |
| Authentication   | Firebase Auth (client-side) + đồng bộ userId |
| Build Tool       | Maven |

---

## 📂 Project Structure
```text
src/main/java/hcmute/edu/vn/nitrisensebackend/
│
├── config/        # Config beans
├── controller/    # REST APIs
├── dto/           # Data Transfer Objects
├── entity/        # JPA Entities
├── enums/         # Enum definitions
├── repository/    # Data access layer
└── service/       # Business logic
```

---

## ⚙️ Setup & Run
### 1️⃣ Clone project
```bash
git clone https://github.com/nitrisense/nitrisense-backend.git
cd nitrisense-backend
```
### 2️⃣ Setup Database
```sql
CREATE DATABASE nitrisense_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```
### 3️⃣ Config application.properties
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/nitrisense_db
spring.datasource.username=root
spring.datasource.password=your_password

gemini.api.key=YOUR_KEY
gemini.api.key.fallback=YOUR_KEY_2
```
### 4️⃣ Run server
```bash
mvn clean install
mvn spring-boot:run
```
👉 Server: http://localhost:8080

---

## 🌐 API Highlights
| Feature        | Endpoint                |
| -------------- | ----------------------- |
| User Sync      | `/api/users/sync`       |
| Log Meal       | `/api/meals/log`        |
| Analyze Image  | `/api/ai/analyze-image` |
| Daily Summary  | `/api/home/summary`     |
| Chat AI        | `/api/chat/send`        |
| Water Tracking | `/api/water/add`        |

---

## 🧪 Sample Data
user_id = 1
Preloaded food items
RDA nutrient reference

---

## 📬 API Design
- ✔ RESTful
- ✔ JSON response

---

## 👨‍💻 Team
| Name         | Role                                                                         |
| ------------ | -----------------------------------------------------------------------------|
| Ninh Anh Tú  | Backend, database, tích hợp Gemini AI, viết báo cáo                          |
| Trần Hữu Lộc | Kiểm thử database, tích hợp Firebase Auth, lịch trình, thông báo, test API   |
| Nguyễn Khánh | Thiết kế giao diện Android, kết nối API, tối ưu UX                           |

---

## ⭐ Support
If you find this project useful:
👉 Give it a Star on GitHub ⭐

---

## 📫 Contact
- 📧 ninhanhtu1704@gmail.com
- 📧 tranhuuloc05@gmail.com
- 📧 nguyenkhanhk9.2005@gmail.com
