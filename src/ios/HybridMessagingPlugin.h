//
//  HybridMessagingPlugin.h
//  
//
//  Created by Yulia Myhailova on 22/11/2016.
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
