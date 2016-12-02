//
//  HybridMessaging.h
//  libHybridMessaging
//
//  Copyright (c) 2015 CM. All rights reserved.
//

#define kHybridMessagingSDKVersion @"1.2.5"

#import <Foundation/Foundation.h>
#import <CoreTelephony/CTCarrier.h>

typedef NS_ENUM(NSInteger, HMDeviceRegistrationStatus) {
    HMDeviceRegistrationStatusUnknown                   = 0,
    HMDeviceRegistrationStatusUnverified                = 1,
    HMDeviceRegistrationStatusWaitingForPin             = 2,
    HMDeviceRegistrationStatusLastPinVerificationFailed = 3,
    HMDeviceRegistrationStatusPinVerified               = 4,
    HMDeviceRegistrationStatusInvalid                   = 5
};

typedef NS_OPTIONS(NSUInteger, HMUserNotificationType) {
    HMUserNotificationTypeNone    = 0,      // the application may not present any UI upon a notification being received
    HMUserNotificationTypeBadge   = 1 << 0, // the application may badge its icon upon a notification being received
    HMUserNotificationTypeSound   = 1 << 1, // the application may play a sound upon a notification being received
    HMUserNotificationTypeAlert   = 1 << 2, // the application may display an alert upon a notification being received
};

/**
 *  HybridMessaging SDK
 *
 *  Configure the SDK in the application delegate class
 *
 *  - (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
 *      [HybridMessaging configureWithKey:@"YOUR_APPLICATION_KEY" secret:@"YOUR_APPLICATION_SECRET"];
 *      [HybridMessaging enable];
 *
 *      ...
 */
@interface HybridMessaging : NSObject

/**
 *  Configure the Hybrid Messaging SDK.
 *
 *  This method should be called from application:didFinishLaunchingWithOptions:
 *
 *  @param key       Application Key
 *  @param secretKey Secret Key
 */
+ (void)configureWithKey:(NSString *)key secret:(NSString *)secretKey;

/**
 *  Gets the user notification types
 *
 *  @return user notification types
 */
+ (HMUserNotificationType)userNotificationTypes;

/**
 * Set the user notification types (optional). Defaults to Alert, Badge and Sound.
 */
+ (void)setUserNotificationTypes:(HMUserNotificationType)types;

/**
 *  Gets the device development mode. Defaults to NO
 *
 *  @return bool
 */
+ (BOOL)development;

/**
 *  Sets the device development mode.
 *
 *  When this value is set to YES, the device registration is marked as a development device,
 *  and our server automatically uses the development push certificate (if configured)
 *  while sending a push notification to the device.
 *
 *  Set this value before calling any device verification methods,
 *  preferably at the start of the application in `- (BOOL)application:didFinishLaunchingWithOptions:`
 *
 *  Remember to this value to NO in a production build or remove the method call
 *
 *  @param enabled YES or NO
 */
+ (void)setDevelopment:(BOOL)yesOrNo;

/**
 *  Get the Device ID
 *
 *  @return Device ID or nil
 */
+ (NSString *)deviceId;

/**
 *  Get the Push Token
 *
 *  @return Push Token or nil
 */
+ (NSString *)pushToken;

/**
 *  Get the Msisdn
 *
 *  @return Msisdn or nil
 */
+ (NSString *)msisdn;

/**
 *  Get the SDK Version
 *
 *  @return SDK Version
 */
+ (NSString *)version;

/**
 *  Enable user notifications
 */
+ (void)enable;

/**
 *  Disable user notifications
 */
+ (void)disable;

/**
 *  Request msisdn verification
 *
 *  @param msisdn        The user's phone number in international format. e.g. 0031612345678
 *  @param statusHandler Device Registration Status handler
 */
+ (void)requestPhoneVerification:(NSString *)msisdn handler:(void (^)(HMDeviceRegistrationStatus status))statusHandler;

/**
 *  Request msisdn verification voice call
 *
 *  @param msisdn        The user's phone number in international format
 *  @param statusHandler Device Registration Status handler
 */
