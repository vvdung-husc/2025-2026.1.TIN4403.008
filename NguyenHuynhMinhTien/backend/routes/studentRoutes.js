const express = require('express');
const router = express.Router();
const studentController = require('../controllers/studentController');

// Get all students
router.get('/', studentController.getAllStudents);

// Get a single student by MSV
router.get('/:msv', studentController.getStudentByMsv);

// Create a new student
router.post('/', studentController.createStudent);

// Update a student by MSV
router.put('/:msv', studentController.updateStudentByMsv);

// Delete a student by MSV
router.delete('/:msv', studentController.deleteStudentByMsv);

module.exports = router;