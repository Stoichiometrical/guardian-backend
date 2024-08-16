const mongoose = require('mongoose');

const AnomalySchema = new mongoose.Schema({
    user_id: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    anomaly_type: String,
    details: String,
    timestamp: { type: Date, default: Date.now },
});

module.exports = mongoose.model('Anomaly', AnomalySchema);
