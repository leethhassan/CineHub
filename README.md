# CineHub — Production Streaming Movies & Series Platform

Production-ready mobile streaming platform for movies and series, built with **Native Android (Kotlin & Jetpack Compose)**, high-efficiency **Retrofit/OkHttp REST API client**, **Room Offline Cache**, and a complete **Laravel 11+ REST API backend with MySQL & Sanctum**.

---

## 🏛️ System Architecture

```
                               ┌──────────────────────────────────────────────┐
                               │             Android Client (Kotlin)          │
                               │        Jetpack Compose + Material 3          │
                               └──────────────────────┬───────────────────────┘
                                                      │
                                           Retrofit 2 / OkHttp 4
                                         (HTTPS + Bearer Sanctum)
                                                      │
                                                      ▼
                               ┌──────────────────────────────────────────────┐
                               │           Laravel REST API Gateway           │
                               │        Laravel Sanctum Authentication        │
                               └──────────────────────┬───────────────────────┘
                                                      │
                                              Eloquent ORM / PDO
                                                      │
                                                      ▼
                               ┌──────────────────────────────────────────────┐
                               │             MySQL 8.0+ Database              │
                               │      Users, Catalogs, Watchlist, Ratings     │
                               └──────────────────────────────────────────────┘
                                                      ▲
                                                      │ (Offline Fallback & Cache)
                               ┌──────────────────────┴───────────────────────┐
                               │             Room Local Database              │
                               │            Zero-Latency Offline Cache        │
                               └──────────────────────────────────────────────┘
```

---

## 🚀 1. Laravel Backend Deployment

### Requirements:
- PHP 8.2+ with extensions: `pdo_mysql`, `openssl`, `mbstring`, `tokenizer`, `xml`, `ctype`, `json`, `bcmath`, `curl`
- MySQL 8.0+ or MariaDB 10.5+
- Composer 2.x
- Nginx / Apache with HTTPS SSL certificate

### Installation Steps:
```bash
# 1. Navigate to backend directory
cd backend

# 2. Install PHP dependencies
composer install --no-dev --optimize-autoloader

# 3. Setup Environment File
cp .env.example .env

# 4. Generate Application Key
php artisan key:generate

# 5. Create New MySQL Database on the server:
# mysql -u root -p -e "CREATE DATABASE cinehub_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 6. Configure MySQL in .env:
# DB_CONNECTION=mysql
# DB_HOST=127.0.0.1
# DB_PORT=3306
# DB_DATABASE=cinehub_db
# DB_USERNAME=cinehub_user
# DB_PASSWORD=your_secure_password_here

# 7. Run Production Safe Migrations (DO NOT use migrate:fresh in production!):
php artisan migrate --force

# 8. Initial One-time Seeder for genres and demo catalogs:
php artisan db:seed --class=DatabaseSeeder --force

# 9. Future Schema Updates (Zero-Downtime / Preserves all user data):
# When adding new columns or tables in future updates:
# php artisan make:migration add_new_fields_to_table
# php artisan migrate --force

# 10. Start the server (Development):
php artisan serve --host=0.0.0.0 --port=8000

# 11. For Production (Nginx):
# Ensure fastcgi_pass points to php-fpm and root is set to /backend/public
```

---

## 🔒 2. Authentication & Sanctum

CineHub uses **Laravel Sanctum** token-based authentication:
- `POST /api/register` → Creates account and returns bearer token.
- `POST /api/login` → Authenticates credentials with Bcrypt and returns bearer token.
- `POST /api/logout` → Revokes the active access token.
- Protected routes require the `Authorization: Bearer <token>` header.
- The Android `ApiClient` automatically injects this header into all network requests.

### Pre-seeded Accounts:
| Role | Email | Password |
|---|---|---|
| **Admin** | `admin@cinehub.com` | `admin123` |
| **User** | `user@cinehub.com` | `user123` |

---

## 📱 3. Android Client Configuration

### Connecting to your Remote Laravel Backend:
1. Open the CineHub app on your Android device or emulator.
2. Navigate to **Profile → App Settings**.
3. Under **Backend & API Architecture**, enter your HTTPS domain (e.g., `https://api.yourdomain.com`).
4. Tap **Save & Reconnect**.
5. The `ApiClient` dynamically reconfigures Retrofit with the new Base URL.
6. If the server is offline or unreachable, CineHub seamlessly serves and queues data using the local **Room Cache**.

---

## 📦 4. Release Keystore & Google Play Preparation

### A. Keystore Setup:
1. Generate your production signing key using `keytool`:
   ```bash
   keytool -genkey -v -keystore my-upload-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
   ```
2. Place `my-upload-key.jks` in the project root.
3. Copy `keystore.properties.example` to `keystore.properties`:
   ```bash
   cp keystore.properties.example keystore.properties
   ```
4. Enter your secure passwords in `keystore.properties`:
   ```properties
   KEYSTORE_PATH=my-upload-key.jks
   STORE_PASSWORD=YourStorePassword
   KEY_ALIAS=upload
   KEY_PASSWORD=YourKeyPassword
   ```

### B. Building Production Artifacts:
```bash
# Clean project
gradle clean

# Run Unit & Robolectric Tests
gradle :app:testDebugUnitTest

# Build Debug APK:
gradle :app:assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk

# Build Debug App Bundle (AAB):
gradle :app:bundleDebug
# Output: app/build/outputs/bundle/debug/app-debug.aab

# Build Production Signed Release App Bundle (for Google Play):
gradle :app:bundleRelease
# Output: app/build/outputs/bundle/release/app-release.aab
```

---

## 🛡️ 5. Production Security Verification

- **HTTPS Strict Mode:** Configured via `res/xml/network_security_config.xml` (`cleartextTrafficPermitted="false"`).
- **ProGuard / R8 Optimization:** Active rules in `app/proguard-rules.pro` preserve Retrofit, Moshi, and Room models while stripping debug overhead.
- **Throttled Progress Reporting:** Video watch progress is throttled to 10-second intervals and upon pause/exit to prevent API request flooding.
- **Zero Secrets in Git:** Keystores, database credentials, and token secrets are excluded from version control.

---

## 📋 6. API Route Summary

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `POST` | `/api/register` | Register new user | No |
| `POST` | `/api/login` | Login user | No |
| `POST` | `/api/logout` | Revoke token | Yes (Sanctum) |
| `GET` | `/api/home` | Home feed catalog | Optional |
| `GET` | `/api/movies` | Paginated movies list | No |
| `GET` | `/api/movies/{id}` | Movie details + increment views | No |
| `GET` | `/api/series` | Paginated series list | No |
| `GET` | `/api/series/{id}` | Series details + seasons + episodes | No |
| `GET` | `/api/search` | Search with filters | No |
| `GET` | `/api/watchlist` | Get user watchlist | Yes |
| `POST` | `/api/watchlist` | Add to watchlist | Yes |
| `DELETE`| `/api/watchlist/{id}` | Remove from watchlist | Yes |
| `GET` | `/api/continue-watching`| Continue watching list | Yes |
| `POST` | `/api/progress` | Save playback progress (throttled) | Yes |
| `POST` | `/api/ratings` | Submit/update 1-5 star rating | Yes |
| `GET` | `/api/notifications` | User notifications | Yes |
| `GET` | `/api/admin/dashboard/stats` | Admin metrics | Yes (Admin) |
| `POST` | `/api/admin/movies` | Publish movie | Yes (Admin) |
| `POST` | `/api/admin/notifications/broadcast` | Broadcast announcement | Yes (Admin) |
