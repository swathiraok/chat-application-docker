# File: chat-frontend/Dockerfile
# This Dockerfile builds a Docker image for your Angular frontend application.

# Stage 1: Build the Angular application
FROM node:18-alpine AS build

# Set the working directory inside the container.
WORKDIR /app

# Install bash in the build stage (needed for the final entrypoint if using bash).
RUN apk add --no-cache bash

# Install Node.js dependencies and Angular CLI globally.
# This ensures 'ng' command is available during the build.
RUN npm install -g @angular/cli@16 # Specify a compatible Angular CLI version for Angular 16

# Copy package.json and package-lock.json to install project dependencies.
COPY package*.json ./

# Install project-specific Node.js dependencies.
RUN npm install --verbose

# Copy the rest of the Angular application source code.
# The build context for the frontend service in docker-compose.yml should be ./frontend/chat-frontend
# So, this COPY . . will copy all files from chat-frontend into /app
COPY . .

# Build the Angular application for production.
# Angular will build to its default path 'dist/chat-frontend' relative to the WORKDIR /app.
# And inside 'dist/chat-frontend', it creates a 'browser' directory.
RUN ng build --configuration production --verbose=true


# Stage 2: Serve the Angular application with Nginx
FROM nginx:1.23.4-alpine

# Install dos2unix utility and bash.
RUN apk add --no-cache dos2unix bash

# Copy the built Angular application from the 'build' stage to Nginx's default web root.
# The correct source path is /app/dist/chat-frontend/browser/
# We copy the *contents* of this directory to /usr/share/nginx/html/.
COPY --from=build /app/dist/chat-frontend/browser/. /usr/share/nginx/html/

# Create the assets directory for config.json before entrypoint.sh runs
RUN mkdir -p /usr/share/nginx/html/assets

# Copy the custom Nginx configuration file.
COPY nginx.conf /etc/nginx/conf.d/default.conf
RUN dos2unix /etc/nginx/conf.d/default.conf

# Create entrypoint.sh content directly using printf and ensure shell
RUN printf '#!/bin/bash\n' > /entrypoint.sh \
    && printf 'set -e\n' >> /entrypoint.sh \
    && printf 'echo "--- Running entrypoint.sh at container startup ---"\n' >> /entrypoint.sh \
    && printf 'echo "Creating config.json with BASE_URL: $BASE_URL"\n' >> /entrypoint.sh \
    && printf 'echo "{\\"BASE_URL\\": \\"$BASE_URL\\"}" > /usr/share/nginx/html/assets/config.json\n' >> /entrypoint.sh \
    && printf 'echo "--- Verify config.json creation and location ---"\n' >> /entrypoint.sh \
    && printf 'ls -la /usr/share/nginx/html/assets/\n' >> /entrypoint.sh \
    && printf 'cat /usr/share/nginx/html/assets/config.json\n' >> /entrypoint.sh \
    && printf 'echo "--- Starting Nginx... ---"\n' >> /entrypoint.sh \
    && printf 'nginx -g '"'"'daemon off;'"'"'\n' >> /entrypoint.sh \
    && chmod +x /entrypoint.sh

# We specify bash directly here, as sometimes the default 'sh' can be problematic.
ENTRYPOINT ["/bin/bash", "/entrypoint.sh"]

# Expose port 80, as Nginx inside the container will listen on this port.
EXPOSE 80
