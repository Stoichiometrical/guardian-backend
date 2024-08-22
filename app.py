
import os
from dotenv import load_dotenv
from flask import Flask, request, jsonify
from flask import Flask, request, jsonify
import joblib
import numpy as np
import networkx as nx
import pandas as pd
from flask_cors import CORS
import soundfile as sf
import requests
import tempfile
import wave
import speech_recognition as sr
from langchain.chains.conversation.base import ConversationChain
from langchain_google_genai import GoogleGenerativeAI
from twilio.rest import Client

# Load environment variables from the .env file
load_dotenv()

app = Flask(__name__)
CORS(app)

# API details
API_URL = "https://api-inference.huggingface.co/models/ehcalabres/wav2vec2-lg-xlsr-en-speech-emotion-recognition"
headers = {"Authorization": f"Bearer {os.getenv('HUGGING_FACE_API_KEY')}"}
DEFAULT_PHONE_NUMBER = "whatsapp:+23058417209"

# Initialize the recognizer
recognizer = sr.Recognizer()

# List of emotions to detect
emotions = ['angry', 'calm', 'disgust', 'fearful', 'happy', 'neutral', 'sad', 'surprised']

# Define negative emotions
negative_emotions = {'angry', 'disgust', 'fearful', 'sad'}

# Twilio configuration
account_sid = os.getenv('TWILIO_ACCOUNT_SID')
auth_token = os.getenv('TWILIO_AUTH_TOKEN')
twilio_client = Client(account_sid, auth_token)

# Function to query the Hugging Face API for emotion recognition
# def query_emotion(filename):
#     try:
#         print("Starting emotion recognition query...")
#         with open(filename, "rb") as f:
#             data = f.read()
#         response = requests.post(API_URL, headers=headers, data=data, timeout=30)  # Increased timeout
#         print(f"Received response status: {response.status_code}")
#         response.raise_for_status()  # Raise an exception for HTTP errors
#         print(response.json())
#         return response.json()
#     except requests.exceptions.RequestException as e:
#         print(f"Error querying emotion recognition API: {e}")
#         raise

def query_emotion(filename):
    try:
        print("Starting emotion recognition query...")
        with open(filename, "rb") as f:
            data = f.read()
        response = requests.post(API_URL, headers=headers, data=data, timeout=30)
        print(f"Received response status: {response.status_code}")
        print(f"Response content: {response.text}")  # Log the full response content
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
        prompt = f"Analyze the following audio text and determine if there's any cause for concern: {content}"
        # Initialize Google Generative AI
        client = GoogleGenerativeAI(model="gemini-pro", google_api_key=os.getenv('GOOGLE_API_KEY'))
        chain = ConversationChain(llm=client)
        response = client.generate([prompt])
        analysis = response.generations[0][0].text
        print("Analysis with Gemini completed.")
        return analysis
    except Exception as e:
        print(f"Error analyzing with Gemini: {e}")
        raise

# Function to send WhatsApp alert using Twilio
def send_whatsapp_alert(message_body, to_number):
    try:
        message = twilio_client.messages.create(
            from_='whatsapp:+14155238886',
            body=message_body,
            to=to_number
        )
        print(f"WhatsApp message sent: {message.sid}")
        return message.sid
    except Exception as e:
        print(f"Error sending WhatsApp message: {e}")
        raise

# Endpoint to process audio, recognize emotions, transcribe speech, and analyze with Gemini
@app.route('/analyze_audio', methods=['POST'])
def analyze_audio():
    try:
        # Save the uploaded audio file
        audio_file = request.files['file']
        temp_file = tempfile.NamedTemporaryFile(delete=False, suffix=".wav")
        audio_file.save(temp_file.name)

        # Log file details
        print(f"Saved file to: {temp_file.name}")

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

        # Step 4: Determine if there's a danger and send WhatsApp alert
        response_data = {
            "transcript": transcript if transcript else "Voices could not be understood clearly",
            "analysis": analysis
        }
        if "danger" in analysis.lower() or "help" in analysis.lower():
            response_data["alert"] = "Danger detected, alerting authorities!"
            # Send WhatsApp alert
            send_whatsapp_alert(analysis, 'whatsapp:+23058417209')
        else:
            response_data["alert"] = "No immediate danger detected."

        return jsonify(response_data), 200

    except Exception as e:
        return jsonify({"error": str(e)}), 500


