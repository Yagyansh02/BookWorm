const User = require('../models/user');

// Add a book to a user's category
exports.addBookToCategory = async (req, res) => {
    try {
        const { userId, book, category } = req.body;

        // Find user by ID
        const user = await User.findById(userId);
        if (!user) {
            return res.status(404).json({ message: "User not found" });
        }

        // Add book to the specified category
        user[category].push(book);

        console.log('Updated category:', user[category]);

        await user.save();
        return res.status(200).json({
            message: "Book added to category successfully",
        });
    } catch (error) {
        console.error(error);
        return res.status(500).json({ message: "Server error" });
    }
};
// Get books by category for a user
exports.getBooksByCategory = async (req, res) => {
    const { userId, category } = req.params;

    try {
        // Validate category
        if (!['currentlyReading', 'wantToRead', 'read'].includes(category)) {
            return res.status(400).json({ message: 'Invalid category' });
        }

        // Find user and get the specified category
        const user = await User.findById(userId);
        if (!user) return res.status(404).json({ message: 'User not found' });

        console.log('Fetched Category:', user[category]);
        res.json(user[category]);
    } catch (err) {
        console.error('Error fetching books by category:', err);
        res.status(500).json({ message: err.message });
    }
};
