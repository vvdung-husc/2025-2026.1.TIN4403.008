const Student = require('../models/Student');

// Get all students
exports.getAllStudents = async (req, res) => {
  try {
    const students = await Student.find();
    res.json(students);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

// Get a single student by MSV
exports.getStudentByMsv = async (req, res) => {
  try {
    const student = await Student.findOne({ MSV: req.params.msv });
    if (!student) return res.status(404).json({ message: 'Student not found' });
    res.json(student);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

// Create a new student
exports.createStudent = async (req, res) => {
  const student = new Student({
    MSV: req.body.MSV,
    HoTen: req.body.HoTen,
    NgaySinh: req.body.NgaySinh,
    GioiTinh: req.body.GioiTinh,
    DiaChi: req.body.DiaChi,
    SoDienThoai: req.body.SoDienThoai,
    Email: req.body.Email,
    MaLop: req.body.MaLop,
    TenLop: req.body.TenLop,
    Khoa: req.body.Khoa,
    Nganh: req.body.Nganh,
    ChuyenNganh: req.body.ChuyenNganh,
  });

  try {
    const newStudent = await student.save();
    res.status(201).json(newStudent);
  } catch (err) {
    res.status(400).json({ message: err.message });
  }
};

// Update a student by MSV
exports.updateStudentByMsv = async (req, res) => {
  try {
    const student = await Student.findOne({ MSV: req.params.msv });
    if (!student) return res.status(404).json({ message: 'Student not found' });

    if (req.body.HoTen) student.HoTen = req.body.HoTen;
    if (req.body.NgaySinh) student.NgaySinh = req.body.NgaySinh;
    if (req.body.GioiTinh) student.GioiTinh = req.body.GioiTinh;
    if (req.body.DiaChi) student.DiaChi = req.body.DiaChi;
    if (req.body.SoDienThoai) student.SoDienThoai = req.body.SoDienThoai;
    if (req.body.Email) student.Email = req.body.Email;
    if (req.body.MaLop) student.MaLop = req.body.MaLop;
    if (req.body.TenLop) student.TenLop = req.body.TenLop;
    if (req.body.Khoa) student.Khoa = req.body.Khoa;
    if (req.body.Nganh) student.Nganh = req.body.Nganh;
    if (req.body.ChuyenNganh) student.ChuyenNganh = req.body.ChuyenNganh;

    const updatedStudent = await student.save();
    res.json(updatedStudent);
  } catch (err) {
    res.status(400).json({ message: err.message });
  }
};

// Delete a student by MSV
exports.deleteStudentByMsv = async (req, res) => {
  try {
    const student = await Student.findOne({ MSV: req.params.msv });
    if (!student) return res.status(404).json({ message: 'Student not found' });

    await student.deleteOne();
    res.json({ message: 'Student deleted' });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};