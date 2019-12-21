
from time import sleep # Import the sleep function from the time module

import time
import face_recognition
import numpy as np
from PIL import Image, ImageDraw
from IPython.display import display
from datetime import datetime,timedelta
import glob
import os,shutil
import pathlib
import firebase_admin
from firebase_admin import credentials,firestore
from firebase_admin import storage as admin_storage
import urllib.request
import codecs,json

import pyrebase
from bleach._vendor.html5lib._ihatexml import name

def downloadAdminPics():  # store admin pics to a certain folder for face recognition
    global firebase
    global bucket_1
    global datadir
    
    print("download started")
    storage = firebase.storage()
    # storage.child("images/example.jpg").download("downloaded.jpg")
#     datadir = '/home/pi/Desktop/Admin/'
    try:
        shutil.rmtree(datadir)
    except:
        pass
    # all_files = storage.child("door_6768/profiles/").list_files()
    image_urls = []
#     print(bucket_1)
    namelist=[]
    for blob in bucket_1.list_blobs():
        name = str(blob.name)
        #print(name)
        if "door_6768/profiles/" in name:  # '''and ("png" or "jpg")'''
            #print(name)
            blob_img = bucket_1.blob(name)
            X_url = blob_img.generate_signed_url(timedelta(seconds=300), method='GET')
            # print(X_url)
            image_urls.append(X_url)
            namelist.append(name.split("door_6768/profiles/")[1])

    PATH = [datadir]
    for path in PATH:
        i = 0
        for url in image_urls:
            if os.path.exists(path):
                pass
            else:
                pathlib.Path(path).mkdir()
            if i !=0:
                #name_img = str(path + "image" + str(i) + ".jpg")
                name_img = str(path + namelist[i])
                urllib.request.urlretrieve(url, name_img)
            i += 1
def learnFaces():
    global doc_ref
    #  LEARN ADMIN'S FACES
    # Load a sample picture and learn how to recognize it.

    images =[]
    known_face_encodings =[]
    print("Started"+ datadir)
    files = glob.glob(datadir +"*.jpg")
    files2 = glob.glob(datadir + "*.png")
    files.extend(files2)
    # print("\n".join(files))

#     for file in files:
#         img = Image.open(file)
#         img.save(file,optimize=True,quality=0)
#         img.close()
#     print("image resized")
    # printing out files in sorted form
    known_face_names = [x.split(datadir)[1].split(".jpg")[0] for x in files]
    print(known_face_names)
    for file in files:
        images.append(face_recognition.load_image_file(file));
    #print(images)
    for encoding in images:
        known_face_encodings.append(face_recognition.face_encodings(encoding)[0].tolist())
#     print(known_face_encodings)
    with open("face_tags.json","w") as json_file:
        json.dump(known_face_encodings,json_file)
#     json.dump(known_face_encodings,codecs.open("face_tags.json", 'w', encoding='utf-8'), separators=(',', ':'),indent=4)

    print('Learned encoding for', len(known_face_encodings), 'images.')
###################################################################
#  SETTING VARIABLES
###################################################################
lastImage=""
# datadir = '/home/pi/Desktop/Admin/'
datadir = "C:\\Users\\tomil\\Pictures\\Admin\\"

config = {
    "apiKey": "AIzaSyA4WeK8ocGS73B-Q2BmYN--Wg6vqhD5Sok",
    "authDomain": "coen-elec-390-d0535.firebaseapp.com",
    "databaseURL": "https://coen-elec-390-d0535.firebaseio.com/",
    "storageBucket": "coen-elec-390-d0535.appspot.com"
}
#cred=

# print("wifi connected")
firebase = pyrebase.initialize_app(config)
# Get a reference to the auth service
auth = firebase.auth()
# Log the user in
user = auth.sign_in_with_email_and_password("tomilashy@gmail.com", "Elec_Coen_390")
#firebase Admin library
app=firebase_admin.initialize_app(credentials.Certificate(cred),{'storageBucket':"coen-elec-390-d0535.appspot.com"})
bucket_1 = admin_storage.bucket(app=app)
store =firestore.client()
doc_ref = store.collection(u'doors').document(u'6768')
print("wifi connected")
known_face_names= []
doc_refs = store.collection(u'doors')
while True:
    try:
        docs = doc_refs.stream()
        for doc in docs:
#             print(doc)
            if str(doc.id) == "6768":
                dict = doc.to_dict()
                print(dict)
                if dict['adminChanged']:
                    print("downloading new picture")
                    downloadAdminPics()
                    learnFaces()
                    doc_ref.update({u'adminChanged': False})
                else:
                    pass
    except Exception as e:
        print(f"error: {e}")
        pass
