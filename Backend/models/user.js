const mongoose = require('mongoose');
const Schema = mongoose.Schema;

// Define the schema for a day's data
const dayDataSchema = new Schema({
  // The binary string
  dataInt: { type: Number, required: true }, // Converted integer from binary string
});

// Define the schema for a month
const monthSchema = new Schema({
  days: { type: Map, of: dayDataSchema, required: true }, // Map of days to their data
});

// Define the user schema
const UserSchema = new Schema({
  email: {
    type: String,
    required: true,
    unique: true,
  },
  password: {
    type: String,
    required: true,
  },
  date: {
    type: Date,
    default: Date.now,
  },
  personalDetails: {
    name: {
      type: String,
      required: false,
    },
    age: {
      type: Number,
      required: false,
    },
  },
  symptoms: {
    type: [String], // Array of strings to store symptom names
    default: [],
  },
  preferences: {
    type: [String], // Array of strings to store preferences
    default: [],
  },
  onboarding: {
    type: Number, // Track onboarding progress
    default: 0,
  },
  months: {
    type: Map,
    of: monthSchema, // Map of months to their data
    required: false,
  },
});

module.exports = mongoose.model('User', UserSchema);
