# Splitwise Pro - Java Full Stack Edition 🚀

Splitwise Pro is a complete, full-stack bill-splitting application designed to help users track shared expenses, manage groups, and calculate exactly "who owes who." 

Built with a robust Java/Spring Boot backend and a modern React frontend, this project demonstrates complex mathematical calculations, secure authentication, and a scalable database architecture.

## 🌟 Features
- **User Authentication:** Secure JWT-based Login and Registration.
- **Group Management:** Create groups and invite friends.
- **Expense Splitting Engine:** Split bills by:
  - EQUAL (split evenly among everyone)
  - EXACT (specify exact amounts per person)
  - PERCENTAGE (split by exact percentages)
- **Real-Time Settlement Dashboard:** See your exact net balance, who owes you, and who you owe.
- **RESTful API:** fully documented with Swagger UI.

## 🛠️ Tech Stack
**Frontend:**
- React.js (Create React App)
- Tailwind CSS
- Axios for API requests

**Backend:**
- Java 17
- Spring Boot 3
- Spring Security (JWT)
- PostgreSQL (Production) / H2 (Development)
- Hibernate / JPA

## 🚀 How to Run Locally

### 1. Start the Backend
The backend runs on `http://localhost:8080`. It uses an in-memory H2 database for local development, meaning no database setup is required!
```bash
cd backend
mvn clean spring-boot:run
```
*You can access the Swagger UI at `http://localhost:8080/swagger-ui/index.html`*

### 2. Start the Frontend
The frontend runs on `http://localhost:3000`.
```bash
cd frontend
npm install
npm start
```

## 🌐 Deployment Ready
This project is configured with a `Dockerfile` for easy backend deployment on Render/Railway and a `vercel.json` for frontend deployment on Vercel.
