var admin = require("firebase-admin");

var serviceAccount = require("./service-account.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://quickstar-android.firebaseio.com"
});

var registrationToken = "fTRTO1n1OtE:APA91bGHZioecwpRnowaAN6h7OtusY6Ls93XLLCsvJaW36ra2vC9OA3T8x8ubSPsIQwUqsO5e4loMr7bWkqEa3rzkIgCuaq3AzEJYXHGhdNMu56oo0QwlhULA7k8IXBfF1tjmKbCGS4M";

var notificationKey = "<notification ketge goes here>";

var topic = 'news';

var payload = {
  notification: {
    title: "NASDAQ News",
    body: "The NASDAQ climbs for the second day. Closes up 0.60%."
  }
};

var options = {
  priority: "high",
  timeToLive: 60 * 60 *24
};

admin.messaging().sendToDevice(registrationToken, payload, options)
//admin.messaging().sendToDeviceGroup(notificationKey, payload)
  .then(function(response) {
    console.log("Successfully sent message:", response);
  })
  .catch(function(error) {
    console.log("Error sending message:", error);
  });
