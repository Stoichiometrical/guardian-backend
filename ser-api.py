

from flask import Flask, request, jsonify
from flask_cors import CORS
import soundfile as sf
import requests
import tempfile
import wave
import speech_recognition as sr
from langchain.chains.conversation.base import ConversationChain
from langchain_google_genai import GoogleGenerativeAI

app = Flask(__name__)
CORS(app)

# API details
API_URL = "https://api-inference.huggingface.co/models/ehcalabres/wav2vec2-lg-xlsr-en-speech-emotion-recognition"
headers = {"Authorization": "Bearer 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"}

# Initialize the recognizer
recognizer = sr.Recognizer()

# List of emotions to detect
emotions = ['angry', 'calm', 'disgust', 'fearful', 'happy', 'neutral', 'sad', 'surprised']

# Define negative emotions
negative_emotions = {'angry', 'disgust', 'fearful', 'sad'}

# Function to query the Hugging Face API for emotion recognition
def query_emotion(filename):
    try:
        print("Starting emotion recognition query...")
        with open(filename, "rb") as f:
            data = f.read()
        response = requests.post(API_URL, headers=headers, data=data, timeout=30)  # Increased timeout
        print(f"Received response status: {response.status_code}")
        response.raise_for_status()  # Raise an exception for HTTP errors
        print(response.json())
        return response.json()
    except requests.exceptions.RequestException as e:
        print(f"Error querying emotion recognition API: {e}")
        raise

# Function to transcribe audio to text using SpeechRecognition
def transcribe_audio(audio_file):
    try:
        print("Starting audio transcription with SpeechRecognition...")
        with sr.AudioFile(audio_file) as source:
            audio_data = recognizer.record(source)
            transcription = recognizer.recognize_google(audio_data)
        print("Audio transcription completed.")
        print(f"Transcription: {transcription}")
        return transcription
    except Exception as e:
        print(f"Error transcribing audio: {e}")
        raise

# Function to analyze the transcript or emotions using Gemini/LLM
def analyze_with_gemini(content):
    try:
        print("Starting analysis with Gemini...")
        prompt = f"Analyze the following and determine if there's any cause for concern, if concern just respond person might be in danger check on them .Then give brief description of what might be happening based on audio transcript: {content}"
        # Initialize Google Generative AI
        client = GoogleGenerativeAI(model="gemini-pro", google_api_key="AIzaSyBjPYCxTeXake-2xFrqTteWw0fH4Tppq-E")
        chain = ConversationChain(llm=client)
        response = client.generate([prompt])
        analysis = response.generations[0][0].text
        print("Analysis with Gemini completed.")
        return analysis
    except Exception as e:
        print(f"Error analyzing with Gemini: {e}")
        raise

# Endpoint to process audio, recognize emotions, transcribe speech, and analyze with Gemini
@app.route('/analyze_audio', methods=['POST'])
def analyze_audio():
    try:
        # Save the uploaded audio file
        audio_file = request.files['file']
        temp_file = tempfile.NamedTemporaryFile(delete=False, suffix=".wav")
        audio_file.save(temp_file.name)

        # Step 1: Emotion Recognition
        try:
            emotion_result = query_emotion(temp_file.name)
            sorted_emotions = sorted(emotion_result, key=lambda x: x['score'], reverse=True)
            top_two_emotions = sorted_emotions[:2]
            top_emotion_labels = [emotion['label'] for emotion in top_two_emotions]
            print(f"Top two emotions: {top_emotion_labels}")
        except Exception as e:
            return jsonify({"error": f"Emotion recognition failed: {str(e)}"}), 500

        # Step 2: Transcribe Audio to Text
        transcript = None
        try:
            transcript = transcribe_audio(temp_file.name)
        except Exception as e:
            print(f"Audio transcription failed: {str(e)}")
            transcript = None

        # Step 3: Analyze with Gemini
        if transcript:
            try:
                analysis = analyze_with_gemini(transcript)
            except Exception as e:
                return jsonify({"error": f"Text analysis with Gemini failed: {str(e)}"}), 500
        else:
            try:
                # If transcription failed, analyze the top two emotions instead
                emotions_text = " and ".join(top_emotion_labels)
                analysis = analyze_with_gemini(f"The detected emotions were: {emotions_text}.")
            except Exception as e:
                return jsonify({"error": f"Emotion-based analysis with Gemini failed: {str(e)}"}), 500

        # Step 4: Determine if there's a danger and return both transcript and analysis
        response_data = {
            "transcript": transcript if transcript else "Transcription not available.",
            "analysis": analysis
        }
        if "danger" in analysis.lower() or "help" in analysis.lower():
            response_data["alert"] = "Danger detected, alerting authorities!"
        else:
            response_data["alert"] = "No immediate danger detected."
        return jsonify(response_data), 200

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(port=5000)

