/**
 *
 * Copyright (c) CM Telecom 2016. All rights reserved.
 *
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

    /**
     * Returns a device ID value
     * @param {function} successCallback (required) - callback function to be called upon successfully fetching device ID
     * @param {function} errorCallback (optional) - callback function to be called upon error
     */
    getDeviceIdValue: function(successCallback, errorCallback) {
        var methodName = 'getDeviceIdValue';
        argscheck.checkArgs('fF', hybridmessaging.pluginName + '.' + methodName, arguments);

        exec(successCallback, errorCallback, hybridmessaging.pluginName, methodName, []);
    },

    /**
     * Returns a msisdn value
     * @param {function} successCallback (required) - callback function to be called upon successfully fetching MSISDN
     * @param {function} errorCallback (optional) - callback function to be called upon error
     */
    getMsisdnValue: function(successCallback, errorCallback) {
        var methodName = 'getMsisdnValue';
        argscheck.checkArgs('fF', hybridmessaging.pluginName + '.' + methodName, arguments);

        exec(successCallback, errorCallback, hybridmessaging.pluginName, methodName, []);
    },

    /**
     * Sets development mode for iOS, which will use Development Sandbox for push messages then.
     * @param {boolean} enableDevelopment (required)
     */
    setDevelopment: function(enableDevelopment) {
        var methodName = 'setDevelopment';
        argscheck.checkArgs('b', hybridmessaging.pluginName + '.' + methodName, arguments);

        exec(null, null, hybridmessaging.pluginName, methodName, [enableDevelopment]);
    },

    /**
     * Configures service with key, secret and callbacks
     * @param {Object} config (required) - config object with format {appKey : 'string', appSecret : 'string', senderId : 'string', notificationCallback : 'function', deviceCarrierUpdateCallback : 'function'}
     */
    configureService: function(config) {
        var methodName = 'configureService';
        argscheck.checkArgs('o', hybridmessaging.pluginName + '.' + methodName, arguments);

        var parameters = [config.appKey, config.appSecret, config.senderId];
        for (var i = 0; i <= 1; i++) {
            if (typeof parameters[i] !== "string" || parameters[i] === ""){
                throw new Error("certa.push.configureService failure: config arguments missing or TypeError - {appKey : 'string', appSecret : 'string', senderId : 'string', notificationCallback : 'function', deviceCarrierUpdateCallback : 'function'}");
            }
        }

        var notificationCallback = config.notificationCallback ? config.notificationCallback : function() {};
        if (typeof notificationCallback !== "function") {
            throw new TypeError("certa.push.configureService failure: config.notificationCallback must be a function");
        }

        var deviceCarrierUpdateCallback = config.deviceCarrierUpdateCallback ? config.deviceCarrierUpdateCallback : function() {};
        if (typeof deviceCarrierUpdateCallback !== "function") {
            throw new TypeError("certa.push.configureService failure: config.deviceCarrierUpdateCallback must be a function");
        }

        this.notificationCallback = function (userInfo) { notificationCallback(userInfo); };
        this.deviceCarrierUpdateCallback = function () { deviceCarrierUpdateCallback(); };

        exec(null, null, hybridmessaging.pluginName, methodName, [config]);
    },

    /**
     * Enables messaging service
     * @param {function} successCallback (required) - callback function to be called upon successfully started messaging service
     * @param {function} errorCallback (optional) - callback function to be called upon error
     */
    startMessagingService: function(successCallback, errorCallback) {
        var methodName = 'startMessagingService';
        argscheck.checkArgs('fF', hybridmessaging.pluginName + '.' + methodName, arguments);

        exec(successCallback, errorCallback, hybridmessaging.pluginName, methodName, []);
    },

    /**
     * Requests the phone number verification
     * @param {string} msisdn (required) - phone number to verify
     * @param {function} successCallback (required) - callback function to be called upon successfully requesting number verification
     * @param {function} errorCallback (optional) - callback function to be called upon error
     */
    requestVerification: function(msisdn, successCallback, errorCallback) {
        var methodName = 'requestVerification';
        argscheck.checkArgs('sfF', hybridmessaging.pluginName + '.' + methodName, arguments);

        exec(successCallback, errorCallback, hybridmessaging.pluginName, methodName, [msisdn]);
    },

    /**
     * Requests the phone number verification via voice call
     * @param {string} msisdn (required) - phone number to verify
     * @param {function} successCallback (required) - callback function to be called upon successfully requesting PIN voice call
     * @param {function} errorCallback (optional) - callback function to be called upon error
     */
    requestVerificationVoiceCall: function(successCallback, errorCallback) {
        var methodName = 'requestVerificationVoiceCall';
        argscheck.checkArgs('fF', hybridmessaging.pluginName + '.' + methodName, arguments);

        exec(successCallback, errorCallback, hybridmessaging.pluginName, methodName, []);
    },

    /**
     * Requests pin verification
     * @param {string} pin (required) - pin to verify
     * @param {function} successCallback (required) - callback function to be called upon successfully executing PIN verification (with verification either passed or failed, but in any case correctly executed)
     * @param {function} errorCallback (optional) - callback function to be called upon error
     */
    verifyPin: function(pin, successCallback, errorCallback) {
        var methodName = 'verifyPin';
        argscheck.checkArgs('sfF', hybridmessaging.pluginName + '.' + methodName, arguments);

        exec(successCallback, errorCallback, hybridmessaging.pluginName, methodName, [pin]);
    },

    /**
     * Requests current verification status
     * @param {function} successCallback (required) - callback function to be called upon successfully fetching the current status
     * @param {function} errorCallback (optional) - callback function to be called upon error
     */
    getVerificationStatus: function(successCallback, errorCallback) {
        var methodName = 'getVerificationStatus';
        argscheck.checkArgs('fF', hybridmessaging.pluginName + '.' + methodName, arguments);

        exec(successCallback, errorCallback, hybridmessaging.pluginName, methodName, []);
    },

    /**
     * Returns messages that have been sent by Hybrid Messaging
     * @param {number} limit (optional) - amount of messages to retrieve
     * @param {number} offset (optional) - offset from which messages to retrieve
     * @param {function} successCallback (required) - callback to be called upon successful message retrieval
     * @param {function} errorCallback (optional) - callback to be called upon error
     */
    getMessages: function(limit, offset, successCallback, errorCallback) {
        var methodName = 'getMessages';
        argscheck.checkArgs('NNfF', hybridmessaging.pluginName + '.' + methodName, arguments);

        exec(successCallback, errorCallback, hybridmessaging.pluginName, methodName, [limit, offset]);
    }
};

module.exports = hybridmessaging;
