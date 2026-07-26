# Etapa 1: Build
FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build -- --configuration production

# Etapa 2: Servir con Nginx
FROM nginx:alpine

# Limpiar la página por defecto
RUN rm -rf /usr/share/nginx/html/*

# Copiar solo la carpeta browser (los archivos estáticos)
COPY --from=build /app/dist/hacker-news-frontend/browser /usr/share/nginx/html

# Verificar que index.html existe
RUN ls -la /usr/share/nginx/html/

# Copiar configuración de Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]