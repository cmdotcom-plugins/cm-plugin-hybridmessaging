//
//  HybridMessagingPlugin.m
//  
//
//  Created by Yulia Myhailova on 22/11/2016.
//
//

#import "HybridMessagingPlugin.h"
#import "HybridMessaging.h"

@implementation HybridMessagingPlugin

#pragma mark - HybridMessaging getters

- (void)getDeviceIdValue:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:[HybridMessaging deviceId]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)getMsisdnValue:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:[HybridMessaging msisdn]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

#pragma mark - HybridMessaging functions

- (void)setDevelopment:(CDVInvokedUrlCommand*)command {
    BOOL enableDevMode = [command argumentAtIndex:0];

    [HybridMessaging setDevelopment:enableDevMode];
}

- (void)configureService:(CDVInvokedUrlCommand*)command {
    id appKey = [command argumentAtIndex:0];
    id appSecret = [command argumentAtIndex:1];

    [HybridMessaging configureWithKey:appKey secret:appSecret];

    [HybridMessaging setDidReceiveRemoteNotificationBlock:^(NSDictionary *userInfo) {
        NSLog(@"User info %@", userInfo);
        NSMutableString *jsonString = [NSMutableString stringWithString:@"{"];
        [self parseDictionary:userInfo[@"aps"] intoJSON:jsonString];
        NSString *userInfoJSON = [NSString stringWithFormat:@"%@}", jsonString];
        NSLog(@"User info json string %@", userInfoJSON);
        NSString * notificationCallbackFunction = [NSString stringWithFormat:@"hybridmessaging.notificationCallback(%@);", userInfoJSON];
        [self callJavaScriptFunction:notificationCallbackFunction];
    }];

    [HybridMessaging setDeviceDidUpdateCarrierBlock:^(CTCarrier *carrier) {
        [self callJavaScriptFunction:@"hybridmessaging.deviceCarrierUpdateCallback();"];
    }];
}

- (void)startMessagingService:(CDVInvokedUrlCommand*)command {
    [HybridMessaging setDidRegisterForRemoteNotificationsWithDeviceTokenBlock:^(NSData *deviceToken) {
        NSLog(@"%@", [deviceToken description]);
        [self callJavaScriptFunction:@"hybridmessaging.pushRegistrationSuccessCallback();"];
    }];

    [HybridMessaging setDidFailToRegisterForRemoteNotificationsWithErrorBlock:^(NSError *error) {
        NSLog(@"%@", error);
        [self callJavaScriptFunction:@"hybridmessaging.pushRegistrationErrorCallback();"];
    }];

    [HybridMessaging enable];
}

#pragma mark - HybridMessaging functions - verification functions

- (void)requestVerification:(CDVInvokedUrlCommand*)command {
    NSString *msisdn = [command argumentAtIndex:0];
    [HybridMessaging requestPhoneVerification:msisdn handler:^(HMDeviceRegistrationStatus status) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:[HybridMessagingPlugin numberFromRegistrationStatus:status]];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)requestVerificationVoiceCall:(CDVInvokedUrlCommand*)command {
    NSString *msisdn = [command argumentAtIndex:0];
    [HybridMessaging requestPhoneVerificationVoiceCall:msisdn handler:^(BOOL success) {
        CDVPluginResult* pluginResult = nil;
        if (success) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        }
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)verifyPin:(CDVInvokedUrlCommand*)command {
    NSString *pin = [command argumentAtIndex:0];

    [HybridMessaging requestPinVerification:pin handler:^(HMDeviceRegistrationStatus status) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:[HybridMessagingPlugin numberFromRegistrationStatus:status]];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)getVerificationStatus:(CDVInvokedUrlCommand*)command {
    [HybridMessaging requestVerificationStatus:^(HMDeviceRegistrationStatus status) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:[HybridMessagingPlugin numberFromRegistrationStatus:status]];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

#pragma mark - HybridMessaging functions - messages functions
- (void)getMessages:(CDVInvokedUrlCommand*)command {
    NSNumber *limit = [command argumentAtIndex:0];
    NSNumber *offset = [command argumentAtIndex:1];

    void (^handler)(NSArray *, NSError *) = ^void(NSArray *messages, NSError *error) {
        CDVPluginResult* pluginResult = nil;
        if (error) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:messages];
        }
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    };

    if (limit) {
        if (offset) {
            [HybridMessaging messagesWithLimit:[limit integerValue] offset:[offset integerValue] handler:handler];
        } else {
            [HybridMessaging messagesWithLimit:[limit integerValue] handler:handler];
        }
    } else {
        [HybridMessaging messages:handler];
    }
}

#pragma mark - HybridMessaging general functions
/**
 *  reentrant method to drill down and surface all sub-dictionaries' key/value pairs into the top level json
 *
 *  @param inDictionary dictionary to be parsed
 *  @param jsonString   JSON string to be sent back to javascript
 */
-(void)parseDictionary:(NSDictionary *)inDictionary intoJSON:(NSMutableString *)jsonString {
    NSArray *keys = [inDictionary allKeys];
    NSString *key;

    for (key in keys) {
        id thisObject = [inDictionary objectForKey:key];

        if ([thisObject isKindOfClass:[NSDictionary class]])
            [self parseDictionary:thisObject intoJSON:jsonString];
        else if ([thisObject isKindOfClass:[NSString class]])
            [jsonString appendFormat:@"\"%@\":\"%@\",", key,
             [[[[inDictionary objectForKey:key]
                stringByReplacingOccurrencesOfString:@"\\" withString:@"\\\\"]
               stringByReplacingOccurrencesOfString:@"\"" withString:@"\\\""]
              stringByReplacingOccurrencesOfString:@"\n" withString:@"\\n"]];
        else {
            [jsonString appendFormat:@"\"%@\":\"%@\",", key, [inDictionary objectForKey:key]];
        }
    }
}

/**
 *  calls a function from JavaScript with a fallback for newer iOS cordova version.
 *
 *  @param functionString - string representation of funciton to be called
 */
- (void)callJavaScriptFunction:(NSString *)functionString {
    //Cordova iOS 4.x compatibility requires handling both UIWebView and WKWebView which happen to differ in Javascript evaluation methods
    if ([self.webView respondsToSelector:@selector(stringByEvaluatingJavaScriptFromString:)]) {
        [self.webView performSelectorOnMainThread:@selector(stringByEvaluatingJavaScriptFromString:) withObject:functionString waitUntilDone:NO];
    } else {
        [self.webView performSelectorOnMainThread:@selector(evaluateJavaScript:completionHandler:) withObject:functionString waitUntilDone:NO];
    }
}

+ (int)numberFromRegistrationStatus:(HMDeviceRegistrationStatus)status {
    switch (status) {
        case HMDeviceRegistrationStatusUnverified:
            return 1;
        case HMDeviceRegistrationStatusWaitingForPin:
            return 2;
        case HMDeviceRegistrationStatusLastPinVerificationFailed:
            return 3;
        case HMDeviceRegistrationStatusPinVerified:
            return 4;
        case HMDeviceRegistrationStatusInvalid:
            return 5;
        default:
            return 6;
    }
}

@end
