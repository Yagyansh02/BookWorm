const express = require('express');
const router = express.Router();
const bookController = require('../controllers/bookController');
const authMiddleware = require('../middleware/authMiddleware');

// Add a book to a category
router.post('/category', bookController.addBookToCategory);

// Get books by category for a user
router.get('/:userId/category/:category', bookController.getBooksByCategory);

module.exports = router;
