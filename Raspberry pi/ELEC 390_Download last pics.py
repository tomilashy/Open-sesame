
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

import pyrebase
from bleach._vendor.html5lib._ihatexml import name

def downloadLastPics():  # store admin pics to a certain folder for face recognition
    global firebase
    global bucket_1
    global datadir
    global doc_refs
    
    namelist=[]
    for blob in bucket_1.list_blobs():
        name = str(blob.name)
        if "door_6768/history/" in name:  
            if "jpg" in name:
                namelist.append(name.split("door_6768/history/")[1].split(".jpg")[0])
    
#     print(namelist)
    
    for blob in bucket_1.list_blobs():
        name = str(blob.name)
        if max(namelist, key=lambda x: datetime.strptime(x, "%H.%M.%S.%d.%m.%Y")) in name:
            blob_img = bucket_1.blob(name)
            lastUrl = blob_img.generate_signed_url(timedelta(seconds=300), method='GET')
            urllib.request.urlretrieve(lastUrl, "image.jpg")
#     print(max(namelist, key=lambda x: datetime.strptime(x, "%H.%M.%S.%d.%m.%Y")))
 
###################################################################
#  SETTING VARIABLES
###################################################################
# datadir = '/home/pi/Desktop/Admin/'
datadir = "C:\\Users\\tomil\\Pictures\\Admin\\"

config = {
    "apiKey": "AIzaSyA4WeK8ocGS73B-Q2BmYN--Wg6vqhD5Sok",
    "authDomain": "coen-elec-390-d0535.firebaseapp.com",
    "databaseURL": "https://coen-elec-390-d0535.firebaseio.com/",
    "storageBucket": "coen-elec-390-d0535.appspot.com"
}
#cred={}

print("wifi connected")
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

doc_refs = store.collection(u'doors')
while True:
    try:
        docs = doc_refs.stream()
        for doc in docs:
            if str(doc.id) == "6768":
                dict = doc.to_dict()
                # print(dict)
                if dict["motionDetected"]:
                    print("downloading new picture")
                    downloadLastPics()
                    doc_ref.update({u'motionDetected': False})
                else:
#                     print("no change")
                    pass
    except Exception as e:
        print(f"error: {e}")
        pass
