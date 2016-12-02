/**
 * Created by ymyhailova on 21/11/2016.
 */

var argscheck = require("cordova/argscheck"),
    exec = require("cordova/exec");

var hybridmessaging = {
    pluginName: 'HybridMessaging',

    DeviceRegistrationStatus: {
        UNVERIFIED: 1,
        WAITING_FOR_PIN: 2,
        LAST_PIN_VERIFICATION_FAILED: 3,
        PIN_VERIFIED: 4,
        INVALID: 5,
        UNKNOWN: 6
    },

    // This function can be called from the native implementation
    notificationCallback: function() {},
    deviceCarrierUpdateCallback: function() {},
    pushRegistrationSuccessCallback: function() {},
    pushRegistrationErrorCallback: function() {},

    /**
     * Returns a device ID value
     * @param {function} successCallback (optional) - callback function to be called with a result
     */
    getDeviceIdValue: function(successCallback) {
        var methodName = 'getDeviceIdValue';
        argscheck.checkArgs('F', hybridmessaging.pluginName + '.' + methodName, arguments);

        exec(successCallback, null, hybridmessaging.pluginName, methodName, []);
    },

    /**
     * Returns a msisdn value
     * @param {function} successCallback (optional) - callback function to be called with a result
     */
    getMsisdnValue: function(successCallback) {
        var methodName = 'getMsisdnValue';
        argscheck.checkArgs('F', hybridmessaging.pluginName + '.' + methodName, arguments);

        exec(successCallback, null, hybridmessaging.pluginName, methodName, []);
    },

    /**
     * Sets development mode for iOS, which will use Development Sanbox for push messages then.
     * @param {boolean} enableDevelopment (required)
     */
    setDevelopment: function(enableDevelopment) {
        var methodName = 'setDevelopment';
        argscheck.checkArgs('b', hybridmessaging.pluginName + '.' + methodName, arguments);

        exec(null, null, hybridmessaging.pluginName, methodName, [enableDevelopment]);
    },

    /**
     * Configures service with key, secret and callbacks
     * @param {Object} options (required) - config object with format {appKey : 'string', appSecret : 'string', notificationCallback : 'function', deviceCarrierUpdateCallback : 'function'}
     */
    configureService: function(options) {
        var methodName = 'configureService';
        argscheck.checkArgs('o', hybridmessaging.pluginName + '.' + methodName, arguments);

        var parameters = [options.appKey, options.appSecret];
        for (var i = 0; i <= 1; i++) {
            if (typeof parameters[i] !== "string" || parameters[i] === ""){
                throw new Error("certa.push.configureService failure: options arguments missing or TypeError - {appKey : 'string', appSecret : 'string', notificationCallback : 'function', deviceCarrierUpdateCallback : 'function'}");
            }
        }

        var notificationCallback = options.notificationCallback ? options.notificationCallback : function() {};
        if (typeof notificationCallback !== "function") {
            throw new TypeError("certa.push.configureService failure: options.notificationCallback must be a function");
        }

        var deviceCarrierUpdateCallback = options.deviceCarrierUpdateCallback ? options.deviceCarrierUpdateCallback : function() {};
        if (typeof deviceCarrierUpdateCallback !== "function") {
            throw new TypeError("certa.push.configureService failure: options.deviceCarrierUpdateCallback must be a function");
        }

        this.notificationCallback = function (userInfo) { notificationCallback(userInfo); };
        this.deviceCarrierUpdateCallback = function () { deviceCarrierUpdateCallback(); };

        exec(null, null, hybridmessaging.pluginName, methodName, parameters);
    },

    /**
     * Sets callbacks for push registration and enables messaging service
     * @param {function} successCallback (optional) - success push registration callback
     * @param {function} failureCallback (optional) - error push registration callback
     */
    startMessagingService: function(successCallback, failureCallback) {
        var methodName = 'startMessagingService';
        argscheck.checkArgs('FF', hybridmessaging.pluginName + '.' + methodName, arguments);

        if ((successCallback === undefined) || (successCallback === null)) {
            successCallback = function() {};
        }
        if ((failureCallback === undefined) || (failureCallback === null)) {
            failureCallback = function() {};
        }

        this.pushRegistrationSuccessCallback = function () { successCallback(); };
        this.pushRegistrationErrorCallback = function () { failureCallback(); };

        exec(null, null, hybridmessaging.pluginName, methodName, []);
    },

    /**
     * Requests the phone number verification
     * @param {string} msisdn (required) - phone number to verify
     * @param {function} successCallback (optional) - callback function to be called on verification result
     */
    requestVerification: function(msisdn, successCallback) {
        var methodName = 'requestVerification';
        argscheck.checkArgs('sF', hybridmessaging.pluginName + '.' + methodName, arguments);

        exec(successCallback, null, hybridmessaging.pluginName, methodName, [msisdn]);
    },

    /**
     * Requests the phone number verification via voice call
     * @param {string} msisdn (required) - phone number to verify
     * @param {function} successCallback (optional) - callback on voice call success
     * @param {function} errorCallback (optional) - callback on voice call error
     */
    requestVerificationVoiceCall: function(msisdn, successCallback, errorCallback) {
        var methodName = 'requestVerificationVoiceCall';
        argscheck.checkArgs('sFF', hybridmessaging.pluginName + '.' + methodName, arguments);

        exec(successCallback, errorCallback, hybridmessaging.pluginName, methodName, [msisdn]);
    },

    /**
     * Requests pin verification
     * @param {string} pin (required) - pin to verify
     * @param {function} successCallback (optional) - callback function to be called on verification result
     */
    verifyPin: function(pin, successCallback) {
        var methodName = 'verifyPin';
        argscheck.checkArgs('sF', hybridmessaging.pluginName + '.' + methodName, arguments);

        exec(successCallback, null, hybridmessaging.pluginName, methodName, [pin]);
    },

    /**
     * Requests current verification status
     * @param successCallback (optional) - callback function to be called on verification result
     */
    getVerificationStatus: function(successCallback) {
        var methodName = 'getVerificationStatus';
        argscheck.checkArgs('F', hybridmessaging.pluginName + '.' + methodName, arguments);

        exec(successCallback, null, hybridmessaging.pluginName, methodName, []);
    },

    /**
     * Returns messages that have been sent by Hybrid Messaging
     * @param limit (optional) - amount of messages to retrieve
     * @param offset (optional) - offset from which messages to retrieve
     * @param successCallback (optional) - callback to be called on successful message retrieval
     * @param errorCallback (optional) - callback to be called on message retrieval error
     */
    getMessages: function(limit, offset, successCallback, errorCallback) {
        var methodName = 'getMessages';
        argscheck.checkArgs('NNFF', hybridmessaging.pluginName + '.' + methodName, arguments);

        exec(successCallback, errorCallback, hybridmessaging.pluginName, methodName, [limit, offset]);
    }
};

module.exports = hybridmessaging;