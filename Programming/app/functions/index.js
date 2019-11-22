const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.onMotionDetected = functions.firestore
    .document('doors/{door}')
    .onUpdate((change, context) => {

      const newValue = change.after.data();

      if (newValue.motionDetected === true) {
        var topic = newValue.doorID.toString();

        var message = {
         notification: {
            title: 'Peek Door',
            body: 'Motion detected! Someone is at the door!'
          },
          topic: topic
        };

        // Send a message to devices subscribed to the provided topic.
        admin.messaging().send(message)
          .then((response) => {
            console.log('Successfully sent message:', response);
            return null;
          })
          .catch((error) => {
            console.log('Error sending message:', error);
          });
      }
    });

