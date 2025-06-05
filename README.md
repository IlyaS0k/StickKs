## 🚀 Quick Start with Docker Compose

Make sure you have Docker and Docker Compose installed on your system.

1. Generate TDLIB_API_ID and TDLIB_API_HASH

   Go to https://my.telegram.org and log in with your Telegram account.

   Navigate to API Development Tools.

   Fill out the form:
         
        - App title – choose any name
        - Short name – can be anything

   After submitting the form, you'll receive:

        - api_id
        - api_hash

2. Create app.env file

   Create a file called app.env in the root of your project with the following contents:
   ```env
   TDLIB_API_ID=your_api_id_here
   TDLIB_API_HASH=your_api_hash_here

3. Clone the repository and start the application:

   ```bash
   git clone https://github.com/IlyaS0k/StickKs.git
   cd StickKs
   docker-compose up --build

4. Open http://localhost:8080 in your browser
