<!--- Copyright (c) 2016 CM Telecom. All rights reserved. -->

# cm-plugin-hybridmessaging

This plugin provides [Hybrid Messaging SDK](https://docs.cmtelecom.com/hybrid-messaging/v2.0.0) functionality for cordova based apps.

Latest version is **0.0.1**

[Changelog](#changelog)


## Installation

    cordova plugin add ssh://git@gitlab.clubmessage.local/apps/hybridmessaging-cordova-plugin.git#[[TAG]]

## Supported Platforms

- Android
- iOS

## Methods
* [hybridmessaging.getDeviceIdValue](#getDeviceIdValue)
* [hybridmessaging.getMsisdnValue](#getMsisdnValue)
* [hybridmessaging.setDevelopment](#setDevelopment)
* [hybridmessaging.configureService](#configureService)
* [hybridmessaging.startMessagingService](#startMessagingService)
* [hybridmessaging.requestVerification](#requestVerification)
* [hybridmessaging.requestVerificationVoiceCall](#requestVerificationVoiceCall)
* [hybridmessaging.verifyPin](#verifyPin)
* [hybridmessaging.getVerificationStatus](#getVerificationStatus)
* [hybridmessaging.getMessages](#getMessages)


## Enumerations
* [hybridmessaging.DeviceRegistrationStatus](#DeviceRegistrationStatus)


## <a name="getDeviceIdValue"></a>getDeviceIdValue(successCallback, errorCallback)

Returns a device ID value

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

Returns a msisdn value

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

Sets development mode for iOS, which will use Development Sandbox for push messages then.

### Parameters

* enableDevelopment: `boolean`, (required)

### Example

```javascript
window.hybridmessaging.enableDevelopment(true);
```


## <a name="configureService"></a>configureService(config) 

Configures service with key, secret and callbacks

### Parameters

* config: `Object`, (required) - config object with format:

```javascript
{
    appKey : 'string',
    appSecret : 'string',
    senderId : 'string', (Android only)
    notificationCallback : 'function', 
    deviceCarrierUpdateCallback : 'function'
}
```

### Example
    
```javascript    
function notificationCallback(alertMessage) {
    console.log(alertMessage);
}

function deviceCarrierUpdateCallback() {
    console.log('Device carrier has changed');
}

var options = {
    appKey : YOUR_APP_KEY,
    appSecret : YOUR_APP_SECRET,
    senderId : YOUR_ANDROID_SENDER_ID,
    notificationCallback: notificationCallback,
    deviceCarrierUpdateCallback: deviceCarrierUpdateCallback
};
    
window.hybridmessaging.configureService(options);
```

## <a name="startMessagingService"></a>startMessagingService(successCallback, errorCallback) 

Enables messaging service

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


## <a name="requestVerificationVoiceCall"></a>requestVerificationVoiceCall(msisdn, successCallback, errorCallback) 

Requests the phone number verification via voice call

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


## <a name="verifyPin"></a>verifyPin(pin, successCallback, errorCallback) 

Requests pin verification. Returns current [DeviceRegistrationStatus](#DeviceRegistrationStatus) in successful callback

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

Returns messages that have been sent by Hybrid Messaging

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
