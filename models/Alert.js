const mongoose = require('mongoose');

const AlertSchema = new mongoose.Schema({
    user_id: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    location: {
        latitude: Number,
        longitude: Number
    },
    alert_type: String,
    alert_message: String,
    timestamp: { type: Date, default: Date.now },
});

module.exports = mongoose.model('Alert', AlertSchema);
