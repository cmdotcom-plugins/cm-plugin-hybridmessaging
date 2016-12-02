//
//  HybridMessagingPlugin.h
//  
//
//  Copyright (c) CM Telecom 2016. All rights reserved.
//
//

#import <UIKit/UIKit.h>
#import <Cordova/CDVPlugin.h>

@interface HybridMessagingPlugin : CDVPlugin

- (void)getDeviceIdValue:(CDVInvokedUrlCommand*)command;
- (void)getMsisdnValue:(CDVInvokedUrlCommand*)command;

- (void)setDevelopment:(CDVInvokedUrlCommand*)command;
- (void)configureService:(CDVInvokedUrlCommand*)command;
- (void)startMessagingService:(CDVInvokedUrlCommand*)command;

- (void)requestVerification:(CDVInvokedUrlCommand*)command;
- (void)requestVerificationVoiceCall:(CDVInvokedUrlCommand*)command;
- (void)verifyPin:(CDVInvokedUrlCommand*)command;
- (void)getVerificationStatus:(CDVInvokedUrlCommand*)command;

- (void)getMessages:(CDVInvokedUrlCommand*)command;

@end
