const express = require('express');
const bodyParser = require('body-parser');
const mongoose = require('mongoose');
const config = require('./config');

// Initialize app
const app = express();
app.use(bodyParser.json());

// Database connection
mongoose.connect(config.mongoURI, { useNewUrlParser: true, useUnifiedTopology: true });

// Routes
const userRoutes = require('./routes/user');
const locationRoutes = require('./routes/location');
const voiceRoutes = require('./routes/voice');
const alertRoutes = require('./routes/alert');
const anomalyRoutes = require('./routes/anomaly');

app.use('/api/v1/user', userRoutes);
app.use('/api/v1/location', locationRoutes);
app.use('/api/v1/voice', voiceRoutes);
app.use('/api/v1/alert', alertRoutes);
app.use('/api/v1/anomaly', anomalyRoutes);

// Health check endpoint
app.get('/api/v1/health', (req, res) => {
    res.status(200).json({ status: 'API is healthy' });
});

// Start the server
const port = process.env.PORT || 3000;
app.listen(port, () => {
    console.log(`Server running on port ${port}`);
});