model = joblib.load('crime_prediction_model.pkl')

# Define towns and routes (same as provided)
towns = {
    'Nairobi': (-1.286389, 36.817223),
    'Mombasa': (-4.043477, 39.668206),
    'Nakuru': (-0.303099, 36.066285),
    'Eldoret': (0.514277, 35.269065),
    'Kisumu': (-0.091702, 34.767956),
    'Nyeri': (-0.421250, 36.945752),
    'Machakos': (-1.516820, 37.266485),
    'Kericho': (-0.366690, 35.291340),
    'Meru': (-0.047200, 37.648000),
    'Kitale': (1.002559, 34.986032),
    'Garissa': (-0.456550, 39.664640),
    'Isiolo': (0.353073, 37.582666),
    'Bungoma': (0.591278, 34.564658),
    'Wajir': (1.737327, 40.065940),
    'Mandera': (3.930530, 41.855910),
    'Malindi': (-3.219186, 40.116944),
    'Lamu': (-2.271189, 40.902012),
    'Thika': (-1.033349, 37.069328),
    'Namanga': (-2.545290, 36.792530),
    'Kitui': (-1.374818, 38.010555),
    'Naivasha': (-0.707222, 36.431944),
    'Narok': (-1.078850, 35.860000),
    'Busia': (0.4605, 34.1115),
    'Bomet': (-0.7827, 35.3428),
    'Marsabit': (2.3342, 37.9891),
    'Voi': (-3.3962, 38.5565)
}

routes = [
    ('Nairobi', 'Mombasa', 'A109'),
    ('Nairobi', 'Nakuru', 'A104'),
    ('Nairobi', 'Eldoret', 'A104'),
    ('Nairobi', 'Nyeri', 'B5'),
    ('Nairobi', 'Garissa', 'A3'),
    ('Mombasa', 'Malindi', 'A14'),
    ('Mombasa', 'Garissa', 'B8'),
    ('Nakuru', 'Eldoret', 'A104'),
    ('Nakuru', 'Kericho', 'B1'),
    ('Nakuru', 'Nyeri', 'B5'),
    ('Eldoret', 'Kitale', 'B2'),
    ('Eldoret', 'Kisumu', 'A104'),
    ('Kisumu', 'Bungoma', 'A1'),
    ('Kisumu', 'Busia', 'A1'),
    ('Kisumu', 'Kericho', 'B1'),
    ('Nyeri', 'Nairobi', 'B5'),
    ('Nyeri', 'Meru', 'B6'),
    ('Garissa', 'Wajir', 'A3'),
    ('Garissa', 'Mandera', 'A3'),
    ('Kitale', 'Bungoma', 'A1'),
    ('Machakos', 'Nairobi', 'B6'),
    ('Machakos', 'Mombasa', 'A109'),
    ('Machakos', 'Kitui', 'B7'),
    ('Meru', 'Isiolo', 'A2'),
    ('Meru', 'Nairobi', 'B6'),
    ('Kericho', 'Nakuru', 'B1'),
    ('Kericho', 'Kisumu', 'B1'),
    ('Isiolo', 'Marsabit', 'A2'),
    ('Isiolo', 'Garissa', 'A2'),
    ('Bungoma', 'Kitale', 'A1'),
    ('Bungoma', 'Busia', 'A1'),
    ('Wajir', 'Garissa', 'A3'),
    ('Wajir', 'Mandera', 'A3'),
    ('Mandera', 'Wajir', 'A3'),
    ('Thika', 'Nairobi', 'A2'),
    ('Thika', 'Nyeri', 'A2'),
    ('Naivasha', 'Nairobi', 'B3'),
    ('Naivasha', 'Narok', 'C88'),
    ('Lamu', 'Malindi', 'C112')
]


def predict_crime_probability(day_of_week, hour_of_day):
    X_new = np.array([[day_of_week, hour_of_day]])
    predicted_risk = model.predict_proba(X_new)[0][1]  # Probability of crime occurring
    return predicted_risk


def find_safest_route(origin, destination, day_of_week, hour_of_day):
    G = nx.Graph()

    # Add edges with predicted crime probabilities as weights
    for route in routes:
        o, d, road_name = route
        crime_prob = predict_crime_probability(day_of_week, hour_of_day)
        G.add_edge(o, d, weight=crime_prob, road=road_name)

    # Find the safest path (path with the lowest crime risk)
    safest_path = nx.shortest_path(G, source=origin, target=destination, weight='weight')

    # Build route description
    route_description = []
    for i in range(len(safest_path) - 1):
        edge_data = G.get_edge_data(safest_path[i], safest_path[i + 1])
        route_description.append(f"Take {edge_data['road']} from {safest_path[i]} to {safest_path[i + 1]}")

    return safest_path, route_description


