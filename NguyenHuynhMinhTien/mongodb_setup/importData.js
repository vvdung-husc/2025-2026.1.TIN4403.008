const { MongoClient } = require('mongodb');
const fs = require('fs');
const csv = require('csv-parser');

// MongoDB Connection URI (ưu tiên biến môi trường)
const uri = process.env.MONGODB_URI || 'mongodb://localhost:27017';
const dbName = process.env.DB_NAME || 'student_management';
const collectionName = "students"; // Tên collection để lưu trữ dữ liệu sinh viên

async function importData() {
    const client = new MongoClient(uri);

    try {
        await client.connect();
        console.log("Connected successfully to MongoDB");

        const db = client.db(dbName);
        const collection = db.collection(collectionName);

        const students = [];
        const csvFilePath = 'DSSV_TIN4403.008.csv';

        fs.createReadStream(csvFilePath)
            .pipe(csv({ separator: ',' })) // Sử dụng dấu phẩy làm dấu phân cách
            .on('data', (row) => {
                // Giả sử MSV nằm ở cột thứ 2 (index 1)
                const msv = row[Object.keys(row)[1]]; // Lấy giá trị của cột thứ 2
                if (msv && msv.length >= 6) {
                    const password = msv.slice(-6); // 6 ký tự cuối của MSV
                    students.push({
                        MSV: msv,
                        HoTen: row[Object.keys(row)[2]], // Giả sử Họ tên ở cột thứ 3 (index 2)
                        Password: password
                    });
                }
            })
            .on('end', async() => {
                if (students.length > 0) {
                    const result = await collection.insertMany(students);
                    console.log(`${result.insertedCount} documents were inserted`);
                } else {
                    console.log("No student data to insert.");
                }
                client.close();
                console.log("MongoDB connection closed.");
            })
            .on('error', (error) => {
                console.error("Error reading CSV file:", error);
                client.close();
            });

    } catch (err) {
        console.error("Error connecting to MongoDB or importing data:", err);
        client.close();
    }
}

importData();