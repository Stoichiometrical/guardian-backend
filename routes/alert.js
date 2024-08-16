const express = require('express');
const router = express.Router();
const Alert = require('../models/Alert');
const User = require('../models/User');

// Trigger emergency alert
router.post('/emergency', async (req, res) => {
    const { user_id, location, alert_type } = req.body;
    try {
        const user = await User.findById(user_id);
        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }
        const newAlert = new Alert({ user_id, location, alert_type });
        await newAlert.save();
        res.status(200).json({ message: 'Emergency alert triggered' });
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
});

// Activate panic button
router.post('/panic', async (req, res) => {
    const { user_id, location } = req.body;
    try {
        const user = await User.findById(user_id);
        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }
        const newAlert = new Alert({ user_id, location, alert_type: 'panic_button' });
        await newAlert.save();
        res.status(200).json({ message: 'Panic button activated' });
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
});

// Send silent alert
router.post('/silent', async (req, res) => {
    const { user_id, location, alert_message } = req.body;
    try {
        const user = await User.findById(user_id);
        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }
        const newAlert = new Alert({ user_id, location, alert_type: 'silent', alert_message });
        await newAlert.save();
        res.status(200).json({ message: 'Silent alert sent' });
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
});

module.exports = router;
