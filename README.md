## ========= Description ========
Hacker News Crawler is a web application that extracts the first 30 entries from Hacker News and provides intelligent filters to analyze the information:

Filter A: Titles with more than 5 words, ordered by number of comments
Filter B: Titles with 5 or fewer words, ordered by points

The application also records all user interactions for usage analysis.

## ========= Features ======

## Functional
✅ Web Scraping: Automatically extracts the first 30 entries from Hacker News

✅ Smart Filters: Two predefined filters with sorting

✅ Statistics Dashboard: Real-time metrics visualization

✅ Activity History: Records all queries made

✅ Activity Modal: Detailed view of usage logs

✅ Data Refresh: Manual data update

## Technical
✅ Layered Architecture: Clear separation of responsibilities

✅ H2 Persistence: In-memory database for logs

✅ Dockerized: Development and production environment with Docker

✅ Unit Testing: Test coverage for frontend and backend

✅ REST API: Documented and versioned endpoints

✅ Responsive Design: Mobile and tablet adaptable UI

## 🏗️ Architecture

### System Layers

#### 1. Client Layer
- **Browser** (http://localhost:4200)
- Interacts with the frontend application

#### 2. Frontend Layer (Angular + Nginx)
| Component | Description |
|-----------|-------------|
| **Dashboard** | Statistics and metrics visualization |
| **Filters UI** | Filter buttons for data analysis |
| **Activity Modal** | Usage logs display |

#### 3. Backend Layer (Spring Boot)
| Component | Description |
|-----------|-------------|
| **Controller** | REST API endpoints (`/api/*`) |
| **Service** | Business logic and web scraping |
| **Repository** | Data access with JPA/Hibernate |

#### 4. Data Layer (H2 Database)
| Table | Fields |
|-------|--------|
| **Entries** | id, position, title, points, comments, wordCount |
| **UsageLogs** | id, timestamp, filterType, resultCount, responseTimeMs, endpoint |


## Requirements
Requirement	    Version	    Note
Docker	        24.x+	    For container execution
Docker Compose	2.x+	    For orchestration
Node.js	        18.x+	    Only for development without Docker
Java	        17.x	    Only for development without Docker

##  Installation & Execution
1. Clone the repository

git clone https://github.com/RValenR/hacker-news-project.git
cd hacker-news-project

2. Start the services

# Build the images
docker-compose build

# Start the services
docker-compose up -d

# View logs in real-time (optional)
docker-compose logs -f

3. Access the application
Service	    URL
Frontend	http://localhost:4200
Backend	    http://localhost:8080/api/**

4. Stop the services
docker-compose down

5. ## Note: Database
The application uses H2 Database in in-memory mode for storing:

Entries: Data extracted from Hacker News
UsageLogs: User activity records


## ======== API Endpoints ==========
## Entries
Method	            Endpoint	                        Description
GET	                /api/entries	                    Get all entries
POST	            /api/entries/refresh	            Refresh data (scrape again)
GET	                /api/entries/filter/more-than-5	    Filter A (>5 words)
GET	                /api/entries/filter/less-equal-5	Filter B (≤5 words)

## Usage Logs
Method	            Endpoint	                        Description
GET	                /api/entries/usage-logs	            Get usage logs (last 20)


## ===========Testing ==============
# Frontend (Angular)
cd hacker-news-frontend
ng test
# Backend (Spring Boot)
cd hacker-news-backend/crawler
./gradlew test

## Test Coverage
Component	        Coverage	    Status
REST Controllers	100%	        ✅
Services (Logs)	    100%	        ✅
JPA Repositories	90%	            ✅
Models	            100%	        ✅
Scraping Service	Manual	        ⚠️*

⚠️ *The scraping service depends on an external resource (Hacker News), so it is manually validated.


## ========== Technical Decisions
1. Layered Architecture
Reason: Clear separation of responsibilities, maintainability, and ease of testing.

Implementation: Controller → Service → Repository → Model.

2. Dockerization
Reason: Consistent environment, easy deployment, and development.

Implementation: Dockerfile for each service and docker-compose for orchestration.

3. H2 Database
Reason: Lightweight, in-memory, no additional configuration.

Implementation: JPA/Hibernate for persistence.

4. Jsoup for Scraping
Reason: Lightweight and efficient HTML parsing library.

Implementation: Data extraction with CSS selectors.

5. Pure CSS (No Framework)
Reason: Full control, no additional dependencies.

Implementation: Custom CSS with variables and responsive design.

6. Unit Testing
Reason: Ensure quality and detect regressions.

Implementation: JUnit/Mockito (backend) and Jasmine (frontend).
