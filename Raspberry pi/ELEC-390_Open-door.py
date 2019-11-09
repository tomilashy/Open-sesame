
import RPi.GPIO as GPIO # Import Raspberry Pi GPIO library
from time import sleep # Import the sleep function from the time module
import firebase_admin
from firebase_admin import credentials,firestore
from firebase_admin import storage as admin_storage
import google.cloud
###################################################################
#  SETTING VARIABLES
###################################################################

doorPin = 12
GPIO.setwarnings(False) # Ignore warning for now
GPIO.setmode(GPIO.BOARD) # Use physical pin numbering, BCM for other numbering
GPIO.setup(doorPin, GPIO.OUT, initial=GPIO.LOW) # Set pin 8 to be an output pin and set initial value to low (off)

cred={
  "type": "service_account",
  "project_id": "coen-elec-390-d0535",
  "private_key_id": "e38d7363baab5b584f4cadcf3c0b0f8759857862",
  "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDhBxyxtnn8e5fk\ncsC7DfcRfYN2Hj2L8RvVoFmG6KCigc1AzjuYbXLaxxf+kdeWNrWnwpvSuSV41WMF\nbZtOQHiRLusCQnNmpT8tXnW6/tmSQTaH0TDszXM82GwuhAAodxZbU8QMzCc7UFBx\nRs4fGWw0esetCHuMxdrC5Y/rhMCaAdaOcXoTyKCRT7cs8s/n65RfFFvWjtbdta4k\nSswhRhbD1TRW3im4yoXjJwJd5HNKTDNSLFBmSEiVftj8EfSrHsiZkRVDmANwhq2A\nsvmZ2W3Dd0zhp6tS8SRDkw4cYkAcNCkGNrS3+czbGrK1YkggWgXUxrF2jgu/yb7g\nCkJB86fxAgMBAAECgf8MASjKgznZ0y6Qh2SPZQ2wJal/AEe9OO942Y+zJa6aVyaL\n8z9w0H7EhF6l0tI+30iWllSsSqRiIztuElMxruH9HaSXsPN7kN7fBPh5u3VaZPyv\nCT64A2Gz1+p46GzovctWuS3cMN84VrsfWC1Vv1svoSPn0bBtTy02Uw9dC0saou4m\ntxXt4LjCFZ3DA1iuygxBvdDdTlMRcwpnwWs7fBvEavs077S+cPCizT7s8LK9y2lo\nInziWx/jmOP4paKvJkT2HgzXm94k2aNX4PLHGATA9JPiKYsInMYdXlyOmY6p3Iol\ndUnQOrobrzbjdY7i/RxaJ1cWkVlQjOIsjlCZjd0CgYEA+j7NTes7CESc1QV/qskN\nzxzj2PuxnlRR1LE3uc0mWbC9fBjUHTlNtvD9cRsbwHRQEq9A8Kkkma+l/q3nWI5d\niDksWAHDlzdDBMsT7RhsNN1YhCEv7VgaTZzmiLF/M1qo+If7RNSidlwg07zbtyXt\nj3VT5gANpCdN6ufikJ4dgr0CgYEA5jPapQmFMgn5ycqCVWb/+QSBbfW4kmtMAhFZ\n8gEY6FyUmHyeO3e2rIhT4EQvaJuCsvKv6R3bBwG/ellcnKY5LDW5q6+sG8W+71fy\n1elJesMDah6vIXe9wrFkbInP9+qj/ciPBLx+4zxdLtxwGNZnnBRJYitdckz0juBL\nF9oBR0UCgYEAh6rIjze6N09LX0mt3x9E90YIWLiLoPTBsHeraHXKFtH0yHSFepp4\ngXfBTzKNJxdn2dldcZuhlhhd++a4B5CXMKUeX1AFg9OX599ZHC8jar2doXSEkT40\ndGNdsDnaZjL+bE78cGJ++YAU4/X9gBt14VKQe58HxjjwyyuP7ytpgFkCgYADst+X\neUCvniNqT5uqfZ4tRRpDRwhxKdIt0lOMpOHekeY9UPtF31WLDiGy38WXO7Bs6aNT\n7ovfX/LQp3Dqf205vZ8/F/J++71moRYgw7/PfiSm5tlPg1oEL896QviZdiauZDOQ\nEutDAWxJ3Xbbn05I3raTIjk9JelkBkyOlI0itQKBgQCNc05r19cI29v0hUfXdEOL\nM6JmzlAcBnJr0uDsDAMe/Ef1t0l1z1T9Yw0vzrffxwcXphGrCGjFFn22ACasPMnJ\ncFnzkhCOPVM9F54LKcS6uKlF8MNB4XOMV9IDMyOZW3NLn6/5GHyKDu/3OKdHFNCR\nQeZvM+FxzlPplF4LYesnTQ==\n-----END PRIVATE KEY-----\n",
  "client_email": "firebase-adminsdk-qgtwz@coen-elec-390-d0535.iam.gserviceaccount.com",
  "client_id": "102066590732902064339",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-qgtwz%40coen-elec-390-d0535.iam.gserviceaccount.com"
}
print("wifi connected")
#firebase Admin library
app=firebase_admin.initialize_app(credentials.Certificate(cred),{'storageBucket':"coen-elec-390-d0535.appspot.com"})
store =firestore.client()
doc_ref = store.collection(u'doors').document(u'6768')
doc_ref.update({
    u'lock': True})
doc_refs = store.collection(u'doors')
while True:
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
              sleep(5)
              doc_ref.update({u'lock': True})
              #lock door
              print("door locked")
            else:
              #lock door
              print("door locked")
              pass

      except google.cloud.exceptions.NotFound:
        print(u'Missing data')