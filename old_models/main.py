

import warnings
import networkx as nx
from geopy.distance import geodesic
from datetime import datetime
import pandas as pd
import folium
import pickle
from flask import Flask, jsonify, request, send_file
import io

# Ignore all warnings
warnings.filterwarnings("ignore")

app = Flask(__name__)

# Load the town data from a CSV file
def load_town_data(file_path='towns_data.csv'):
    return pd.read_csv(file_path)

# Function to load the model and calculate the safest route
def load_model_and_calculate_safest_route(towns_df, start_town, end_town, travel_date, model_filename='crime_risk_model.pkl'):
    # Load the trained model
    with open(model_filename, 'rb') as file:
        model = pickle.load(file)

    # Build the graph
    G = nx.Graph()
    for i, town_a in towns_df.iterrows():
        for j, town_b in towns_df.iterrows():
            if i != j:
                # Calculate the geodesic distance between the towns
                distance = geodesic((town_a['Latitude'], town_a['Longitude']),
                                    (town_b['Latitude'], town_b['Longitude'])).km
                # Prepare the feature set for the model prediction
                features = [town_a['Latitude'], town_a['Longitude'], travel_date.weekday(), travel_date.hour]
                # Predict the crime risk using the loaded model
                crime_risk = model.predict([features])[0]
                # Add an edge between the towns with a weight combining distance and crime risk
                G.add_edge(town_a['Town'], town_b['Town'], weight=crime_risk + distance)

    # Find the safest route based on the shortest path in terms of the 'weight'
    safest_route = nx.shortest_path(G, source=start_town, target=end_town, weight='weight')

    # Create a map showing the safest route
    m = folium.Map(location=[0.0236, 37.9062], zoom_start=6)

    # Plot each town in the route
    for town in safest_route:
        town_data = towns_df[towns_df['Town'] == town].iloc[0]
        folium.Marker(
            location=[town_data['Latitude'], town_data['Longitude']],
            popup=town,
            icon=folium.Icon(color='blue')
        ).add_to(m)

    # Draw lines between the towns in the route
    for i in range(len(safest_route) - 1):
        town_a = towns_df[towns_df['Town'] == safest_route[i]].iloc[0]
        town_b = towns_df[towns_df['Town'] == safest_route[i + 1]].iloc[0]
        folium.PolyLine(
            locations=[(town_a['Latitude'], town_a['Longitude']), (town_b['Latitude'], town_b['Longitude'])],
            color='blue'
        ).add_to(m)

    return safest_route, m

# Flask route to calculate and return the safest route
@app.route('/safest_route', methods=['GET'])
def get_safest_route():
    start_town = request.args.get('start_town')
    end_town = request.args.get('end_town')
    travel_date_str = request.args.get('travel_date')  # Expecting 'YYYY-MM-DD HH:MM' format
    travel_date = datetime.strptime(travel_date_str, '%Y-%m-%d %H:%M')

    # Load the town data from CSV
    kenya_towns = load_town_data('../towns_data.csv')

    # Calculate the safest route
    safest_route, route_map = load_model_and_calculate_safest_route(kenya_towns, start_town, end_town, travel_date)

    # Save the map to a binary object to send it via Flask
    map_html = io.BytesIO()
    route_map.save(map_html, close_file=False)
    map_html.seek(0)

    return jsonify({'safest_route': safest_route}), send_file(map_html, mimetype='text/html')

# Example Flask route to test the server
@app.route('/')
def index():
    return "Safest Route API is running!"

if __name__ == '__main__':
    app.run(debug=True)



# import streamlit as st
# import torchaudio
# import torch
# from transformers import Wav2Vec2ForCTC, Wav2Vec2Tokenizer, pipeline
#
#
# # Load pre-trained models for transcription and emotion recognition
# @st.cache_resource
# def load_models():
#     tokenizer = Wav2Vec2Tokenizer.from_pretrained("facebook/wav2vec2-base-960h")
#     model = Wav2Vec2ForCTC.from_pretrained("facebook/wav2vec2-base-960h")
#     emotion_classifier = pipeline("text-classification", model="j-hartmann/emotion-english-distilroberta-base",
#                                   return_all_scores=True)
#     return tokenizer, model, emotion_classifier
#
#
# tokenizer, model, emotion_classifier = load_models()
#
#
# # Function to transcribe audio
# def transcribe_audio(audio_data):
#     input_values = tokenizer(audio_data, return_tensors="pt", padding="longest").input_values
#     logits = model(input_values).logits
#     predicted_ids = torch.argmax(logits, dim=-1)
#     transcription = tokenizer.batch_decode(predicted_ids)[0].lower()
#     return transcription
#
#
# # Streamlit app interface
# st.title("Emotion Recognition App")
#
# # Record or upload audio file
# audio_file = st.file_uploader("Upload an audio file", type=["wav", "mp3"])
#
# if audio_file is not None:
#     # Load the audio file
#     waveform, sample_rate = torchaudio.load(audio_file)
#
#     # Transcribe the audio
#     with st.spinner('Transcribing audio...'):
#         transcription = transcribe_audio(waveform.squeeze().numpy())
#
#     st.write("Transcription:")
#     st.write(transcription)
#
#     # Perform emotion recognition
#     with st.spinner('Analyzing emotion...'):
#         emotion_scores = emotion_classifier(transcription)
#         emotion = max(emotion_scores[0], key=lambda x: x['score'])
#
#     st.write("Detected Emotion:")
#     st.write(f"**{emotion['label']}** with a confidence of **{emotion['score']:.2f}**")
#
#     # Display emotion scores
#     st.write("Emotion Scores:")
#     for score in emotion_scores[0]:
#         st.write(f"{score['label']}: {score['score']:.2f}")
#