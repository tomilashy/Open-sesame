import RPi.GPIO as GPIO
from mfrc522 import SimpleMFRC522
import  json

reader = SimpleMFRC522()
tags={}
try:
    with open("/home/pi/Desktop/RFID_tags.json") as json_file:
        tags= json.load(json_file)
    text = input('New data:')
    print("Now place your tag to write")
    reader.write(text)
    id, text = reader.read()
    tags[id]=text.split(" ")[0]
    with open("/home/pi/Desktop/RFID_tags.json","w") as json_file:
        json.dump(tags,json_file)
    print("Written")
finally:
         GPIO.cleanup()