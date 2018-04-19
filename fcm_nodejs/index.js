var admin = require("firebase-admin");

var serviceAccount = require("./service-account.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://quickstar-android.firebaseio.com"
});


// This registration token comes from the client FCM SDKs.
var registrationToken = 'fTRTO1n1OtE:APA91bGHZioecwpRnowaAN6h7OtusY6Ls93XLLCsvJaW36ra2vC9OA3T8x8ubSPsIQwUqsO5e4loMr7bWkqEa3rzkIgCuaq3AzEJYXHGhdNMu56oo0QwlhULA7k8IXBfF1tjmKbCGS4M';

// The topic name can be optionally prefixed with "/topics/".
var topic = 'news';

// See documentation on defining a message payload.
var message = {
    data: {
        score: '850',
        time: '2:45'
    },
    notification: {
        title: "NASDAQ News",
        body: "The NASDAQ climbs for the second day. Closes up 0.60%."
    },
    //token: registrationToken,
    topic: topic
};

// Send a message to the device corresponding to the provided
// registration token.
admin.messaging().send(message)
  .then((response) => {
    // Response is a message ID string.
    console.log('Successfully sent message:', response);
  })
  .catch((error) => {
    console.log('Error sending message:', error);
  });