@app.route('/predict_crime', methods=['POST'])
def predict_crime():
    data = request.get_json()
    day_of_week = data.get('day_of_week')
    hour_of_day = data.get('hour_of_day')

    if day_of_week is None or hour_of_day is None:
        return jsonify({"error": "Missing parameters"}), 400

    risk = predict_crime_probability(day_of_week, hour_of_day)
    return jsonify({"crime_risk_probability": risk})


@app.route('/find_safest_route', methods=['POST'])
def safest_route():
    data = request.get_json()
    origin = data.get('origin')
    destination = data.get('destination')
    day_of_week = data.get('day_of_week')
    hour_of_day = data.get('hour_of_day')

    if not origin or not destination or day_of_week is None or hour_of_day is None:
        return jsonify({"error": "Missing parameters"}), 400

    safest_path, route_description = find_safest_route(origin, destination, day_of_week, hour_of_day)

    return jsonify({
        "safest_path": safest_path,
        "route_description": route_description
    })


#Alert
@app.route('/send-alert', methods=['POST'])
def send_alert():
    data = request.get_json()

    # Get description and optional phone number from request
    message_body = data.get('description')
    to_number = data.get('phone_number', DEFAULT_PHONE_NUMBER)

    if not message_body:
        return jsonify({'error': 'Description is required'}), 400

    try:
        # Send the alert
        message_sid = send_whatsapp_alert(message_body, to_number)
        return jsonify({'message_sid': message_sid}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500


if __name__ == '__main__':
    app.run(port=5000)


# import os
# from dotenv import load_dotenv
# from flask import Flask, request, jsonify
# from flask import Flask, request, jsonify
# import joblib
# import numpy as np
# import networkx as nx
# import pandas as pd
# from flask_cors import CORS
# import soundfile as sf
# import requests
# import tempfile
# import wave
# import speech_recognition as sr
# from langchain.chains.conversation.base import ConversationChain
# from langchain_google_genai import GoogleGenerativeAI
# from twilio.rest import Client
#
# # Load environment variables from the .env file
# load_dotenv()
#
# app = Flask(__name__)
# CORS(app)
#
# # API details
# API_URL = "https://api-inference.huggingface.co/models/ehcalabres/wav2vec2-lg-xlsr-en-speech-emotion-recognition"
# headers = {"Authorization": f"Bearer {os.getenv('HUGGING_FACE_API_KEY')}"}
#
# # Initialize the recognizer
# recognizer = sr.Recognizer()
#
# # List of emotions to detect
# emotions = ['angry', 'calm', 'disgust', 'fearful', 'happy', 'neutral', 'sad', 'surprised']
#
# # Define negative emotions
# negative_emotions = {'angry', 'disgust', 'fearful', 'sad'}
#
# # Twilio configuration
# account_sid = os.getenv('TWILIO_ACCOUNT_SID')
# auth_token = os.getenv('TWILIO_AUTH_TOKEN')
# twilio_client = Client(account_sid, auth_token)
#
# # Function to query the Hugging Face API for emotion recognition
# def query_emotion(filename):
#     try:
#         print("Starting emotion recognition query...")
#         with open(filename, "rb") as f:
#             data = f.read()
#         response = requests.post(API_URL, headers=headers, data=data, timeout=30)  # Increased timeout
#         print(f"Received response status: {response.status_code}")
#         response.raise_for_status()  # Raise an exception for HTTP errors
#         print(response.json())
#         return response.json()
#     except requests.exceptions.RequestException as e:
#         print(f"Error querying emotion recognition API: {e}")
#         raise
#
# # Function to transcribe audio to text using SpeechRecognition
# def transcribe_audio(audio_file):
#     try:
#         print("Starting audio transcription with SpeechRecognition...")
#         with sr.AudioFile(audio_file) as source:
#             audio_data = recognizer.record(source)
#             transcription = recognizer.recognize_google(audio_data)
#         print("Audio transcription completed.")
#         print(f"Transcription: {transcription}")
#         return transcription
#     except Exception as e:
#         print(f"Error transcribing audio: {e}")
#         raise
#
# # Function to analyze the transcript or emotions using Gemini/LLM
# def analyze_with_gemini(content):
#     try:
#         print("Starting analysis with Gemini...")
#         prompt = f"Analyze the following audio text and determine if there's any cause for concern.If the person might be in danger, use safe if they are not in danger.If they are in Write a whatsapp message saying.Hello , this is my safe tracking alert, l might be in danger.Then  you briefly summarise the audio text. if audio could not be heard properly just check if the emotions are concerning,is yes, indicate danger. Your response should model a typical whatsapp message like you are talking to a person and informing them of a concerning situation, you are reporting so encourage them to either conatc the police or check in on them: {content}"
#         # Initialize Google Generative AI
#         client = GoogleGenerativeAI(model="gemini-pro", google_api_key=os.getenv('GOOGLE_API_KEY'))
#         chain = ConversationChain(llm=client)
#         response = client.generate([prompt])
#         analysis = response.generations[0][0].text
#         print("Analysis with Gemini completed.")
#         return analysis
#     except Exception as e:
#         print(f"Error analyzing with Gemini: {e}")
#         raise
#
# # Function to send WhatsApp alert using Twilio
# def send_whatsapp_alert(message_body, to_number):
#     try:
#         message = twilio_client.messages.create(
#             from_='whatsapp:+14155238886',
#             body=message_body,
#             to=to_number
#         )
#         print(f"WhatsApp message sent: {message.sid}")
#         return message.sid
#     except Exception as e:
#         print(f"Error sending WhatsApp message: {e}")
#         raise
#
# # Endpoint to process audio, recognize emotions, transcribe speech, and analyze with Gemini
# @app.route('/analyze_audio', methods=['POST'])
# def analyze_audio():
#     try:
#         # Save the uploaded audio file
#         audio_file = request.files['file']
#         temp_file = tempfile.NamedTemporaryFile(delete=False, suffix=".wav")
#         audio_file.save(temp_file.name)
#
#         # Step 1: Emotion Recognition
#         try:
#             emotion_result = query_emotion(temp_file.name)
#             sorted_emotions = sorted(emotion_result, key=lambda x: x['score'], reverse=True)
#             top_two_emotions = sorted_emotions[:2]
#             top_emotion_labels = [emotion['label'] for emotion in top_two_emotions]
#             print(f"Top two emotions: {top_emotion_labels}")
#
#             # Check if top two emotions are negative
#             if all(emotion in negative_emotions for emotion in top_emotion_labels):
#                 print("Truuuuu")
#                 analyze_text = True
#             else:
#                 analyze_text = False
#
#         except Exception as e:
#             return jsonify({"error": f"Emotion recognition failed: {str(e)}"}), 500
#
#         # Step 2: Transcribe Audio to Text
#         transcript = None
#         if analyze_text:
#             try:
#                 transcript = transcribe_audio(temp_file.name)
#             except Exception as e:
#                 print(f"Audio transcription failed: {str(e)}")
#                 transcript = None
#
#         # Step 3: Analyze with Gemini
#         if analyze_text and transcript:
#             try:
#                 analysis = analyze_with_gemini(transcript)
#             except Exception as e:
#                 return jsonify({"error": f"Text analysis with Gemini failed: {str(e)}"}), 500
#         else:
#             try:
#                 # If transcription failed, analyze the top two emotions instead
#                 emotions_text = " and ".join(top_emotion_labels)
#                 analysis = analyze_with_gemini(f"The detected emotions were: {emotions_text}.")
#             except Exception as e:
#                 return jsonify({"error": f"Emotion-based analysis with Gemini failed: {str(e)}"}), 500
#
#         # Step 4: Determine if there's a danger and send WhatsApp alert
#         response_data = {
#             "transcript": transcript if transcript else "Voices could not be understood clearly",
#             "analysis": analysis
#         }
#         if "danger" in analysis.lower() or "help" in analysis.lower():
#             response_data["alert"] = "Danger detected, alerting authorities!"
#             # Uncomment and provide a valid phone number for actual use
#             # send_whatsapp_alert(analysis, 'whatsapp:+23058417209')
#         else:
#             response_data["alert"] = "No immediate danger detected."
#
#         return jsonify(response_data), 200
#
#     except Exception as e:
#         return jsonify({"error": str(e)}), 500
#






# Load the trained model





