
from time import sleep # Import the sleep function from the time module

import time
from time import sleep
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
import json

import pyrebase
from bleach._vendor.html5lib._ihatexml import name

def downloadAdminPics():  # store admin pics to a certain folder for face recognition
    global firebase
    global bucket_1
    global datadir
    
    doc_refs = store.collection(u'doors')
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
    
    namelist.sort(reverse=True)
    
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


####################################
#  FACE RECOGNITION
####################################
def recognizeFace(imagePath):
    global known_face_names
    global doc_ref
    #  LEARN ADMIN'S FACES
    # Load a sample picture and learn how to recognize it.
 
    images =[]
    known_face_encodings =[]
    print("Started"+ imagePath)
    files = glob.glob(imagePath +"*.jpg")
    files2 = glob.glob(imagePath + "*.png")
    files.extend(files2)
    # print("\n".join(files))
 
# #     for file in files:
# #         img = Image.open(file)
# #         img.save(file,optimize=True,quality=0)
# #         img.close()
# #     print("image resized")
#     # printing out files in sorted form
    known_face_names = [x.split(imagePath)[1].split(".jpg")[0] for x in files]
    print(known_face_names)
#     for file in files:
#         images.append(face_recognition.load_image_file(file));
#     #print(images)
#     for encoding in images:
#         known_face_encodings.append(face_recognition.face_encodings(encoding)[0])
# #     print(known_face_encodings)
# 
#     print('Learned encoding for', len(known_face_encodings), 'images.')
    #######################################################################
    # Load an image with an unknown face
    known_face_encodings=[]
    with open("face_tags.json") as json_file:
        known_face_encodings= json.load(json_file)

    known_face_encodings=[np.array(x) for x in known_face_encodings]
    unknown_image = face_recognition.load_image_file("image.jpg")
    # unknown_image1 = face_recognition.load_image_file("test3.jpg")



    # Find all the faces and face encodings in the unknown image
    face_locations = face_recognition.face_locations(unknown_image)
    face_encodings = face_recognition.face_encodings(unknown_image, face_locations)
    # Convert the image to a PIL-format image so that we can draw on top of it with the Pillow library

    pil_image = Image.fromarray(unknown_image)
    # Create a Pillow ImageDraw Draw instance to draw with
    draw = ImageDraw.Draw(pil_image)
    # draw1 = ImageDraw.Draw(pil_image1)

    # THE TEST PART

    # Loop through each face found in the unknown image
    for (top, right, bottom, left), face_encoding in zip(face_locations, face_encodings):
        # See if the face is a match for the known face(s)
        matches = face_recognition.compare_faces(known_face_encodings, face_encoding)

        name = "Unknown"

        # Or instead, use the known face with the smallest distance to the new face
        face_distances = face_recognition.face_distance(known_face_encodings, face_encoding)
        best_match_index = np.argmin(face_distances)
        if matches[best_match_index]:
            name = known_face_names[best_match_index]
            print(name)

        if (name in known_face_names):
            doc_ref.update({
                u'lock': False})
            print("door unlocked")
        # Draw a box around the face using the Pillow module
        draw.rectangle(((left, top), (right, bottom)), outline=(0, 0, 255))

        # Draw a label with a name below the face
        text_width, text_height = draw.textsize(name)
        draw.rectangle(((left, bottom - text_height - 10), (right, bottom)), fill=(0, 0, 255), outline=(0, 0, 255))
        draw.text((left + 6, bottom - text_height - 5), name, fill=(255, 255, 255, 255))




    # Remove the drawing library from memory as per the Pillow docs
    del draw

    # Display the resulting image
    display(pil_image)

    pil_image.show()
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

known_face_names= []

while True:
    times = os.path.getmtime("image.jpg")
    current = str(time.strftime("%H.%M.%S.%d.%m.%Y",time.localtime(times)))
    print(f"lastpic-{lastImage} \n recent-{current} ")
    sleep(5)
    if lastImage != str(time.strftime("%H.%M.%S.%d.%m.%Y",time.localtime(times))):
        lastImage = str(time.strftime("%H.%M.%S.%d.%m.%Y", time.localtime(times)))
#         downloadAdminPics()
        recognizeFace(datadir)
        

    else:
        pass
