const express = require('express');
const router = express.Router();
const User = require('../models/user');

// Save personal details
router.post('/savePersonalDetails', async (req, res) => {
    const { userId, name, age, onboarding } = req.body;
    try {
        let user = await User.findById(userId);
        if (!user) {
            return res.status(404).json({ msg: 'User not found' });
        }

        user.personalDetails = { name, age };
        user.onboarding = onboarding;
        await user.save();

        res.status(200).json({ msg: 'Personal details saved successfully' });
    } catch (err) {
        console.error(err.message);
        res.status(500).send('Server error');
    }
});

router.post('/saveSymptoms', async (req, res) => {
    const { userId, symptoms, onboarding } = req.body;
    try {
        let user = await User.findById(userId);
        if (!user) {
            console.log('User not found');
            return res.status(404).json({ msg: 'User not found' });
        }

        user.symptoms = symptoms;
        user.onboarding = onboarding;
        await user.save();

        console.log('Symptoms saved successfully');
        res.status(200).json({ msg: 'Symptoms saved successfully' });
    } catch (err) {
        console.error('Server error:', err.message);
        res.status(500).send('Server error');
    }
});

// Save preferences
router.post('/savePreferences', async (req, res) => {
    const { userId, preferences, onboarding } = req.body;
    try {
        let user = await User.findById(userId);
        if (!user) {
            console.log('User not found');
            return res.status(404).json({ msg: 'User not found' });
        }

        user.preferences = preferences;
        user.onboarding = onboarding;
        await user.save();

        console.log('Preferences saved successfully');
        res.status(200).json({ msg: 'Preferences saved successfully' });
    } catch (err) {
        console.error('Server error:', err.message);
        res.status(500).send('Server error');
    }
});



// Get onboarding status
router.get('/getOnboardingStatus/:userId', async (req, res) => {
    const { userId } = req.params;
    try {
        let user = await User.findById(userId).select('onboarding');
        if (!user) {
            return res.status(404).json({ msg: 'User not found' });
        }

        res.status(200).json({ onboarding: user.onboarding });
    } catch (err) {
        console.error(err.message);
        res.status(500).send('Server error');
    }
});

module.exports = router;



