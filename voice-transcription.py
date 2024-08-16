import streamlit as st
import speech_recognition as sr
import datetime

# Initialize recognizer
recognizer = sr.Recognizer()


def transcribe_audio():
    with sr.Microphone() as source:
        st.write("Listening...")
        # Adjust for ambient noise and record audio
        recognizer.adjust_for_ambient_noise(source)
        audio = recognizer.listen(source)

        try:
            # Transcribe audio to text
            text = recognizer.recognize_google(audio)
            st.write("You said: " + text)

            # Save transcription to file with timestamp
            timestamp = datetime.datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
            filename = f"transcription_{timestamp}.txt"
            with open(filename, "w") as file:
                file.write(text)

            st.write(f"Transcription saved to {filename}")
        except sr.UnknownValueError:
            st.write("Sorry, I could not understand the audio.")
        except sr.RequestError as e:
            st.write(f"Error with the recognition service; {e}")


def main():
    st.title("Voice Transcription App")
    st.write("Click the button below to start recording.")

    if st.button("Start Recording"):
        transcribe_audio()


if __name__ == "__main__":
    main()
