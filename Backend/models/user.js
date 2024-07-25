const mongoose = require('mongoose');

const UserSchema = new mongoose.Schema({
    email: {
        type: String,
        required: true,
        unique: true
    },
    password: {
        type: String,
        required: true
    },
    date: {
        type: Date,
        default: Date.now
    },
    personalDetails: {
        name: {
            type: String,
            required: false
        },
        age: {
            type: Number,
            required: false
        }
    },
    symptoms: {
        type: [String], // Array of strings to store symptom names
        default: []
    },
    preferences: {
        type: [String], // Array of strings to store preferences
        default: []
    },
    onboarding: {
        type: Number, // Track onboarding progress
        default: 0
    }
});

module.exports = mongoose.model('User', UserSchema);
