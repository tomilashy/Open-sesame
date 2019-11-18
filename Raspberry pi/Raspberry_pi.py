import RPi.GPIO as GPIO # Import Raspberry Pi GPIO library
from time import sleep # Import the sleep function from the time module
import pyrebase
from picamera import PiCamera
import datetime
from mfrc522 import SimpleMFRC522
import signal
import face_recognition
import numpy as np
from PIL import Image, ImageDraw
from IPython.display import display
import cv2
###################################################################
#  ALL FUNCTIONS
###################################################################
####################################
#  FIREBASE 
####################################
def checkFirebasedoor():
    global firebase
    #db = firestore.client() firestore
    db = firebase.database()

    if db.child("Door").get().val():
        openDoor()
    else:
        closeDoor()

def sendPicHistory(imagePath,name):
    global firebase
    # posting to firebase storage
    storage = firebase.storage()
    # as admin
    storage.child("door_1/history/"+name+".jpg").put(imagePath)

def downloadAdminPics(): #store admin pics to a certain folder for face recognition
    global firebase
    storage = firebase.storage()
    #storage.child("images/example.jpg").download("downloaded.jpg")
    datadir = '/home/pi/Desktop/Admin/'

    all_files = storage.child("door_1/profiles/").list_files()

    for file in all_files:
        try:
            file.download_to_filename(datadir + file.name)
        except:
            print('Download Failed')    
    

####################################
#  DOOR LOCK
####################################
def openDoor():
    global doorPin
    GPIO.output(doorPin, GPIO.LOW) # Turn off
    sleep(10) # Sleep for 10 second
def closeDoor():
    global doorPin
    global firebase
    GPIO.output(doorPin, GPIO.HIGH) # Turn on

    #set firebase door as false
     # Get a reference to the database service
    db = firebase.database()
    # Pass the user's idToken to the push method
    results = db.child("Door").set(False)



####################################
#  FACE RECOGNITION
####################################
def faceRecognition(imagePath):
    ##  LEARN ADMIN'S FACES
    # Load a sample picture and learn how to recognize it.
    obama_image = face_recognition.load_image_file("obama.jpg")
    obama_face_encoding = face_recognition.face_encodings(obama_image)[0]

    # Load a second sample picture and learn how to recognize it.
    biden_image = face_recognition.load_image_file("biden.jpg")
    biden_face_encoding = face_recognition.face_encodings(biden_image)[0]
    # Load a third sample picture and learn how to recognize it.
    new_image = face_recognition.load_image_file("test31.jpg")
    new_face_encoding = face_recognition.face_encodings(new_image)[0]

    # Create arrays of known face encodings and their names
    known_face_encodings = [
        obama_face_encoding,
        biden_face_encoding,
        new_face_encoding
    ]
    known_face_names = [
        "Barack Obama",
        "Joe Biden",
        "Angela"
    ]
    print('Learned encoding for', len(known_face_encodings), 'images.')
    #######################################################################
    # Load an image with an unknown face
    unknown_image = face_recognition.load_image_file("two_people.jpg")
    unknown_image1 = face_recognition.load_image_file("test3.jpg")

    # Find all the faces and face encodings in the unknown image
    face_locations = face_recognition.face_locations(unknown_image)
    face_encodings = face_recognition.face_encodings(unknown_image, face_locations)
    face_locations1 = face_recognition.face_locations(unknown_image1)
    face_encodings1 = face_recognition.face_encodings(unknown_image1, face_locations1)
    # Convert the image to a PIL-format image so that we can draw on top of it with the Pillow library

    pil_image = Image.fromarray(unknown_image)
    pil_image1 = Image.fromarray(unknown_image1)
    # Create a Pillow ImageDraw Draw instance to draw with
    draw = ImageDraw.Draw(pil_image)
    draw1 = ImageDraw.Draw(pil_image1)


    #THE TEST PART

    # Loop through each face found in the unknown image
    for (top, right, bottom, left), face_encoding in zip(face_locations1, face_encodings1):
        # See if the face is a match for the known face(s)
        matches = face_recognition.compare_faces(known_face_encodings, face_encoding)

        name = "Unknown"

        # Or instead, use the known face with the smallest distance to the new face
        face_distances = face_recognition.face_distance(known_face_encodings, face_encoding)
        best_match_index = np.argmin(face_distances)
        if matches[best_match_index]:
            name = known_face_names[best_match_index]
            print (name)
        # Draw a box around the face using the Pillow module
        draw.rectangle(((left, top), (right, bottom)), outline=(0, 0, 255))

        # Draw a label with a name below the face
        text_width, text_height = draw.textsize(name)
        draw.rectangle(((left, bottom - text_height - 10), (right, bottom)), fill=(0, 0, 255), outline=(0, 0, 255))
        draw.text((left + 6, bottom - text_height - 5), name, fill=(255, 255, 255, 255))
    
        #--- Draw a box around the face using the Pillow module
        draw1.rectangle(((left, top), (right, bottom)), outline=(0, 0, 255))

        # Draw a label with a name below the face
        text_width, text_height = draw1.textsize(name)
        draw1.rectangle(((left, bottom - text_height - 10), (right, bottom)), fill=(0, 0, 255), outline=(0, 0, 255))
        draw1.text((left + 6, bottom - text_height - 5), name, fill=(255, 255, 255, 255))


    # Remove the drawing library from memory as per the Pillow docs
    del draw1

    # Display the resulting image
    display(pil_image1)

    pil_image1.show()

