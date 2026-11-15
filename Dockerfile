FROM php:8.2-apache

WORKDIR /var/www/html
RUN a2enmod rewrite

RUN apt-get update && apt-get install -y \
    curl git unzip libpng-dev libzip-dev \
    && docker-php-ext-install pdo_mysql gd zip \
    && rm -rf /var/lib/apt/lists/*

COPY php.ini /usr/local/etc/php/php.ini
COPY . /var/www/html

RUN chown -R www-data:www-data /var/www/html \
    && chmod -R 755 /var/www/html

EXPOSE 80

HEALTHCHECK --interval=15s --timeout=5s --retries=3 \
    CMD curl -f http://localhost/health.php || exit 1
