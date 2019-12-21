import RPi.GPIO as GPIO
from mfrc522 import SimpleMFRC522
import json
from time import sleep # Import the sleep function from the time module
import firebase_admin
from firebase_admin import credentials,firestore
from firebase_admin import storage as admin_storage
import google.cloud

#cred=
#firebase Admin library
app=firebase_admin.initialize_app(credentials.Certificate(cred),{'storageBucket':"coen-elec-390-d0535.appspot.com"})
print("wifi connected")
store =firestore.client()
doc_ref = store.collection(u'doors').document(u'6768')

reader = SimpleMFRC522()
while True:
        try:
                id, text = reader.read()
                print(id)
                print(text)
                with open("/home/pi/Desktop/RFID_tags.json") as json_file:
                    data = json.load(json_file)

                    if str(id) in data:
                        print(data)
                        #open door
                        doc_ref.update({
                            u'lock': False})
                        pass

        finally:
                pass
