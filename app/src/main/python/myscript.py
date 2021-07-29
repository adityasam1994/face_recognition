import face_recognition, os
from os.path import dirname, join
import pickle

def main(iname):
    adi_en = join(dirname(__file__), "adi.pkl")
    #robert = join(dirname(__file__), "adipic.jpg")

    #known_image = face_recognition.load_image_file(robert)
    unknown_image = face_recognition.load_image_file(iname)

    #robert_encoding = face_recognition.face_encodings(known_image)[0]
    robert_encoding = ""
    with open(adi_en, "rb") as f:
        robert_encoding = pickle.load(f)

    unknown_encoding = face_recognition.face_encodings(unknown_image)
    if len(unknown_encoding) > 0:
        unknown_encoding = unknown_encoding[0]
        results = face_recognition.compare_faces([robert_encoding], unknown_encoding)
        return str(results[0])
    else:
        return "No Face Found"
    #return str(os.environ["HOME"])