const express = require('express');
const path = require('path');
const axios = require('axios');
const { PythonShell } = require('python-shell');
const cors = require('cors');
const fs = require('fs');

const app = express();
const PORT = 3000;

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Create the pre_existing_images directory if it doesn't exist
const preExistingImagesDir = path.join(__dirname, 'uploads');
if (!fs.existsSync(preExistingImagesDir)) {
    fs.mkdirSync(preExistingImagesDir);
}

// Serve pre-existing images statically
app.use('/pre_existing_images', express.static(preExistingImagesDir));

// Endpoint to handle the root URL
app.get('/', (req, res) => {
    res.send('Welcome to the Image Comparison Server!');
});

// Endpoint to compare images
app.post('/compare', async (req, res) => {
    const { ipfsImageUrl, localImageName } = req.body;

    if (!ipfsImageUrl || !localImageName) {
        return res.status(400).send({ error: 'IPFS image URL and local image name are required' });
    }

    const localImagePath = path.join(preExistingImagesDir, localImageName);
    if (!fs.existsSync(localImagePath)) {
        return res.status(400).send({ error: 'Local image not found' });
    }

    try {
        // Download the IPFS image
        const response = await axios({
            url: ipfsImageUrl,
            method: 'GET',
            responseType: 'stream'
        });

        const ipfsImagePath = path.join(__dirname, 'ipfs_temp_image.jpg');
        const writer = fs.createWriteStream(ipfsImagePath);

        response.data.pipe(writer);

        writer.on('finish', () => {
            // Run the Python script to compare images
            const options = {
                scriptPath: __dirname,
                args: [localImagePath, ipfsImagePath]
            };

            PythonShell.run('similarity.py', options, (err, results) => {
                // Clean up the IPFS image file
                fs.unlink(ipfsImagePath, (unlinkErr) => {
                    if (unlinkErr) {
                        console.error(`Failed to delete temporary IPFS image: ${unlinkErr.message}`);
                    }
                });

                if (err) return res.status(500).send({ error: err.message });
                res.json({ similarity: parseFloat(results[0]) });
            });
        });

        writer.on('error', (err) => {
            res.status(500).send({ error: err.message });
        });
    } catch (error) {
        res.status(500).send({ error: error.message });
    }
});

app.listen(PORT, '58.186.252.236', () => {
    console.log(`Server is running on http://171.247.145.173:5000:`);
});
