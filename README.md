<!--- Copyright (c) 2016 CM Telecom. All rights reserved. -->

# cm-plugin-hybridmessaging

This plugin provides [Hybrid Messaging SDK](https://docs.cmtelecom.com/hybrid-messaging/v2.0.0) functionality for cordova based apps.

Latest version is **0.0.2**

[Changelog](#changelog)


## Supported Platforms

- Android
- iOS

## Methods
* [hybridmessaging.configureService](#configureService)
* [hybridmessaging.startMessagingService](#startMessagingService)
* [hybridmessaging.requestVerification](#requestVerification)
* [hybridmessaging.verifyPin](#verifyPin)
* [hybridmessaging.requestVerificationVoiceCall](#requestVerificationVoiceCall)
* [hybridmessaging.getVerificationStatus](#getVerificationStatus)
* [hybridmessaging.getMessages](#getMessages)
* [hybridmessaging.getDeviceIdValue](#getDeviceIdValue)
* [hybridmessaging.getMsisdnValue](#getMsisdnValue)
* [hybridmessaging.setDevelopment](#setDevelopment)


## Enumerations
* [hybridmessaging.DeviceRegistrationStatus](#DeviceRegistrationStatus)


## <a name="configureService"></a>configureService(config)

Configures the hybrid messaging service with API key, API secret, Android sender ID  and relevant callbacks

**IMPORTANT**: the configureService method should be called as early in the application's life cycle as possible and prior to executing any other call related to hybrid messaging. All hybrid messaging methods called prior to service configuration will return with an error.

### Parameters

* config: `Object`, (required) - config object with format:

```javascript
{
    appKey : 'string',
    appSecret : 'string',
    senderId : 'string', (required on Android only)
    notificationCallback : 'function',
    deviceCarrierUpdateCallback : 'function'
}
```

### Example

```javascript

function notificationCallback(pushPayload) {
    console.log(pushPayload);

    #push payload is raw platform-specific payload including keys such as 'alert' on iOS and 'message' on Android.
    var message = pushPayload.alert ? pushPayload.alert : pushPayload.message;

    $cordovaDialogs.alert(message, 'Push message', 'OK');
}

function deviceCarrierUpdateCallback() {
    console.log('Device carrier has changed');
}

var config = {
    appKey : YOUR_APP_KEY,
    appSecret : YOUR_APP_SECRET,
    senderId : YOUR_ANDROID_SENDER_ID,
    notificationCallback: notificationCallback,
    deviceCarrierUpdateCallback: deviceCarrierUpdateCallback
};

window.hybridmessaging.configureService(config);
```


## <a name="startMessagingService"></a>startMessagingService(successCallback, errorCallback)

Enables the hybrid messaging service

**IMPORTANT**: the startMessagingService method should be called after executing the configureService method and prior to executing any other call related to hybrid messaging. All hybrid messaging methods called prior to service starting (except for configureService which should be called before it and as early in the application life cycle as possible) will return with an error.

### Parameters

* successCallback: `function`, (required) - callback function to be called upon successfully started messaging service

* failureCallback: `function`, (optional) - callback function to be called upon error

### Example

```javascript
window.hybridmessaging.startMessagingService(function() {
    console.log('Messaging service has started successfully');
}, function(error) {
    console.log('Failed to start messaging service: ' + error);
});
```


## <a name="requestVerification"></a>requestVerification(msisdn, successCallback, errorCallback) 

Requests the phone number verification. Returns current [DeviceRegistrationStatus](#DeviceRegistrationStatus) in successful callback

**IMPORTANT**: in order to be able to receive hybrid messaging notifications and to be able to fetch older messages from the server, the device's phone number (MSISDN) has to be verified at least once after the app's installation. Default phone number verification method is by the means of sending a one-time PIN via SMS.

### Parameters

* msisdn: `string`, (required) - phone number to verify

* successCallback: `function`, (required) - callback function to be called upon successfully requesting number verification

* failureCallback: `function`, (optional) - callback function to be called upon error

### Example

```javascript
var msisdn = '0031612345678';
window.hybridmessaging.requestVerification(msisdn, function(status) {
    console.log(status);
}, function(error) {
     console.log('Phone number verification failed: ' + error);
});
```


## <a name="verifyPin"></a>verifyPin(pin, successCallback, errorCallback)

Verifies the PIN typically entered by the user after previously receiving it in an SMS for the purpose of phone number verification. Returns current [DeviceRegistrationStatus](#DeviceRegistrationStatus) in successful callback

### Parameters

* pin: `string`, (required) - pin to verify

* successCallback: `function`, (required) - callback function to be called upon successfully executing PIN verification (with verification either passed or failed, but in any case correctly executed)

* errorCallback: `function`, (optional) - callback function to be called upon error

### Example

```javascript
var pin = '0785';
window.hybridmessaging.verifyPin(pin, function(status) {
    console.log(status);
}, function(error) {
   console.log('Failed to verify pin: ' + error);
});
```


## <a name="requestVerificationVoiceCall"></a>requestVerificationVoiceCall(msisdn, successCallback, errorCallback)

Requests the phone number verification via voice call. Typically serves as a fallback in case if the default SMS-based number verification failed e.g. due to network issues. The requested voice call, if successful, will provide the same PIN code as was originally sent via SMS.

### Parameters

* msisdn: `string`, (required) - phone number to verify

* successCallback: `function`, (required) - callback function to be called upon successfully requesting PIN voice call

* errorCallback: `function`, (optional) - callback function to be called upon error

### Example

```javascript 
var msisdn = '0031612345678';
window.hybridmessaging.requestVerificationVoiceCall(msisdn, function() {
  console.log('Successfully performed a voice call');
}, function(error) {
  console.log('Failed to perform a voice call: ' + error);
});
```


## <a name="getVerificationStatus"></a>getVerificationStatus(successCallback, errorCallback)

Requests current verification status. Returns current [DeviceRegistrationStatus](#DeviceRegistrationStatus) in successful callback

### Parameters

* successCallback: `function`, (required) - callback function to be called upon successfully fetching the current status

* errorCallback: `function`, (optional) - callback function to be called upon error

### Example

```javascript
window.hybridmessaging.getVerificationStatus(function(status) {
    console.log(status);
}, function(error) {
    console.log('Failed to get verification status: ' + error);
});
```

## <a name="getMessages"></a>getMessages(limit, offset, successCallback, errorCallback)

Returns the messages that have been sent to the particular MSISDN number using Hybrid Messaging (either all of them ever since the number has been registered with Hybrid Messaging or a subset of those depending on the provided limit and offset values)

### Parameters

* limit: `number`, (optional) - amount of messages to retrieve

* offset: `number`, (optional) - offset from which messages to retrieve

* successCallback: `function`, (required) - callback to be called upon successful message retrieval

* errorCallback: `function`, (optional) - callback function to be called upon error

### Example

```javascript
var limit = 5;
var offset = 10;
window.hybridmessaging.getMessages(limit, offset, function(messages) {
    console.log(messages);
}, function(error) {
    console.log('Failed to retrieve messages: ' + error);
});
```


## <a name="getDeviceIdValue"></a>getDeviceIdValue(successCallback, errorCallback)

Returns a device ID value as used by the Hybrid Messaging system for device identification

### Parameters

* successCallback: `function`, (required) - callback function to be called upon successfully fetching device ID

* errorCallback: `function`, (optional) - callback function to be called upon error

### Example

```javascript
window.hybridmessaging.getDeviceIdValue(function(deviceId) {
    console.log('Device id is ' + deviceId);
}, function(error) {
    console.log('Failed to fetch device id: ' + error);
});
```


## <a name="getMsisdnValue"></a>getMsisdnValue(successCallback, errorCallback) 

Returns the value of the msisdn number registered with the hybrid messaging system

### Parameters

* successCallback: `function`, (required) - callback function to be called upon successfully fetching MSISDN

* errorCallback: `function`, (optional) - callback function to be called upon error

### Example

```javascript
window.hybridmessaging.getMsisdnValue(function(msisdn) {
    console.log('MSISDN is ' + msisdn);
}, function(error) {
     console.log('Failed to fetch MSISDN: ' + error);
});
```


## <a name="setDevelopment"></a>setDevelopment(enableDevelopment) 

Sets development mode for iOS, which will use Development Sandbox for push messages in that case.

### Parameters

* enableDevelopment: `boolean`, (required)

### Example

```javascript
window.hybridmessaging.enableDevelopment(true);
```


## <a name="DeviceRegistrationStatus"></a>DeviceRegistrationStatus

Possible values are:

```javascript 
DeviceRegistrationStatus.UNVERIFIED,
DeviceRegistrationStatus.WAITING_FOR_PIN,
DeviceRegistrationStatus.LAST_PIN_VERIFICATION_FAILED,
DeviceRegistrationStatus.PIN_VERIFIED,
DeviceRegistrationStatus.INVALID,
DeviceRegistrationStatus.UNKNOWN
```

## <a name="changelog"></a>Changelog

### 0.0.1
* Initial version

### 0.0.2
* Modified Android part of plugin to no longer rely on a custom Application class to prevent conflicts with environments which define their own Application subclasses
* Restructured docs and extended method descriptions

