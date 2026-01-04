const mongoose = require('mongoose');

const studentSchema = new mongoose.Schema({
  MSV: {
    type: String,
    required: true,
    unique: true,
  },
  HoTen: {
    type: String,
    required: true,
  },
  NgaySinh: {
    type: String,
    required: true,
  },
  GioiTinh: {
    type: String,
    required: true,
  },
  DiaChi: {
    type: String,
    required: true,
  },
  SoDienThoai: {
    type: String,
    required: true,
  },
  Email: {
    type: String,
    required: true,
    unique: true,
  },
  MaLop: {
    type: String,
    required: true,
  },
  TenLop: {
    type: String,
    required: true,
  },
  Khoa: {
    type: String,
    required: true,
  },
  Nganh: {
    type: String,
    required: true,
  },
  ChuyenNganh: {
    type: String,
    required: true,
  },
});

const Student = mongoose.model('Student', studentSchema);

module.exports = Student;