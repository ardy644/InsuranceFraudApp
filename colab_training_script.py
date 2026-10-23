import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import classification_report, accuracy_score
import json

print("Insurance Fraud Detection Model - Colab Training Script")
print("=======================================================")
print("You can run this script in Google Colab or Kaggle to train a more advanced model.")
print("Once trained, you can export the weights and use them in a Node.js backend.")

# 1. Generate Synthetic Training Data (or load your real dataset)
def generate_synthetic_data(num_samples=5000):
    np.random.seed(42)

    # Features
    amounts = np.random.uniform(10000, 3000000, num_samples)
    late_night = np.random.choice([0, 1], num_samples, p=[0.8, 0.2])
    recent_policy = np.random.choice([0, 1], num_samples, p=[0.85, 0.15])
    no_fir = np.random.choice([0, 1], num_samples, p=[0.9, 0.1])
    no_cctv = np.random.choice([0, 1], num_samples, p=[0.7, 0.3])
    inconsistent = np.random.choice([0, 1], num_samples, p=[0.85, 0.15])
    recurrent = np.random.choice([0, 1], num_samples, p=[0.95, 0.05])

    # Calculate Risk Score (0 to 100) based on some heuristics
    risk_scores = np.zeros(num_samples)

    for i in range(num_samples):
        score = 10
        if amounts[i] > 2000000: score += 40
        elif amounts[i] > 500000: score += 25
        if late_night[i]: score += 15
        if recent_policy[i]: score += 35
        if no_fir[i]: score += 25
        if no_cctv[i]: score += 20
        if inconsistent[i]: score += 20
        if recurrent[i]: score += 25

        # Add random noise
        score += np.random.normal(0, 5)
        risk_scores[i] = min(max(score, 5), 98)

    # Create Labels (1 = Fraud/High Risk, 0 = Normal)
    # We will assume a score >= 70 is fraudulent for the classifier
    labels = (risk_scores >= 70).astype(int)

    df = pd.DataFrame({
        'amount': amounts,
        'late_night': late_night,
        'recent_policy': recent_policy,
        'no_fir': no_fir,
        'no_cctv': no_cctv,
        'inconsistent': inconsistent,
        'recurrent': recurrent,
        'risk_score': risk_scores,
        'is_fraud': labels
    })
    return df

print("\nGenerating synthetic data...")
data = generate_synthetic_data()

# 2. Prepare Data for Training
X = data.drop(['risk_score', 'is_fraud'], axis=1)
y = data['is_fraud']

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# 3. Train Model (Using Random Forest for demonstration)
print("\nTraining Random Forest Classifier...")
clf = RandomForestClassifier(n_estimators=100, random_state=42)
clf.fit(X_train, y_train)

# 4. Evaluate Model
print("\nEvaluating Model on Test Data...")
y_pred = clf.predict(X_test)
print(f"Accuracy: {accuracy_score(y_test, y_pred) * 100:.2f}%\n")
print("Classification Report:")
print(classification_report(y_test, y_pred))

# 5. Export Model / Feature Importances
print("\nFeature Importances:")
importances = clf.feature_importances_
for feature, imp in zip(X.columns, importances):
    print(f"{feature}: {imp:.4f}")

# Example of how you would save this for the Node backend (simplified)
# Note: In a real scenario, you'd export to ONNX or TensorFlow.js format
print("\nTo use this model in Node.js, you can export it using TensorFlow.js (tfjs-converter)")
print("or export rules/weights to a JSON file format if using simple structures.")
