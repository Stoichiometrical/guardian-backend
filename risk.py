from flask import Flask, request, jsonify
import joblib
import numpy as np
import networkx as nx
import pandas as pd

# Initialize Flask app
app = Flask(__name__)

# Load the trained model
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


if __name__ == '__main__':
    app.run(debug=True)
