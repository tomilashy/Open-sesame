'''
Created on Nov. 2, 2019

@author: tomil

'''
import RPi.GPIO as GPIO # Import Raspberry Pi GPIO library
from time import sleep # Import the sleep function from the time module
import pyrebase
from picamera import PiCamera
from datetime import datetime,timedelta
from mfrc522 import SimpleMFRC522
import signal
import face_recognition
import numpy as np
from PIL import Image, ImageDraw,ImagePath
#from IPython.display import display
#import cv
from threading import Thread
import firebase_admin
from firebase_admin import credentials,firestore
from firebase_admin import storage as admin_storage
import glob
import os,shutil
import pathlib
import urllib.request


def sendPicHistory(imagePath, name):
    global firebase

    print("sending pic started")
    # posting to firebase storage
    storage = firebase.storage()
    # as admins
    storage.child("door_6768/history/" + name + ".jpg").put(imagePath)


def downloadAdminPics():  # store admin pics to a certain folder for face recognition
    global firebase
    global bucket_1

    print("download started")
    storage = firebase.storage()
    # storage.child("images/example.jpg").download("downloaded.jpg")
    datadir = '/home/pi/Desktop/Admin/'

    shutil.rmtree(datadir)
    # all_files = storage.child("door_6768/profiles/").list_files()
    image_urls = []
    print(bucket_1)
    namelist=[]
    for blob in bucket_1.list_blobs():
        name = str(blob.name)
        #print(name)
        if "door_6768/profiles/" in name:  # '''and ("png" or "jpg")'''
            #print(name)
            blob_img = bucket_1.blob(name)
            X_url = blob_img.public_url
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


####################################
#  CAMERA
####################################
def camera():
    global bucket_1
    store = firestore.client()
    doc_ref = store.collection(u'doors').document(u'6768')

    print("camera started")
    now = datetime.now()
    dt_string = now.strftime("%H.%M.%S.%d.%m.%Y")
    camera = PiCamera()
    camera.start_preview()
    sleep(5)
    camera.stop_preview()
    name = 'image.jpg'
    camera.capture(name)
    camera.close()
    sendPicHistory(name, dt_string)  # call firebase
    imgUrl = "a"
    for blob in bucket_1.list_blobs():
        name = str(blob.name)
        if dt_string in name:  # '''and ("png" or "jpg")'''
            #print(name)
            blob_img = bucket_1.blob(name)
#             imgUrl = blob_img.generate_signed_url(timedelta(seconds=300), method='GET')
            imgUrl = blob_img.public_url()
            doc_ref.update({
                u'lastImageUrl': str(imgUrl)})

    print("camera stopped")
###################################################################
#  SETTING VARIABLES
###################################################################
sensorPin = 38
doorPin = 12
GPIO.setwarnings(False)  # Ignore warning for now
GPIO.setmode(GPIO.BOARD)  # Use physical pin numbering, BCM for other numbering
GPIO.setup(sensorPin, GPIO.IN)  # Set pin 8 to be an output pin and set initial value to low (off)
GPIO.setup(doorPin, GPIO.OUT, initial=GPIO.LOW)  # Set pin 8 to be an output pin and set initial value to low (off)

config = {
    "apiKey": "AIzaSyA4WeK8ocGS73B-Q2BmYN--Wg6vqhD5Sok",
    "authDomain": "coen-elec-390-d0535.firebaseapp.com",
    "databaseURL": "https://coen-elec-390-d0535.firebaseio.com/",
    "storageBucket": "coen-elec-390-d0535.appspot.com"
}
#cred =

firebase = pyrebase.initialize_app(config)
# Get a reference to the auth service
auth = firebase.auth()
# Log the user in
user = auth.sign_in_with_email_and_password("tomilashy@gmail.com", "Elec_Coen_390")

# firebase Admin library
app = firebase_admin.initialize_app(credentials.Certificate(cred), {'storageBucket': firebase.storage_bucket})
bucket_1 = admin_storage.bucket(app=app)
RFID_tags = {"225486185361": "tomi"}
store =firestore.client()
doc_refs = store.collection(u'doors')
doc_ref = store.collection(u'doors').document(u'6768')
test = True
print("Wifi connected")

##pir = MotionSensor(2)
# 304578982797 - BLUE TAG
# 225486185361 - WHITE TAG
###################################################################
#  LOOP
###################################################################
while test:  # Run forever

    if GPIO.input(sensorPin) == GPIO.HIGH:  # check if the sensor is HIGH
        # update admin pics list if different or overwrite it

        print("Motion Detected")

        camera()
        try:
            docs = doc_refs.stream()
            for doc in docs:
                
                if str(doc.id) == "6768":
                    
                    dict = doc.to_dict()
                    #print(dict)
                    doc_ref.update({u'motionDetected': True})
        except Exception as e:
            print(e)
        sleep(10)
        print("Motion ended")
                # test=False
    else:
        # print("Motion Stopped")
        pass
