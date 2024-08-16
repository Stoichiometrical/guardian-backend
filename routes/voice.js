const express = require('express');
const router = express.Router();
const Voiceprint = require('../models/Voiceprint');
const User = require('../models/User');

// Register voiceprint
router.post('/register', async (req, res) => {
    const { user_id, voice_sample } = req.body;
    try {
        const user = await User.findById(user_id);
        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }
        const newVoiceprint = new Voiceprint({ user_id, voice_sample });
        await newVoiceprint.save();
        res.status(200).json({ message: 'Voiceprint registered successfully' });
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
});

// Verify voice
router.post('/verify', async (req, res) => {
    const { user_id, voice_sample } = req.body;
    try {
        const user = await User.findById(user_id);
        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }
        const voiceprint = await Voiceprint.findOne({ user_id });
        if (voiceprint && voiceprint.voice_sample === voice_sample) {
            res.status(200).json({ message: 'Voice verified successfully' });
        } else {
            res.status(403).json({ message: 'Voice verification failed' });
        }
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
});

module.exports = router;
