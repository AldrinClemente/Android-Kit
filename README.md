Android Kit
=========

Android Kit is a collection of useful classes and methods to make some development tasks easier. This was originally my personal set of utilities which I've been using for a few years now, and I've decided to share it with everyone in hopes that it will also save people some development time.

You may also be interested in [Swift Kit](https://github.com/AldrinClemente/Swift-Kit) if you're an iOS developer.

----------

Features
------------

#### HTTP Kit
Makes HTTP requests simpler. Includes option to use your own trust store or key store, or completely bypass SSL client authentication if needed. Multi-part content body is also supported. All important details about the request and the response can also be automatically logged if desired.

#### Crypto Kit
Includes a comprehensive set of utility methods for your cryptography needs. Supports cross-platform encryption/decryption of data (see [Swift Kit](https://github.com/AldrinClemente/Swift-Kit) for iOS) using the default crypto spec. You may also define your own spec to suits your needs (set the encryption algorithm, block cipher mode, padding, salt length, HMAC salt length, HMAC key length, key derivation iterations, PRF algorithm and MAC algorithm).

Encryption using a crypto spec implements secure random IV, password-based key derivation (PBKDF2) and keyed-hash message authentication code (HMAC). You may also use the different methods available to implement your own data format for encrypted data.

#### Secure Data File
An encrypted data file you can use to persist app data without worrying about user tampering. Very useful for protecting game data against cheaters or hiding sensitive information from prying eyes.

#### Secure Variables
A set of classes which protect primitives from memory editors and similar cheating tools. Designed to combat game cheaters and enforce fair play on your games.

#### Logger
A switchable logging utility which you can easily turn on or off. Ideally, you'll use this with `BuildConfig.DEBUG` so logs will be automatically turned on or off depending on your build in case you don't use ProGuard.

#### Timer
A simple utility you can use to measure elapsed time or benchmarking your code. Multiple timers can run at the same time.

#### Toast
A utility to make showing toasts cleaner. You can show toasts instantly or queue them as needed.

#### App Utils
A collection of utility methods related to apps such as getting the version code, checking if an app is installed, checking if an app is installed from the Play Store, launching the Play Store product page of an app, and more.

#### System Utils
A collection of utility methods related to the system or device, such as getting the SDK version, getting the device ID, checking if a process or app is running, converting DP to PX and vice versa, checking of the device is connected to a network, getting the network operator name, checking if the GPS is enabled and more.

#### Async
A utility to make it easier to run tasks and `Runnable`s asynchronously without troubling yourself with `AsyncTask`s. Includes the option to run tasks serially or in parallel, or force a `Runnable` to run on the main thread instead.

#### Bitmap Utils
A collection of utility methods related to `Bitmap`s, such as getting the bounds, computing the in sample size, decoding a file, stream or byte array into a `Bitmap`, decoding a `Bitmap` from a URL, cropping, orientation correction, and more.

#### Cursor Utils
A collection of utility methods for easy extraction of field values from a `Cursor`.

#### File Utils
A collection of utility methods related to `File`s, such as getting the MIME type, getting the extension, getting the formatted file size, and more.

#### JSON Utils
A collection of utility methods to make it easier to extract values from `JSONObject`s or `JSONArray`s. Retrieve values by path (e.g. user/0/name), parse JSON strings without messy try/catch blocks and more.

#### SMS Utils
A collection of utility methods for sending SMS with an option to listen to the delivery status, combining received multipart SMS into a single string and more.

#### String Utils
A collection of utility methods for String processing and checking, such as checking if a string is a valid URL, getting the left or right substring and more.

#### View Utils
A collection of utility methods related to `View`s, such as getting values from `View`s, getting `View`s by tag, getting and setting the selected item in a `Spinner`, batch setting of visibility, batch setting of `Typeface` and more.

----------

Notes
------------
APIs/method names may still change a few times while we're still below 1.0. I'm still trying to decide on some class and method names and where to put them.

----------

Bug Reports and Requests
------------
Please submit bugs using the [issue tracker](https://github.com/AldrinClemente/Android-Kit/issues/new). Feature requests are very welcome too!
