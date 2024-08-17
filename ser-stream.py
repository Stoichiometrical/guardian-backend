# import streamlit as st
# import requests
# import tempfile
# import sounddevice as sd
# import soundfile as sf
#
# # Set up the Streamlit app
# st.title("Real-Time Speech Emotion and Safety Analysis")
#
# # Record audio using Streamlit
# def record_audio(duration=10, fs=16000):
#     st.info(f"Recording for {duration} seconds...")
#     audio = sd.rec(int(duration * fs), samplerate=fs, channels=1, dtype='int16')
#     sd.wait()  # Wait until recording is finished
#     return audio, fs
#
# # Streamlit interface
# if st.button("Record and Analyze"):
#     # Record audio
#     audio, fs = record_audio()
#
#     # Save the recorded audio to a temporary file
#     temp_file = tempfile.NamedTemporaryFile(delete=False, suffix=".wav")
#     sf.write(temp_file.name, audio, fs)
#
#     # Send the audio file to the Flask API
#     with open(temp_file.name, 'rb') as f:
#         files = {'file': f}
#         response = requests.post("http://127.0.0.1:5000/analyze_audio", files=files)
#
#     # Display the result from the Flask API
#     if response.status_code == 200:
#         result = response.json()
#         st.write("**Transcript:**")
#         st.write(result.get("transcript", "Transcript not available."))
#         st.write("**Analysis:**")
#         st.write(result.get("analysis", "Analysis not available."))
#         st.write("**Alert:**")
#         st.write(result.get("alert", "No alert available."))
#     else:
#         st.error(f"Error occurred while processing the audio: {response.text}")
#
# # Additional feature to upload an audio file
# st.write("Or you can upload an audio file to analyze:")
# uploaded_file = st.file_uploader("Choose a file...", type=["wav"])
#
# if uploaded_file is not None:
#     with tempfile.NamedTemporaryFile(delete=False, suffix=".wav") as temp_file:
#         temp_file.write(uploaded_file.getbuffer())
#
#     # Send the uploaded file to the Flask API
#     with open(temp_file.name, 'rb') as f:
#         files = {'file': f}
#         response = requests.post("http://127.0.0.1:5000/analyze_audio", files=files)
#
#     # Display the result from the Flask API
#     if response.status_code == 200:
#         result = response.json()
#         st.write("**Transcript:**")
#         st.write(result.get("transcript", "Transcript not available."))
#         st.write("**Analysis:**")
#         st.write(result.get("analysis", "Analysis not available."))
#         st.write("**Alert:**")
#         st.write(result.get("alert", "No alert available."))
#     else:
#         st.error(f"Error occurred while processing the audio: {response.text}")


import streamlit as st
import requests
import tempfile
import sounddevice as sd
import soundfile as sf

# Set up the Streamlit app
st.title("Real-Time Speech Emotion and Safety Analysis")

# Record audio using Streamlit
def record_audio(duration=10, fs=16000):
    st.info(f"Recording for {duration} seconds...")
    audio = sd.rec(int(duration * fs), samplerate=fs, channels=1, dtype='int16')
    sd.wait()  # Wait until recording is finished
    return audio, fs

# Streamlit interface
if st.button("Record and Analyze"):
    # Record audio
    audio, fs = record_audio()

    # Save the recorded audio to a temporary file
    temp_file = tempfile.NamedTemporaryFile(delete=False, suffix=".wav")
    sf.write(temp_file.name, audio, fs)

    # Send the audio file to the Flask API
    with open(temp_file.name, 'rb') as f:
        files = {'file': f}
        response = requests.post("http://127.0.0.1:5000/analyze_audio", files=files)

    # Display the result from the Flask API
    if response.status_code == 200:
        result = response.json()
        st.write("**Transcript:**")
        st.write(result.get("transcript", "Transcript not available."))
        st.write("**Analysis:**")
        st.write(result.get("analysis", "Analysis not available."))
        st.write("**Alert:**")
        st.write(result.get("alert", "No alert available."))
    else:
        st.error(f"Error occurred while processing the audio: {response.text}")

# Additional feature to upload an audio file
st.write("Or you can upload an audio file to analyze:")
uploaded_file = st.file_uploader("Choose a file...", type=["wav"])

if uploaded_file is not None:
    with tempfile.NamedTemporaryFile(delete=False, suffix=".wav") as temp_file:
        temp_file.write(uploaded_file.getbuffer())

    # Send the uploaded file to the Flask API
    with open(temp_file.name, 'rb') as f:
        files = {'file': f}
        response = requests.post("http://127.0.0.1:5000/analyze_audio", files=files)

    # Display the result from the Flask API
    if response.status_code == 200:
        result = response.json()
        st.write("**Transcript:**")
        st.write(result.get("transcript", "Transcript not available."))
        st.write("**Analysis:**")
        st.write(result.get("analysis", "Analysis not available."))
        st.write("**Alert:**")
        st.write(result.get("alert", "No alert available."))
    else:
        st.error(f"Error occurred while processing the audio: {response.text}")
