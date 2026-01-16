# ReadFlow Backend (Spring Boot)

Subscription-based content platform backend API.

## Tech Stack

- Spring Boot, Spring Security, JWT
- PostgreSQL
- Paystack (payments + webhook)
- Resend

## Features (MVP)

- Auth: register, verify email, login, forgot/reset password
- Role-based access (USER, ADMIN)
- Platform guard (WEB/MOBILE)
- Content + categories
- Bookmarks + reading progress
- Subscriptions via Paystack + webhook verification
- Subscription expiry + reminder jobs
- Admin dashboard APIs

## Running Locally

1. Create a Postgres DB: `readflow_local`
2. Copy `.env.example` and rrename to `.env` and fill values
3. Start the server

## API Docs

Swagger will be added later.
