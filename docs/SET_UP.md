# Set up
## Firebase Authentication
1. copy `local.properties.examples`
2. search for _Google Web Client ID_ in firebase (Security > Authentication > Access Methods > Google > SDK Configuration)
3. in the same directory as `app/google-service.example.json`, put the `google-services.json` downloaded from firebase (App Android from project)
4. add your local debug or production key fingerprint (SHA1, SHA256) to your firebase
   ```bash 
   keytool -list -v -keystore [path/to/your_keystore.jks] -alias [your_alias_name]
   ```
   Default debug keystore for windows 
   ```bash 
   keytool -list -v -keystore %USERPROFILE%\.android\debug.keystore -alias androiddebugkey -storepass android
   ```
   Default debug keystore for MacOS or Linux
   ```bash 
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android
   ```


