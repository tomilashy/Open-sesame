
import RPi.GPIO as GPIO # Import Raspberry Pi GPIO library
from time import sleep # Import the sleep function from the time module
import firebase_admin
from firebase_admin import credentials,firestore
from firebase_admin import storage as admin_storage
import google.cloud
###################################################################
#  SETTING VARIABLES
###################################################################

doorPin = 37
GPIO.setwarnings(False) # Ignore warning for now
GPIO.setmode(GPIO.BOARD) # Use physical pin numbering, BCM for other numbering
GPIO.setup(doorPin, GPIO.OUT, initial=GPIO.HIGH) # Set pin 37 to be an output pin and set initial value to High(on)

#cred={
#firebase Admin library
app=firebase_admin.initialize_app(credentials.Certificate(cred),{'storageBucket':"coen-elec-390-d0535.appspot.com"})
store =firestore.client()
print("wifi connected")
doc_ref = store.collection(u'doors').document(u'6768')
doc_ref.update({
    u'lock': True})
doc_refs = store.collection(u'doors')
while True:
    doc_ref.update({u'isDoorConnected': True})
    try:
      docs = doc_refs.stream()
      for doc in docs:
        if str(doc.id) == "6768":
          dict = doc.to_dict()
          # print(dict)
          if dict["lock"]== False:
            # print(u'Doc Data:{} {}'.format(doc.to_dict(), doc.id))
            #unlock door
            print("door unlocked")
            GPIO.output(doorPin,GPIO.LOW)
            sleep(5)
            doc_ref.update({u'lock': True})
            #lock door
            GPIO.output(doorPin,GPIO.HIGH)
            print("door locked")
          else:
            #lock door
            GPIO.output(doorPin,GPIO.HIGH)
            print("door locked")
            pass
    
    except google.cloud.exceptions.NotFound:
      print(u'Missing data')
