import streamlit as st
import pandas as pd
import numpy as np
import networkx as nx
import folium
from streamlit_folium import st_folium
import joblib
from datetime import datetime

# Load the dataset and trained model
df = pd.read_csv('kenya_crime_data.csv')
rf_model = joblib.load('../crime_assessment_model.pkl')

# Create a graph
G = nx.Graph()

# Define a grid of nodes (latitude and longitude)
latitude_range = np.linspace(df['Latitude'].min(), df['Latitude'].max(), 100)
longitude_range = np.linspace(df['Longitude'].min(), df['Longitude'].max(), 100)

nodes = {}
node_id = 1
for lat in latitude_range:
    for lon in longitude_range:
        nodes[node_id] = (lat, lon)
        G.add_node(node_id, pos=(lat, lon))
        node_id += 1

# Add edges with weights based on crime risk
for i in range(1, node_id):
    for j in range(i + 1, node_id):
        distance = np.linalg.norm(np.array(nodes[i]) - np.array(nodes[j]))
        if distance < 0.05:
            mid_point_lat = (nodes[i][0] + nodes[j][0]) / 2
            mid_point_lon = (nodes[i][1] + nodes[j][1]) / 2

            mid_point_features = pd.DataFrame({
                'Latitude': [mid_point_lat],
                'Longitude': [mid_point_lon],
                'Hour': [12],  # Assume a fixed hour
                'DayOfWeek': [3],  # Assume a fixed day
                'CrimeType_Robbery': [1],  # Assume a fixed crime type
                'Area_Nairobi': [1],  # Assume a fixed area
                'TimeOfDay_Afternoon': [1]  # Assume a fixed time of day
            })
            risk = rf_model.predict_proba(mid_point_features)[:, 1]  # Probability of risk

            G.add_edge(i, j, weight=risk[0])


# Function to find the safest route
def find_safest_route(G, start_node, end_node):
    return nx.dijkstra_path(G, source=start_node, target=end_node, weight='weight')


# Function to determine time of day based on the hour
def get_time_of_day(hour):
    if 5 <= hour < 12:
        return 'Morning'
    elif 12 <= hour < 17:
        return 'Afternoon'
    elif 17 <= hour < 21:
        return 'Evening'
    else:
        return 'Night'


# Streamlit Interface
st.title("Safe Route Finder")
st.subheader("Enter your starting and destination locations")

# Select start and end points
start_lat = st.number_input("Start Latitude", min_value=latitude_range.min(), max_value=latitude_range.max(),
                            value=latitude_range[0])
start_lon = st.number_input("Start Longitude", min_value=longitude_range.min(), max_value=longitude_range.max(),
                            value=longitude_range[0])
end_lat = st.number_input("Destination Latitude", min_value=latitude_range.min(), max_value=latitude_range.max(),
                          value=latitude_range[10])
end_lon = st.number_input("Destination Longitude", min_value=longitude_range.min(), max_value=longitude_range.max(),
                          value=longitude_range[10])

# Time input
use_current_time = st.checkbox("Use current time", value=True)

if use_current_time:
    now = datetime.now()
    hour = now.hour
    day_of_week = now.weekday()
else:
    hour = st.number_input("Enter hour of the day (0-23)", min_value=0, max_value=23, value=12)
    day_of_week = st.number_input("Enter day of the week (0=Monday, 6=Sunday)", min_value=0, max_value=6, value=3)

time_of_day = get_time_of_day(hour)


# Find nearest nodes to start and end locations
def find_nearest_node(lat, lon):
    distances = {node: np.linalg.norm(np.array(pos) - np.array([lat, lon])) for node, pos in nodes.items()}
    nearest_node = min(distances, key=distances.get)
    return nearest_node


start_node = find_nearest_node(start_lat, start_lon)
end_node = find_nearest_node(end_lat, end_lon)

# Find safest route and display it
if st.button("Find Safest Route"):
    safest_route = find_safest_route(G, start_node, end_node)
    safest_route_coords = [nodes[node] for node in safest_route]

    # Create a map centered on the start point
    map_safest_route = folium.Map(location=[start_lat, start_lon], zoom_start=12)

    # Add the safest route to the map
    folium.PolyLine(safest_route_coords, color="green", weight=2.5, opacity=1).add_to(map_safest_route)

    # Display the map
    st_folium(map_safest_route, width=700, height=500)
