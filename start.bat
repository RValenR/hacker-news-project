@echo off
echo ========================================
echo  🚀 Hacker News Crawler - Docker Setup
echo ========================================
echo.
echo 📦 Construyendo imágenes...
docker-compose build
echo.
echo 🚀 Levantando servicios...
docker-compose up -d
echo.
echo ========================================
echo ✅ Servicios iniciados correctamente
echo ========================================
echo.
echo 🌐 Frontend:   http://localhost:4200
echo 🌐 Backend:    http://localhost:8080
echo 🌐 H2 Console: http://localhost:8080/h2-console
echo.
echo 📊 Para ver logs: docker-compose logs -f
echo 🛑 Para detener:   docker-compose down
echo.
pause