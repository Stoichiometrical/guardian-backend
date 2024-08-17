import streamlit as st
import sounddevice as sd
import numpy as np
import librosa
from transformers import pipeline

# Initialize the emotion recognition pipeline
emotion_model = pipeline("sentiment-analysis", model="j-hartmann/emotion-english-distilroberta-base")


# Define a function to record audio
def record_audio(duration=5, fs=44100):
    st.info(f"Recording for {duration} seconds...")
    audio = sd.rec(int(duration * fs), samplerate=fs, channels=1)
    sd.wait()  # Wait until recording is finished
    audio = np.squeeze(audio)
    return audio, fs


# Define a function to extract features from the audio
def extract_features(audio, fs):
    mfccs = librosa.feature.mfcc(y=audio, sr=fs, n_mfcc=13)
    return np.mean(mfccs.T, axis=0)


# Define the Streamlit interface
def main():
    st.title("Speech Emotion Recognition")

    # Record button
    if st.button("Record Audio"):
        audio, fs = record_audio()
        st.success("Recording finished")

        # Save the audio as a temporary file
        librosa.output.write_wav('temp_audio.wav', audio, fs)

        # Analyze emotion
        with open("temp_audio.wav", "rb") as audio_file:
            result = emotion_model(audio_file.read())

        st.write("Detected Emotion:")
        st.write(result[0]["label"])
        st.write("Confidence Score:")
        st.write(result[0]["score"])


if __name__ == "__main__":
    main()
