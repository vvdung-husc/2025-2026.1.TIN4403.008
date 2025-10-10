# TIN4403 - Lập trình ứng dụng cho các thiết bị di động

WiFi - CNTT-MMT/13572468
##Phần mềm

1. Android Studio - Narwhal
 - https://developer.android.com/ 

2. Emulators (BlueStacks, LDPlayer, NoxPlayer, ...)
 - https://www.bluestacks.com/

3. Visual Studio Code
 - https://code.visualstudio.com/

4. MEAN Stack - https://meanjs.org/
 - MongoDB, ExpressJS, AngularJS, and Node.js
 - Search Google: MEAN STACK là gì
 - Nodejs v22.19.0 (LTS) (https://nodejs.org/)
 - MongoDB (https://www.mongodb.com/try/download/community)
   
5. Postman
 - https://www.postman.com/

6. Thư viện okhttp (chi tiết trong file Notes.txt)
API - 
 - GET https://dev.husc.edu.vn/tin4403/api
 - POST https://dev.husc.edu.vn/tin4403/api/login
   + BODY TYPE x-www-form-urlencode: username/password
   + Response
   {
    r:1,
    m:'token-value'
   }
 - POST https://dev.husc.edu.vn/tin4403/api/userinfo
   + HEADER : token lấy từ bước đăng nhập
 - POST https://dev.husc.edu.vn/tin4403/api/register
   + BODY TYPE x-www-form-urlencode: username[/password/fullname/email]
- POST https://dev.husc.edu.vn/tin4403/api/userupdate
   + HEADER : token lấy từ bước đăng nhập
   + BODY TYPE x-www-form-urlencode: [password/fullname/email]