+ (void)requestPhoneVerificationVoiceCall:(NSString *)msisdn handler:(void (^)(BOOL success))statusHandler;

/**
 *  Request PIN verification
 *
 *  @param pin           Pin code
 *  @param statusHandler Device Registration Status handler
 */
+ (void)requestPinVerification:(NSString *)pin handler:(void (^)(HMDeviceRegistrationStatus status))statusHandler;

/**
 *  Request information about the registration
 *
 *  deviceRegistration is a dictionary with the following fields: {
 *    "DeviceID":"9386b95d-d31b-49bd-8bf8-f203bed9236e",
 *    "Platform":"iOS",
 *    "RegistrationStatus":"Unverified",
 *    "Created":"2014-10-22T18:02:45.427",
 *    "RegisteredForPushNotification":false
 *  }
 *
 *  @param handler
 */
+ (void)requestRegistrationInformation:(void (^)(NSDictionary *deviceRegistration))handler;

/**
 *  Request the device verification status
 *
 *  @param statusHandler
 */
+ (void)requestVerificationStatus:(void(^)(HMDeviceRegistrationStatus status))statusHandler;

/**
 *  Request GeoIP information
 *
 *  @param handler
 */
+ (void)requestGeoIpInfo:(void(^)(NSDictionary *info))handler;

/**
 *  Gets a list of messages
 *
 *  @param handler Message handler
 */
+ (void)messages:(void (^)(NSArray *messages, NSError *error))handler;

/**
 *  Gets a list of messages with a specified limit
 *
 *  @param limit    Number of messages to return
 *  @param handler  Message handler
 */
+ (void)messagesWithLimit:(NSInteger)limit handler:(void (^)(NSArray *messages, NSError *error))handler;

/**
 *  Gets a list of messages with a specified limit
 *
 *  @param limit    Number of messages to return
 *  @param offset   Message offset
 *  @param handler  Message handler
 */
+ (void)messagesWithLimit:(NSInteger)limit offset:(NSInteger)offset handler:(void (^)(NSArray *messages, NSError *error))handler;

/**
 *  Gets a list of messages filtered by parameters
 *
 *  @param parameters Filter parameters
 *  @param handler    Request handler
 */
+ (void)messagesWithParameters:(NSDictionary *)parameters handler:(void (^)(NSArray *messages, NSError *error))handler;

/**
 *  Gets a list of messages filtered by parameters
 *
 *  @param parameters Filter parameters
 *  @param handler    Request handler
 */
+ (void)messagesWithParameters:(NSDictionary *)parameters requestHandler:(void (^)(NSArray *messages, NSError *error, NSHTTPURLResponse *response))handler;

/**
 *  Sets a callback to be executed when the push registration succeeds
 *
 *  @param block Callback block
 */
+ (void)setDidRegisterForRemoteNotificationsWithDeviceTokenBlock:(void(^)(NSData *deviceToken))block;

/**
 *  Sets a callback to be executed when the push registration fails
 *
 *  @param block Callback block
 */
+ (void)setDidFailToRegisterForRemoteNotificationsWithErrorBlock:(void(^)(NSError *error))block;

/**
 *  Sets a callback to be executed when a push notification is received
 *
 *  @param block Callback block
 */
+ (void)setDidReceiveRemoteNotificationBlock:(void(^)(NSDictionary *userInfo))block;

/**
 *  Sets a callback to be executed when the carrier has changed
 *
 *  @param block Callback block
 */
+ (void)setDeviceDidUpdateCarrierBlock:(void(^)(CTCarrier *carrier))block;

/**
 *  Notification status update
 *
 *  @param block Callback block
 */
+ (void)setNotificationDeliveredStatus:(NSString *)updateId finished:(void (^)(NSError *))handler;


/**
 *  Reset push token and device ID
 */
+ (void)reset;

- (instancetype)init __attribute__((unavailable("init is unavailable")));

@end
