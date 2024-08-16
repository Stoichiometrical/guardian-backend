const mongoose = require('mongoose');

const LocationSchema = new mongoose.Schema({
    user_id: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    latitude: Number,
    longitude: Number,
    timestamp: { type: Date, default: Date.now },
});

module.exports = mongoose.model('Location', LocationSchema);
