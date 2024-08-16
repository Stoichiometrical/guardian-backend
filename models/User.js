const mongoose = require('mongoose');

const UserSchema = new mongoose.Schema({
    name: String,
    email: { type: String, unique: true },
    password: String,
    phone_number: String,
    emergency_contacts: [String],
});

module.exports = mongoose.model('User', UserSchema);
