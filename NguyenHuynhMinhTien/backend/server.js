const express = require('express');
const mongoose = require('mongoose');

const app = express();
const PORT = process.env.PORT || 3000;
const studentRoutes = require('./routes/studentRoutes');

// Middleware
app.use(express.json());

// Kết nối đến MongoDB (ưu tiên biến môi trường)
const mongoUri = process.env.MONGODB_URI || 'mongodb://localhost:27017';
const dbName = process.env.DB_NAME || 'student_management';

mongoose.connect(`${mongoUri}/${dbName}`, {
        useNewUrlParser: true,
        useUnifiedTopology: true,
    })
    .then(() => console.log('Connected to MongoDB'))
    .catch(err => console.error('Could not connect to MongoDB...', err));

// Routes
app.use('/api/students', studentRoutes);

app.get('/', (req, res) => {
    res.send('Student Management API');
});

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});