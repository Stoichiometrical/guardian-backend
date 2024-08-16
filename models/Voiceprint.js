const mongoose = require('mongoose');

const VoiceprintSchema = new mongoose.Schema({
    user_id: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    voice_sample: String,
});

module.exports = mongoose.model('Voiceprint', VoiceprintSchema);
