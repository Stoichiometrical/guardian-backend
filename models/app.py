import streamlit as st
import soundfile as sf
import numpy as np
import torch
from transformers import Wav2Vec2ForSequenceClassification, Wav2Vec2Processor
import librosa

# Load pre-trained model and processor
model_name = "facebook/wav2vec2-base-960h"
model = Wav2Vec2ForSequenceClassification.from_pretrained(model_name)
processor = Wav2Vec2Processor.from_pretrained(model_name)

# Function to preprocess and predict emotion from audio
def predict_emotion_from_audio(audio):
    # Preprocess the audio file
    inputs = processor(audio, sampling_rate=16000, return_tensors="pt", padding=True)

    # Move tensors to GPU if available
    if torch.cuda.is_available():
        model.to("cuda")
        inputs = {key: value.to("cuda") for key, value in inputs.items()}

    # Make the prediction
    with torch.no_grad():
        logits = model(**inputs).logits

    # Get the predicted label
    predicted_id = torch.argmax(logits, dim=-1).item()
    labels = model.config.id2label

    return labels[predicted_id]

# Streamlit app
st.title("Speech Emotion Recognition")
st.write("Record your voice and let the AI predict your emotion!")

# Audio recording widget
audio_bytes = st.audio(input("Press the 'Record' button below to start recording."))

if audio_bytes:
    # Save the recorded audio to a file
    with open("recorded_audio.wav", "wb") as f:
        f.write(audio_bytes)

    # Load the audio file and resample it to 16kHz
    audio, sample_rate = librosa.load("recorded_audio.wav", sr=16000)

    # Predict emotion
    emotion = predict_emotion_from_audio(audio)

    # Display the result
    st.write(f"Predicted Emotion: {emotion}")


#
# from flask import Flask, request, render_template, redirect, url_for
# import soundfile as sf
# import numpy as np
# import torch
# from transformers import Wav2Vec2ForSequenceClassification, Wav2Vec2Processor
# import librosa
# import os
#
# # Initialize the Flask app
# app = Flask(__name__)
#
# # Load pre-trained model and processor
# model_name = "superb/wav2vec2-base-superb-er"
# model = Wav2Vec2ForSequenceClassification.from_pretrained(model_name)
# processor = Wav2Vec2Processor.from_pretrained(model_name)
#
# # Function to preprocess and predict emotion from audio
# def predict_emotion_from_audio(audio):
#     # Preprocess the audio file
#     inputs = processor(audio, sampling_rate=16000, return_tensors="pt", padding=True)
#
#     # Move tensors to GPU if available
#     if torch.cuda.is_available():
#         model.to("cuda")
#         inputs = {key: value.to("cuda") for key, value in inputs.items()}
#
#     # Make the prediction
#     with torch.no_grad():
#         logits = model(**inputs).logits
#
#     # Get the predicted label
#     predicted_id = torch.argmax(logits, dim=-1).item()
#     labels = model.config.id2label
#
#     return labels[predicted_id]
#
# # Route for the home page
# @app.route('/')
# def index():
#     return render_template('index.html')
#
# # Route for handling the audio file upload and prediction
# @app.route('/predict', methods=['POST'])
# def predict():
#     if 'audio_file' not in request.files:
#         return redirect(request.url)
#
#     file = request.files['audio_file']
#
#     if file.filename == '':
#         return redirect(request.url)
#
#     if file:
#         # Save the uploaded audio file
#         file_path = os.path.join('uploads', 'recorded_audio.wav')
#         file.save(file_path)
#
#         # Load the audio file and resample it to 16kHz
#         audio, sample_rate = librosa.load(file_path, sr=16000)
#
#         # Predict emotion
#         emotion = predict_emotion_from_audio(audio)
#
#         return render_template('result.html', emotion=emotion)
#
# if __name__ == "__main__":
#     if not os.path.exists('uploads'):
#         os.makedirs('uploads')
#     app.run(debug=True)
#
