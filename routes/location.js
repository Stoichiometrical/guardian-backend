const express = require('express');
const router = express.Router();
const Location = require('../models/Location');
const User = require('../models/User');

// Share location
router.post('/share', async (req, res) => {
    const { user_id, location } = req.body;
    try {
        const user = await User.findById(user_id);
        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }
        const newLocation = new Location({ user_id, ...location });
        await newLocation.save();
        res.status(200).json({ message: 'Location shared successfully' });
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
});

// Get safe routes (Mock implementation)
router.get('/safe', (req, res) => {
    const { start_location, end_location } = req.query;
    // Mock safe routes logic
    res.status(200).json({ routes: 'Safe routes based on historical data' });
});

module.exports = router;
