import fs from 'fs';
import brain from 'brain.js';

// Generate synthetic insurance claim data for training
function generateData() {
  const data = [];

  for (let i = 0; i < 1000; i++) {
    // Generate random values
    const amount = Math.random() * 3000000; // Up to 30L
    const isLateNight = Math.random() > 0.8 ? 1 : 0;
    const isRecentPolicy = Math.random() > 0.85 ? 1 : 0;
    const noFir = Math.random() > 0.9 ? 1 : 0;
    const noCctv = Math.random() > 0.7 ? 1 : 0;
    const inconsistent = Math.random() > 0.85 ? 1 : 0;
    const recurrent = Math.random() > 0.95 ? 1 : 0;

    // Normalize values for Neural Network (0 to 1)
    const normalizedAmount = amount / 3000000;

    // Calculate a "true" risk based on our synthetic rules
    let risk = 0.1; // Base risk
    if (normalizedAmount > 0.6) risk += 0.3; // High amount
    if (isLateNight) risk += 0.15;
    if (isRecentPolicy) risk += 0.35;
    if (noFir) risk += 0.25;
    if (noCctv) risk += 0.1;
    if (inconsistent) risk += 0.2;
    if (recurrent) risk += 0.25;

    // Add some random noise
    risk += (Math.random() * 0.1) - 0.05;

    // Cap between 0.05 and 0.98
    risk = Math.max(0.05, Math.min(0.98, risk));

    data.push({
      input: {
        amount: normalizedAmount,
        lateNight: isLateNight,
        recentPolicy: isRecentPolicy,
        noFir: noFir,
        noCctv: noCctv,
        inconsistent: inconsistent,
        recurrent: recurrent
      },
      output: { risk: risk }
    });
  }

  return data;
}

console.log("Generating synthetic training data...");
const trainingData = generateData();

console.log("Initializing brain.js Neural Network...");
// Create a Neural Network
const net = new brain.NeuralNetwork({
  hiddenLayers: [5, 4],
  activation: 'sigmoid'
});

console.log("Training the model... (this may take a moment)");
// Train the Network
const stats = net.train(trainingData, {
  iterations: 20000,
  errorThresh: 0.005,
  log: true,
  logPeriod: 1000,
  learningRate: 0.01
});

console.log(`Training Complete!`);
console.log(`Iterations: ${stats.iterations}, Error: ${stats.error}`);

// Save the model
console.log("Saving model to backend/model.json...");
const json = net.toJSON();
fs.writeFileSync('model.json', JSON.stringify(json));
console.log("Model successfully saved to backend/model.json.");

// Test a quick prediction
const testData = {
  amount: 2500000 / 3000000, // High amount
  lateNight: 1,
  recentPolicy: 1,
  noFir: 1,
  noCctv: 1,
  inconsistent: 0,
  recurrent: 0
};
const result = net.run(testData);
console.log("Quick Test Prediction (should be high risk):", result.risk * 100);
