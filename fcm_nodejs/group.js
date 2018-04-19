var request = require('request');

var token1 = 'fTRTO1n1OtE:APA91bGHZioecwpRnowaAN6h7OtusY6Ls93XLLCsvJaW36ra2vC9OA3T8x8ubSPsIQwUqsO5e4loMr7bWkqEa3rzkIgCuaq3AzEJYXHGhdNMu56oo0QwlhULA7k8IXBfF1tjmKbCGS4M';
var token2 = 'fhQ0GrDZz0E:APA91bF7dbUQ32Xzrk9MFJuFUo5_3UKxqCXgxIrmwYaK4c7BSqzwKWr9Jh02UTEt4coWDkFI2kU31UI3daXcCPZi-sfjpNAt1xNEna4FsNlgfYxZTRnssVUeIyOWFmR90YRPOsig7ot8';

var headers = {
     'Authorization': 'AAAAgYcGNmU:APA91bG7PHHIjp3fInZvxEP0Hve4x4TjWvOjH8SCMwTc2KNORLKDwFrfEBlQZgaQerOL6XFGJPrnsk6tBBlj2q2dasscZwrp3v_IoTgohcMebx0aQ4CDppUY7z7OIH99Jea9074SRSo8',
     'project_id': '556316112485',
     'Content-Type':     'application/json'
         }
var options = {
     url: 'https://iid.googleapis.com/notification',
     method: 'POST',
     headers: headers,
     json: {'operation': 'create', 
		'notification_key_name': 'test1111.chen@gmail.com',
                'registration_ids': [token1, token2]}
}

/*
var options = {
     url: 'https://android.googleapis.com/gcm/notification',
     method: 'POST',
     headers: headers,
     json: {'operation': 'remove',
                'notification_key_name': '<key name here>',
		'notification_key': '<notification key here>',
                'registration_ids': [token1, token2]}
}
*/

request(options, function (error, response, body) {
                  console.log(body)
})
