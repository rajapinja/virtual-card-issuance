### Here are the Postman requests for Merchant Registration, Verification, and Login assuming your base URL is http://localhost:8080/api/merchants.

✅ 1. Register Merchant
    Method: POST
    URL: http://localhost:8080/api/merchants/register
    Body (JSON):
        {
            "name": "Acme Corp",
            "email": "acme@example.com",
            "password": "secure123"
        }
    📌 verified and verificationCode will be set internally, so no need to pass them.

✅ 2. Verify Merchant
    Method: POST
    URL: http://localhost:8080/api/merchants/verify
        Body (JSON):
        {
            "email": "acme@example.com",
            "code": "e3b0c442-98fc-..."  // Replace with actual code stored in DB
        }
💡 You’ll need to either:
    Print the verificationCode in the response/log after registration, or
    Check it manually from the database for now.

✅ 3. Login Merchant
    Method: POST
    URL: http://localhost:8080/api/merchants/login
        Body (JSON):
        {
            "email": "acme@example.com",
            "password": "secure123"
        }
✅ This will succeed only after verification.
    
    📦 Optional: Response Examples
    ✔️ Register
    {
        "message": "Merchant registered. Please verify using the verification code sent to email."
    }
    ✔️ Verify
   
    {
        "message": "Account verified successfully"
    }
    ✔️ Login
    {
        "message": "Login successful for Acme Corp"
    }
