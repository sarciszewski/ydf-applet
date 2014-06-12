/*   1:    */ package au.com.yourdigitalfile.crypto_applet.crypto;
/*   2:    */ 
/*   3:    */ import au.com.yourdigitalfile.crypto_applet.CryptoApplet;
/*   4:    */ import au.com.yourdigitalfile.crypto_applet.browser.BrowserHandler;
/*   5:    */ import au.com.yourdigitalfile.crypto_applet.http.UploadHandler;
/*   6:    */ import au.com.yourdigitalfile.crypto_applet.os.OSHandler;
/*   7:    */ import au.com.yourdigitalfile.crypto_applet.os.SaveKeystoreFileChooser;
/*   8:    */ import au.com.yourdigitalfile.crypto_applet.ui.UIHandler;
/*   9:    */ import au.com.yourdigitalfile.crypto_applet.util.Base64Utils;
/*  10:    */ import au.com.yourdigitalfile.crypto_applet.util.Util;
/*  11:    */ import java.io.File;
/*  12:    */ import java.io.FileInputStream;
/*  13:    */ import java.io.FileOutputStream;
/*  14:    */ import java.io.IOException;
/*  15:    */ import java.math.BigInteger;
/*  16:    */ import java.security.GeneralSecurityException;
/*  17:    */ import java.security.KeyPair;
/*  18:    */ import java.security.KeyPairGenerator;
/*  19:    */ import java.security.KeyStore;
/*  20:    */ import java.security.PrivateKey;
/*  21:    */ import java.security.PublicKey;
/*  22:    */ import java.security.SecureRandom;
/*  23:    */ import java.security.cert.Certificate;
/*  24:    */ import java.security.cert.X509Certificate;
/*  25:    */ import java.util.Arrays;
/*  26:    */ import java.util.Date;
/*  27:    */ import java.util.Enumeration;
/*  28:    */ import java.util.Map;
/*  29:    */ import java.util.logging.Level;
/*  30:    */ import java.util.logging.Logger;
/*  31:    */ import javax.crypto.Cipher;
/*  32:    */ import javax.swing.JFileChooser;
/*  33:    */ import sun.security.x509.AlgorithmId;
/*  34:    */ import sun.security.x509.CertificateAlgorithmId;
/*  35:    */ import sun.security.x509.CertificateIssuerName;
/*  36:    */ import sun.security.x509.CertificateSerialNumber;
/*  37:    */ import sun.security.x509.CertificateSubjectName;
/*  38:    */ import sun.security.x509.CertificateValidity;
/*  39:    */ import sun.security.x509.CertificateVersion;
/*  40:    */ import sun.security.x509.CertificateX509Key;
/*  41:    */ import sun.security.x509.X500Name;
/*  42:    */ import sun.security.x509.X509CertImpl;
/*  43:    */ import sun.security.x509.X509CertInfo;
/*  44:    */ 
/*  45:    */ public class CryptoHandler
/*  46:    */ {
/*  47: 49 */   private static final Logger log = Logger.getLogger(CryptoHandler.class.getName());
/*  48:    */   private static final String KEYSTORE_TYPE = "PKCS12";
/*  49:    */   private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
/*  50:    */   private static final String RSA_CIPHER_TYPE = "RSA/ECB/OAEPWithSHA1AndMGF1Padding";
/*  51:    */   private static final String RSA_KEY_PAIR_TYPE = "RSA";
/*  52:    */   private static final int RSA_KEY_SIZE = 2048;
/*  53:    */   private static final int certificateValidityDays = 2557;
/*  54:    */   private static final String certificateDistinguishedName = "CN=Your Digital File, C=AU";
/*  55:    */   private static final String KEYSTORE_FILE_NAME = "ydf.pfx";
/*  56:    */   private static final String KEYSTORE_ALIAS = "YOUR DIGITAL FILE Private Key";
/*  57: 63 */   private static PrivateKey privateKey = null;
/*  58:    */   private static String keyStorePath;
/*  59:    */   
/*  60:    */   public CryptoHandler()
/*  61:    */   {
/*  62: 68 */     log.finest("Crypto init");
/*  63:    */   }
/*  64:    */   
/*  65:    */   private static String writePublicKeyAsPem(PublicKey publicKey)
/*  66:    */   {
/*  67:141 */     String base64Key = Base64Utils.base64Encode(publicKey.getEncoded());
/*  68:142 */     StringBuffer strBuf = new StringBuffer();
/*  69:143 */     strBuf.append("-----BEGIN PUBLIC KEY-----\n");
/*  70:    */     
/*  71:145 */     int index = 0;
/*  72:146 */     while (index < base64Key.length())
/*  73:    */     {
/*  74:147 */       int nextIndex = index + 64;
/*  75:148 */       if (nextIndex > base64Key.length()) {
/*  76:148 */         nextIndex = base64Key.length();
/*  77:    */       }
/*  78:149 */       strBuf.append(base64Key.substring(index, nextIndex));
/*  79:150 */       strBuf.append("\n");
/*  80:151 */       index = nextIndex;
/*  81:    */     }
/*  82:154 */     strBuf.append("-----END PUBLIC KEY-----\n");
/*  83:155 */     return strBuf.toString();
/*  84:    */   }
/*  85:    */   
/*  86:    */   public static boolean isEnabled()
/*  87:    */   {
/*  88:216 */     log.info("isEnabled:" + privateKey);
/*  89:217 */     return privateKey != null;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public static void disable()
/*  93:    */   {
/*  94:221 */     privateKey = null;
/*  95:    */   }
/*  96:    */   
/*  97:    */   private static void setPrivateKey(PrivateKey key)
/*  98:    */   {
/*  99:225 */     privateKey = key;
/* 100:226 */     if (!CryptoApplet.testMode) {
/* 101:226 */       CryptoApplet.applet.uiHandler.updateImage();
/* 102:    */     }
/* 103:    */   }
/* 104:    */   
/* 105:    */   public static PrivateKey getPrivateKey()
/* 106:    */   {
/* 107:230 */     return privateKey;
/* 108:    */   }
/* 109:    */   
/* 110:    */   private static String getDefaultKeyStoreDirectory()
/* 111:    */     throws IOException
/* 112:    */   {
/* 113:234 */     return OSHandler.getYdfDirectory().getAbsolutePath();
/* 114:    */   }
/* 115:    */   
/* 116:    */   private static String getDefaultKeyStoreFilename()
/* 117:    */     throws IOException
/* 118:    */   {
/* 119:238 */     return OSHandler.getLocalFilePrefix() + "ydf.pfx";
/* 120:    */   }
/* 121:    */   
/* 122:    */   private static String getDefaultKeyStorePath()
/* 123:    */     throws IOException
/* 124:    */   {
/* 125:242 */     return getDefaultKeyStoreDirectory() + File.separator + getDefaultKeyStoreFilename();
/* 126:    */   }
/* 127:    */   
/* 128:    */   public static String getKeyStorePath()
/* 129:    */   {
/* 130:    */     try
/* 131:    */     {
/* 132:247 */       keyStorePath = keyStorePath == null ? OSHandler.getPathCache() : keyStorePath;
/* 133:    */     }
/* 134:    */     catch (IOException e1) {}
/* 135:249 */     if (keyStorePath == null) {
/* 136:    */       try
/* 137:    */       {
/* 138:252 */         File file = new File(getDefaultKeyStorePath());
/* 139:253 */         setKeyStorePath(file.getAbsolutePath());
/* 140:    */       }
/* 141:    */       catch (IOException e)
/* 142:    */       {
/* 143:255 */         log.log(Level.SEVERE, e.getLocalizedMessage(), e);
/* 144:    */       }
/* 145:    */     }
/* 146:258 */     return keyStorePath;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public static void setKeyStorePath(String path)
/* 150:    */   {
/* 151:262 */     keyStorePath = path;
/* 152:    */     try
/* 153:    */     {
/* 154:264 */       OSHandler.setPathCache(keyStorePath);
/* 155:    */     }
/* 156:    */     catch (IOException e) {}
/* 157:    */   }
/* 158:    */   
/* 159:    */   public static KeyStore createKeyStore(char[] pwd)
/* 160:    */     throws GeneralSecurityException, IOException
/* 161:    */   {
/* 162:276 */     KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
/* 163:277 */     SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
/* 164:    */     
/* 165:279 */     random.nextBytes(new byte[1]);
/* 166:280 */     keyGen.initialize(2048, random);
/* 167:281 */     KeyPair pair = keyGen.generateKeyPair();
/* 168:282 */     setPrivateKey(pair.getPrivate());
/* 169:283 */     PublicKey publicKey = pair.getPublic();
/* 170:    */     
/* 171:    */ 
/* 172:    */ 
/* 173:287 */     X509Certificate cert = generateSignedCertificate(pair);
/* 174:288 */     Certificate[] certChain = new Certificate[1];
/* 175:289 */     certChain[0] = cert;
/* 176:    */     
/* 177:291 */     KeyStore keyStore = KeyStore.getInstance("PKCS12");
/* 178:292 */     keyStore.load(null);
/* 179:293 */     keyStore.setKeyEntry("YOUR DIGITAL FILE Private Key", privateKey, pwd, certChain);
/* 180:    */     
/* 181:295 */     File keystoreLocation = new File(getDefaultKeyStorePath());
/* 182:296 */     if (keystoreLocation.exists())
/* 183:    */     {
/* 184:297 */       JFileChooser fileChooser = new SaveKeystoreFileChooser();
/* 185:298 */       if (!CryptoApplet.testMode) {
/* 186:298 */         CryptoApplet.applet.setCurrentChooser(fileChooser);
/* 187:    */       }
/* 188:299 */       fileChooser.setSelectedFile(keystoreLocation);
/* 189:300 */       keystoreLocation = null;
/* 190:301 */       if (fileChooser.showSaveDialog(CryptoApplet.applet) == 0) {
/* 191:302 */         keystoreLocation = fileChooser.getSelectedFile();
/* 192:    */       }
/* 193:304 */       if (!CryptoApplet.testMode) {
/* 194:304 */         CryptoApplet.applet.setCurrentChooser(null);
/* 195:    */       }
/* 196:    */     }
/* 197:306 */     if (keystoreLocation == null) {
/* 198:306 */       return null;
/* 199:    */     }
/* 200:309 */     setKeyStorePath(keystoreLocation.getAbsolutePath());
/* 201:310 */     log.info("saving to keyStorePath -->" + keyStorePath);
/* 202:311 */     FileOutputStream fos = new FileOutputStream(keyStorePath);
/* 203:312 */     keyStore.store(fos, pwd);
/* 204:313 */     fos.close();
/* 205:316 */     if (!CryptoApplet.testMode)
/* 206:    */     {
/* 207:317 */       BrowserHandler.setField("public_key", writePublicKeyAsPem(publicKey));
/* 208:318 */       BrowserHandler.setField("password_hash", LockManager.hashPasswordForWebsiteLogin(pwd));
/* 209:    */     }
/* 210:    */     else
/* 211:    */     {
/* 212:320 */       log.info(writePublicKeyAsPem(publicKey));
/* 213:321 */       log.info(LockManager.hashPasswordForWebsiteLogin(pwd));
/* 214:    */     }
/* 215:327 */     return keyStore;
/* 216:    */   }
/* 217:    */   
/* 218:    */   public static KeyStore loadKeystore(char[] password)
/* 219:    */     throws GeneralSecurityException, IOException
/* 220:    */   {
/* 221:335 */     String keyStorePath = getKeyStorePath();
/* 222:336 */     log.finest("CryptoHandler.loadKeyStore keyStorePath: " + keyStorePath);
/* 223:337 */     KeyStore keystore = KeyStore.getInstance("PKCS12");
/* 224:338 */     FileInputStream keystoreStream = new FileInputStream(keyStorePath);
/* 225:339 */     keystore.load(keystoreStream, password);
/* 226:340 */     setPrivateKey(keystore, password);
/* 227:    */     try
/* 228:    */     {
/* 229:342 */       OSHandler.setPathCache(keyStorePath);
/* 230:343 */       CryptoApplet.applet.lockManager.storeSecret(password);
/* 231:344 */       BrowserHandler.keystoreEnabled();
/* 232:    */     }
/* 233:    */     catch (Exception e)
/* 234:    */     {
/* 235:346 */       log.log(Level.SEVERE, "OSHandler error saving persistent data: " + e.getLocalizedMessage(), e);
/* 236:    */     }
/* 237:348 */     return keystore;
/* 238:    */   }
/* 239:    */   
/* 240:    */   public static String decryptKeySplits(String base64Splits)
/* 241:    */     throws GeneralSecurityException, IOException
/* 242:    */   {
/* 243:352 */     return decryptKeySplits(base64Splits, false);
/* 244:    */   }
/* 245:    */   
/* 246:    */   public static String decryptKeySplits(String base64Splits, boolean testMode)
/* 247:    */     throws GeneralSecurityException, IOException
/* 248:    */   {
/* 249:356 */     StringBuilder sb = new StringBuilder();
/* 250:    */     
/* 251:358 */     String[] splits = base64Splits.split("\\|");
/* 252:359 */     log.finest(base64Splits);
/* 253:360 */     log.finest(Arrays.toString(splits));
/* 254:361 */     log.finest("Splits=" + splits.length);
/* 255:    */     
/* 256:363 */     String sep = "";
/* 257:364 */     boolean first = true;
/* 258:365 */     for (String tuple : splits) {
/* 259:366 */       if ((tuple != null) && (tuple.split(":").length == 2))
/* 260:    */       {
/* 261:367 */         String k = tuple.split(":")[1];
/* 262:368 */         String id = tuple.split(":")[0];
/* 263:369 */         log.finest("Doing " + tuple);
/* 264:370 */         byte[] encBytes = Base64Utils.base64Decode(k);
/* 265:371 */         log.finest(Util.newString(encBytes));
/* 266:372 */         Cipher c = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
/* 267:374 */         if (testMode)
/* 268:    */         {
/* 269:375 */           char[] password = CryptoApplet.applet.lockManager.loadPassword();
/* 270:376 */           KeyStore ks = loadKeystore(password);
/* 271:377 */           Util.blankOutCharArray(password);
/* 272:378 */           Certificate cert = getPublicKey(ks);
/* 273:379 */           PublicKey publicKey = cert.getPublicKey();
/* 274:380 */           c.init(1, publicKey);
/* 275:381 */           encBytes = c.doFinal(Util.getBytes(k));
/* 276:382 */           log.info("Test mode encrypting:" + Util.newString(encBytes));
/* 277:    */         }
/* 278:384 */         c.init(2, privateKey);
/* 279:385 */         byte[] decBytes = c.doFinal(encBytes);
/* 280:386 */         log.finest("Decrypted:" + Util.newString(decBytes));
/* 281:387 */         if (first) {
/* 282:388 */           first = false;
/* 283:    */         } else {
/* 284:390 */           sb.append(sep);
/* 285:    */         }
/* 286:392 */         sb.append(id);
/* 287:393 */         sb.append(":");
/* 288:394 */         sb.append(Base64Utils.base64Encode(decBytes));
/* 289:395 */         sep = "|";
/* 290:    */       }
/* 291:    */     }
/* 292:397 */     log.finest("Done\n");
/* 293:398 */     return sb.toString();
/* 294:    */   }
/* 295:    */   
/* 296:    */   private static PrivateKey setPrivateKey(KeyStore ks, char[] keyPassword)
/* 297:    */     throws GeneralSecurityException
/* 298:    */   {
/* 299:408 */     log.info("setPrivateKey(KeyStore ks, char[] keyPassword)");
/* 300:409 */     Enumeration<String> aliasesEnum = ks.aliases();
/* 301:410 */     if (aliasesEnum.hasMoreElements())
/* 302:    */     {
/* 303:411 */       String alias = (String)aliasesEnum.nextElement();
/* 304:412 */       setPrivateKey((PrivateKey)ks.getKey(alias, keyPassword));
/* 305:413 */       return privateKey;
/* 306:    */     }
/* 307:415 */     return null;
/* 308:    */   }
/* 309:    */   
/* 310:    */   private static Certificate getPublicKey(KeyStore ks)
/* 311:    */     throws GeneralSecurityException
/* 312:    */   {
/* 313:422 */     Enumeration<String> aliasesEnum = ks.aliases();
/* 314:423 */     if (aliasesEnum.hasMoreElements())
/* 315:    */     {
/* 316:424 */       String alias = (String)aliasesEnum.nextElement();
/* 317:425 */       Certificate publicKey = ks.getCertificate(alias);
/* 318:426 */       return publicKey;
/* 319:    */     }
/* 320:428 */     return null;
/* 321:    */   }
/* 322:    */   
/* 323:    */   private static X509Certificate generateSignedCertificate(KeyPair pair)
/* 324:    */     throws GeneralSecurityException, IOException
/* 325:    */   {
/* 326:440 */     PrivateKey privkey = pair.getPrivate();
/* 327:441 */     X509CertInfo info = new X509CertInfo();
/* 328:442 */     Date from = new Date();
/* 329:443 */     Date to = new Date(from.getTime() + 220924800000L);
/* 330:444 */     CertificateValidity interval = new CertificateValidity(from, to);
/* 331:445 */     BigInteger sn = new BigInteger(64, new SecureRandom());
/* 332:446 */     X500Name owner = new X500Name("CN=Your Digital File, C=AU");
/* 333:    */     
/* 334:448 */     info.set("validity", interval);
/* 335:449 */     info.set("serialNumber", new CertificateSerialNumber(sn));
/* 336:450 */     info.set("subject", CryptoApplet.JAVA_VERSION >= 1.8D ? owner : new CertificateSubjectName(owner));
/* 337:451 */     info.set("issuer", CryptoApplet.JAVA_VERSION >= 1.8D ? owner : new CertificateIssuerName(owner));
/* 338:452 */     info.set("key", new CertificateX509Key(pair.getPublic()));
/* 339:453 */     info.set("version", new CertificateVersion(2));
/* 340:454 */     AlgorithmId algo = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);
/* 341:455 */     info.set("algorithmID", new CertificateAlgorithmId(algo));
/* 342:    */     
/* 343:    */ 
/* 344:458 */     X509CertImpl cert = new X509CertImpl(info);
/* 345:459 */     cert.sign(privkey, "SHA1withRSA");
/* 346:    */     
/* 347:    */ 
/* 348:462 */     algo = (AlgorithmId)cert.get("x509.algorithm");
/* 349:463 */     info.set("algorithmID.algorithm", algo);
/* 350:464 */     cert = new X509CertImpl(info);
/* 351:465 */     cert.sign(privkey, "SHA1withRSA");
/* 352:466 */     return cert;
/* 353:    */   }
/* 354:    */   
/* 355:    */   public static void doUpload(File f, String csrfMiddlewareToken, Map<String, String> parameters)
/* 356:    */     throws Exception
/* 357:    */   {
/* 358:470 */     log.info("CryptoHandler: Calling file upload");
/* 359:471 */     byte[] hash = SignatureHandler.getHash(f);
/* 360:472 */     String timestamp = SignatureHandler.getTimestamp();
/* 361:473 */     byte[] signature = SignatureHandler.sign(SignatureHandler.getSignatureInput(hash, timestamp), privateKey);
/* 362:474 */     log.finest("mode upload file=" + f.getName() + " Hash=" + hash + " Timestamp" + timestamp + " Sig=" + signature);
/* 363:    */     
/* 364:476 */     parameters.put("signature", Base64Utils.base64Encode(signature));
/* 365:477 */     parameters.put("timestamp", timestamp);
/* 366:478 */     parameters.put("hash", Base64Utils.base64Encode(hash));
/* 367:479 */     new UploadHandler(f, csrfMiddlewareToken, parameters).doUpload();
/* 368:    */   }
/* 369:    */ }


/* Location:           F:\Program Files\JDGUI\YDF\CryptoApplet-20140519102015.jar
 * Qualified Name:     au.com.yourdigitalfile.crypto_applet.crypto.CryptoHandler
 * JD-Core Version:    0.7.0.1
 */