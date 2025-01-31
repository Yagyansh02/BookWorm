const User = require('../models/user');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');

const validator = require('validator');

// Register a new user
exports.registerUser = async (req, res) => {
  const { username, email, password } = req.body;

  // Validate email
  if (!validator.isEmail(email)) {
    return res.status(400).json({ message: 'Invalid email format' });
  }

  // Validate password strength
  if (!validator.isLength(password, { min: 6 })) {
    return res.status(400).json({ message: 'Password must be at least 6 characters long' });
  }
  if (!validator.isStrongPassword(password, {
    minLength: 6,
    minLowercase: 1,
    minUppercase: 1,
    minNumbers: 0,
    minSymbols: 0
  })) {
    return res.status(400).json({ message: 'Password must contain at least one uppercase letter, one lowercase letter' });
  }

  try {
    const userExists = await User.findOne({ email });
    if (userExists) {
      return res.status(400).json({ message: 'User already exists' });
    }
    const hashedPassword = await bcrypt.hash(password, 10);
    const newUser = new User({ username, email, password: hashedPassword });
    await newUser.save();
    
    // Generate token for new user
    const token = jwt.sign({ id: newUser._id }, process.env.JWT_SECRET, { expiresIn: '1h' });
    
    // Return both token and user data
    const userResponse = {
      id: newUser._id,
      username: newUser.username,
      email: newUser.email,
      profileImage: newUser.profileImage,
      preferences: newUser.preferences
    };
    
    res.status(201).json({ 
      token,
      user: userResponse,
      message: 'User registered successfully' 
    });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};
// User login
exports.loginUser = async (req, res) => {
    const { email, password } = req.body;
    try {
      const user = await User.findOne({ email });
      if (!user) {
        return res.status(400).json({ message: 'Invalid credentials' });
      }
      const isMatch = await bcrypt.compare(password, user.password);
      if (!isMatch) {
        return res.status(400).json({ message: 'Invalid Password' });
      }
      const token = jwt.sign({ id: user._id }, process.env.JWT_SECRET);
      
      // Return both token and user data (excluding password)
      const userResponse = {
        id: user._id,
        username: user.username,
        email: user.email,
        profileImage: user.profileImage,
        preferences: user.preferences
      };
      
      res.json({ 
        token,
        user: userResponse
      });
    } catch (err) {
      res.status(500).json({ message: err.message });
    }
  };
  // In authController.js
exports.refreshToken = async (req, res) => {
    try {
        const user = await User.findById(req.user.id).select('-password');
        const newToken = jwt.sign({ id: user._id }, process.env.JWT_SECRET);
        
        sendSuccess(res, 200, {
            token: newToken,
            user: user
        });
    } catch (err) {
        sendError(res, 500, 'Error refreshing token');
    }
};