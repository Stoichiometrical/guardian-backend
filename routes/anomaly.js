const express = require('express');
const router = express.Router();
const Anomaly = require('../models/Anomaly');
const User = require('../models/User');

// Report anomaly
router.post('/report', async (req, res) => {
    const { user_id, anomaly_type, details } = req.body;
    try {
        const user = await User.findById(user_id);
        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }
        const newAnomaly = new Anomaly({ user_id, anomaly_type, details });
        await newAnomaly.save();
        res.status(200).json({ message: 'Anomaly reported successfully' });
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
});

module.exports = router;
