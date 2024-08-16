const express = require('express');
const router = express.Router();
const User = require('../models/User');
const { v4: uuidv4 } = require('uuid');

// Register user
router.post('/register', async (req, res) => {
    const { name, email, password, phone_number, emergency_contacts } = req.body;
    const newUser = new User({ name, email, password, phone_number, emergency_contacts });
    try {
        await newUser.save();
        res.status(201).json({ user_id: newUser._id });
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
});

// Login user
router.post('/login', async (req, res) => {
    const { email, password } = req.body;
    try {
        const user = await User.findOne({ email, password });
        if (user) {
            res.status(200).json({ message: 'Login successful', user_id: user._id });
        } else {
            res.status(401).json({ message: 'Invalid credentials' });
        }
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
});

// Update user profile
router.put('/profile', async (req, res) => {
    const { user_id, name, phone_number, emergency_contacts } = req.body;
    try {
        const user = await User.findById(user_id);
        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }
        user.name = name || user.name;
        user.phone_number = phone_number || user.phone_number;
        user.emergency_contacts = emergency_contacts || user.emergency_contacts;
        await user.save();
        res.status(200).json({ message: 'Profile updated successfully' });
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
});

module.exports = router;
