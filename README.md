ydf-applet
==========

YourDigitalFile's CryptoApplet-20140519102015.jar meets JD-GUI for auditing purposes

If you access this page: `https://www.yourdigitalfile.com.au/cryptoloc_applet/`

You will get a block of code that looks like:

```js

            var attributes = {
                    codebase:'/static/java',
                    id: 'cryptoloc_applet',
                    code:'au.com.yourdigitalfile.crypto_applet.CryptoApplet.class',
                    archive: 'CryptoApplet-20140519102015.jar',
                    width: '240', 
                    height: '100',
                    plugins_page: "http://java.com/en/download/",
                    MAYSCRIPT: true,
                    applet_stop_timeout: 3000,
                    codebase_lookup: false
            };
            var parameters = {
                    //java_arguments: '-Xmx256m',
                    separate_jvm: true,
                    codebase_lookup: false,
                    java_status_events: true,
                    sessionKey: '', // lol
                    hostname: 'https://www.yourdigitalfile.com.au'
            }; // customize per your needs
            var version = '1.6' ; // JDK version
            deployJava.runApplet(attributes, parameters, version);
            
```

I used JD-GUI to extract all the source from the .jar `http://jd.benow.ca/`

And now I will begin to audit it.