####################################
#  RFID // PIN LOCK
####################################
def writeRFID ():
    reader = SimpleMFRC522()
    try:
        text = input('New data:')
        print("Now place your tag to write")
        reader.write(text)
        print("Written")
        id, text = reader.read()
        RFID_tags[id]=text

def readRFID ():			
	# READ
	reader = SimpleMFRC522()
	try:
			id, text = reader.read()
			if id in RFID_tags:
				openDoor()
			


####################################
#  MEANS OF UNLOCK
####################################
def methodOfUnlock():
    if methodOfUnlock =="":
        pass
    elif methodOfUnlock == "":
        pass
    else:
        pass
####################################
#  CAMERA
####################################
def camera():
    now = datetime.now()
    dt_string = now.strftime("%d/%m/%Y %H:%M:%S")
    camera = PiCamera()
    camera.start_preview()
    sleep(5)
    name="/home/pi/Desktop/History/" + dt_string+".jpg"
    camera.capture(name)
    camera.stop_preview()
    sendPicHistory(name,dt_string) #call firebase
    facefaceRecognition(name)#call face detection
    
    

###################################################################
#  SETTING VARIABLES
###################################################################
methodUnlock=""
sensorPin = 8
doorPin = 7
GPIO.setwarnings(False) # Ignore warning for now
GPIO.setmode(GPIO.BOARD) # Use physical pin numbering
GPIO.setup(sensorPin, GPIO.IN, initial=GPIO.LOW) # Set pin 8 to be an output pin and set initial value to low (off)
GPIO.setup(doorPin, GPIO.OUT, initial=GPIO.LOW) # Set pin 8 to be an output pin and set initial value to low (off)



config = {
  "apiKey": "AIzaSyA4WeK8ocGS73B-Q2BmYN--Wg6vqhD5Sok",
  "authDomain": "coen-elec-390-d0535.firebaseapp.com",
  "databaseURL": "https://coen-elec-390-d0535.firebaseio.com/",
  "storageBucket": "coen-elec-390-d0535.appspot.com",
  "serviceAccount": "path/to/serviceAccountKey.json"
}
firebase = pyrebase.initialize_app(config)
# Get a reference to the auth service
auth = firebase.auth()
# Log the user in
user = auth.sign_in_with_email_and_password("tomilashy@gmail.com", "Elec_Coen_390")
RFID_tags = {}
#304578982797 - BLUE TAG
#225486185361 - WHITE TAG
###################################################################
#  LOOP
###################################################################
while True: # Run forever
    #if firebase says 
    #multiple thread to run RFID along side motion sensor as for password to write with pressin ctrl + c first
    #RFID
    if (sensorPin == GPIO.HIGH):         # check if the sensor is HIGH
        #update admin pics list if different or overwrite it
        openDoor()
        camera()
        print("Motion Detected")
        
    else:
        closeDoor()
        print("Motion Stopped")