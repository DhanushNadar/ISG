FROM node:18-alpine AS build
WORKDIR /app

# Install dependencies first (caching)
COPY package*.json ./
RUN npm install

# Build the Angular app
COPY . .
RUN npx ng build --configuration production

# Serve using Nginx
FROM nginx:alpine
# Copy compiled output to Nginx html directory
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
# Copy custom Nginx configuration to support Angular routing
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
