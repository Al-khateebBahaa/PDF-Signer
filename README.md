<h1 align="center">PDF-Signer</h1>

<p align="center">
    <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
</p>

<p align="center">
🪡 PDF-Signer is a Digitally sign PDF documents using handwritten signature or specific image.
</p>


![Configure and Initialize](./readmeresources/sign1.mov)
![Configure and Initialize](./readmeresources/sign2.mov)

## Why PDF-Signer?

1. Easy, flexible.
2. Support add sign from gallery and handwrite
3. Support multiple colors for handwrite
4. Save signs on local storage

## Start using PDF-Signer

### Gradle

## Go to your gradle settings and ensure jitpack.io is existing:


```gradle
 repositories {
        maven { url 'https://jitpack.io' }
        ...
    }
```


Add the dependency below to your **module**'s `build.gradle` file:

```gradle
dependencies {
    implementation "com.github.AlMOHANDSen:PDF_Signer_Tool:Tag"
}
```

## How to Use

PDF-Signer supports both Kotlin and Java projects, so you can reference it by your language.

## Requirements

- Android API 23 or later

## Initialization

1. At onCreate method, invoke SDK onCreate

```kotlin
 SignBuilder.onCreate(activity: AppCompatActivity)
```

2. Implement `SignerCallBack` interface,

```kotlin
class MainActivity : AppCompatActivity(), SignerCallback {

}
```

3. Override SignerCallback callback methods:

```kotlin

override fun onSignResult(isSuccess: Boolean, resultFile: File?) {
    // if signing success, isSuccess param will return true with signed file

}

override fun onSignFailed(error: String) {

    //SDK will return any error here

}
```

4. Register your Activity/Fragment callback and build SDK.

```kotlin

try {
    val mSignManager = SignBuilder.setCallback(this).build()

} catch (e: PdfSignerInitialingException) {
    e.printStackTrace()
}

```

5. Now you can pass your file to SDK and start signing process by call:

```kotlin

mSignManager.startSigning(
    inputFile: File?,
returnedFileName: String? = null, // optional returned file name
activity: AppCompatActivity
)

```

You will get result either on onSignResult or onSignFailed

## Error types:

1. FILE_NOT_FOUND : Returned When passing a nullable or invalid file
2. ON_CREATE_ERROR : Returned When forget to invoke SDK onCreate method
3. DELEGATE_NOT_FOUND : Returned When forget to attach SignerCallback
4. INVALID_FILE_TYPE : File type is not a valid pdf file
5. FAILED_TO_SIGN : Failed to add sign

## Additional

1. You can open Signatures library "Which contain your Signatures" by:

```kotlin

mSignManager.openSigningLibrary(context:Context)

```

![Configure and Initialize](./readmeresources/sign3.mov)

2. If you want to pick a Signatures image from gallery, you should know that supported types are
   just **jpeg or png**.

3. This library still on testing phase, so you may find some issues

4. Files not saved, but Signatures saved at Signatures library

# License

```xml
Copyright 2023 Al-khateebBahaa (Bahaa Alkhateeb)

    Licensed under the Apache License, Version 2.0 (the "License");you may not use this file except in compliance with the License.You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, softwaredistributed under the License is distributed on an "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.See the License for the specific language governing permissions andlimitations under the License.
```

    
