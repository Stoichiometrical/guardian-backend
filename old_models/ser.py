# import requests
#
# API_URL = "https://api-inference.huggingface.co/models/ehcalabres/wav2vec2-lg-xlsr-en-speech-emotion-recognition"
# headers = {"Authorization": "Bearer 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"}
#
# def query(filename):
#     with open(filename, "rb") as f:
#         data = f.read()
#     response = requests.post(API_URL, headers=headers, data=data)
#     return response.json()
#
# output = query("sample1.flac")

# import streamlit as st
# import sounddevice as sd
# import numpy as np
# import requests
# import tempfile
# import wave
#
# # API details
# API_URL = "https://api-inference.huggingface.co/models/ehcalabres/wav2vec2-lg-xlsr-en-speech-emotion-recognition"
# headers = {"Authorization": "Bearer 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"}
#
#
# def query(filename):
#     with open(filename, "rb") as f:
#         data = f.read()
#     response = requests.post(API_URL, headers=headers, data=data)
#     return response.json()
#
#
# # Function to record audio
# def record_audio(duration=5, fs=16000):
#     st.info(f"Recording for {duration} seconds...")
#     audio = sd.rec(int(duration * fs), samplerate=fs, channels=1, dtype='int16')
#     sd.wait()  # Wait until recording is finished
#     return audio, fs
#
#
# # Function to save the audio to a temporary file
# def save_audio(audio, fs):
#     temp_file = tempfile.NamedTemporaryFile(delete=False, suffix=".wav")
#     with wave.open(temp_file.name, 'wb') as wf:
#         wf.setnchannels(1)  # mono
#         wf.setsampwidth(2)  # 16 bits per sample
#         wf.setframerate(fs)
#         wf.writeframes(audio)
#     return temp_file.name
#
#
# # Streamlit app
# st.title("Speech Emotion Recognition")
# st.write("Record your voice and let the AI predict your emotion!")
#
# # Audio recording button
# if st.button("Record Audio"):
#     audio, fs = record_audio()
#     st.success("Recording finished")
#
#     # Save the recorded audio to a temporary file
#     audio_file = save_audio(audio, fs)
#
#     # Send the audio file to the API and get the response
#     emotion_result = query(audio_file)
#
#     # Display the result
#     st.write("Predicted Emotion:")
#     st.write(emotion_result)
#

import streamlit as st
import sounddevice as sd
import numpy as np
import requests
import tempfile
import wave
from collections import Counter

# API details
API_URL = "https://api-inference.huggingface.co/models/ehcalabres/wav2vec2-lg-xlsr-en-speech-emotion-recognition"
headers = {"Authorization": "Bearer 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"}


def query(filename):
    with open(filename, "rb") as f:
        data = f.read()
    response = requests.post(API_URL, headers=headers, data=data)
    return response.json()


# Function to record audio
def record_audio(duration=10, fs=16000):
    st.info(f"Recording for {duration} seconds...")
    audio = sd.rec(int(duration * fs), samplerate=fs, channels=1, dtype='int16')
    sd.wait()  # Wait until recording is finished
    return audio, fs


# Function to save the audio to a temporary file
def save_audio(audio, fs):
    temp_file = tempfile.NamedTemporaryFile(delete=False, suffix=".wav")
    with wave.open(temp_file.name, 'wb') as wf:
        wf.setnchannels(1)  # mono
        wf.setsampwidth(2)  # 16 bits per sample
        wf.setframerate(fs)
        wf.writeframes(audio)
    return temp_file.name


# Function to analyze emotions and provide alerts
def analyze_emotions(emotion_list):
    emotion_counts = Counter(emotion_list)
    top_two = emotion_counts.most_common(2)

    if any(emotion == "anger" for emotion, _ in top_two) and any(emotion == "sad" for emotion, _ in top_two):
        st.warning("Danger, target might need help.")
    else:
        st.success("No immediate danger detected.")


# Streamlit app
st.title("Speech Emotion Recognition with Alert System")
st.write("Recording your voice for 30 seconds and analyzing emotions every 10 seconds.")

if st.button("Start Recording"):
    all_detected_emotions = []

    for i in range(3):
        # Record 10 seconds of audio
        audio, fs = record_audio()
        st.success(f"Recording segment {i + 1}/3 finished")

        # Save the recorded audio to a temporary file
        audio_file = save_audio(audio, fs)

        # Send the audio file to the API and get the response
        emotion_result = query(audio_file)
        st.write(f"Emotion detected in segment {i + 1}: {emotion_result}")

        # Extract the top emotion from the result
        top_emotion = max(emotion_result, key=lambda x: x['score'])['label']
        all_detected_emotions.append(top_emotion)

    # Analyze emotions and provide alerts if necessary
    analyze_emotions(all_detected_emotions)

