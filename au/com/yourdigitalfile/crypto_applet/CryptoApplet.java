/*   1:    */ package au.com.yourdigitalfile.crypto_applet;
/*   2:    */ 
/*   3:    */ import au.com.yourdigitalfile.crypto_applet.browser.BrowserHandler;
/*   4:    */ import au.com.yourdigitalfile.crypto_applet.crypto.CryptoHandler;
/*   5:    */ import au.com.yourdigitalfile.crypto_applet.crypto.LockManager;
/*   6:    */ import au.com.yourdigitalfile.crypto_applet.crypto.SignatureHandler;
/*   7:    */ import au.com.yourdigitalfile.crypto_applet.os.OSHandler;
/*   8:    */ import au.com.yourdigitalfile.crypto_applet.os.SaveKeystoreFileChooser;
/*   9:    */ import au.com.yourdigitalfile.crypto_applet.ui.UIHandler;
/*  10:    */ import au.com.yourdigitalfile.crypto_applet.util.Base64Utils;
/*  11:    */ import au.com.yourdigitalfile.crypto_applet.util.Util;
/*  12:    */ import java.awt.Color;
/*  13:    */ import java.io.File;
/*  14:    */ import java.io.IOException;
/*  15:    */ import java.security.AccessController;
/*  16:    */ import java.security.GeneralSecurityException;
/*  17:    */ import java.security.KeyStore;
/*  18:    */ import java.security.PrivilegedAction;
/*  19:    */ import java.util.Date;
/*  20:    */ import java.util.HashMap;
/*  21:    */ import java.util.Map;
/*  22:    */ import java.util.logging.Level;
/*  23:    */ import java.util.logging.Logger;
/*  24:    */ import javax.swing.JApplet;
/*  25:    */ import javax.swing.JFileChooser;
/*  26:    */ import javax.swing.filechooser.FileFilter;
/*  27:    */ import javax.swing.filechooser.FileNameExtensionFilter;
/*  28:    */ 
/*  29:    */ public class CryptoApplet
/*  30:    */   extends JApplet
/*  31:    */ {
/*  32:    */   private static final long serialVersionUID = -6138746609593745897L;
/*  33: 42 */   private static final Logger log = Logger.getLogger(CryptoApplet.class.getName());
/*  34:    */   public static final String VERSION = "0.1.35";
/*  35: 45 */   public static final double JAVA_VERSION = getVersion();
/*  36: 47 */   public static final Color BG_COLOR = new Color(226, 235, 241);
/*  37: 49 */   public static boolean testMode = false;
/*  38:    */   private static final String SESSION_KEY_PARAM = "sessionKey";
/*  39:    */   private static String sessionKey;
/*  40:    */   private static final String HOSTNAME_PARAM = "hostname";
/*  41:    */   private static String hostname;
/*  42:    */   public OSHandler osHandler;
/*  43:    */   public UIHandler uiHandler;
/*  44:    */   public LockManager lockManager;
/*  45:    */   public SignatureHandler signatureHandler;
/*  46:    */   public CryptoHandler cryptoHandler;
/*  47:    */   public static CryptoApplet applet;
/*  48: 63 */   public boolean started = false;
/*  49: 64 */   private Date startDate = new Date();
/*  50:    */   
/*  51:    */   private static double getVersion()
/*  52:    */   {
/*  53: 67 */     String version = System.getProperty("java.version");
/*  54: 68 */     int pos = 0;int count = 0;
/*  55: 69 */     for (; (pos < version.length()) && (count < 2); pos++) {
/*  56: 70 */       if (version.charAt(pos) == '.') {
/*  57: 70 */         count++;
/*  58:    */       }
/*  59:    */     }
/*  60: 72 */     return Double.parseDouble(version.substring(0, pos - 1));
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void init()
/*  64:    */   {
/*  65: 76 */     log.info("APPLET INIT 0.1.35 .........");
/*  66: 77 */     log.info("Java Version: " + JAVA_VERSION);
/*  67: 78 */     applet = this;
/*  68: 79 */     sessionKey = getParameter("sessionKey");
/*  69: 80 */     hostname = getParameter("hostname");
/*  70:    */     
/*  71: 82 */     assert (sessionKey != null);
/*  72: 83 */     assert (hostname != null);
/*  73: 84 */     log.info("sessionKey=" + sessionKey + " hostname=" + hostname);
/*  74: 87 */     if (this.osHandler == null) {
/*  75: 88 */       this.osHandler = new OSHandler();
/*  76:    */     }
/*  77: 89 */     if (this.uiHandler == null) {
/*  78: 90 */       this.uiHandler = new UIHandler();
/*  79:    */     }
/*  80: 91 */     if (this.cryptoHandler == null) {
/*  81: 92 */       this.cryptoHandler = new CryptoHandler();
/*  82:    */     }
/*  83: 93 */     if (this.signatureHandler == null) {
/*  84: 94 */       this.signatureHandler = new SignatureHandler();
/*  85:    */     }
/*  86: 96 */     this.lockManager = new LockManager(sessionKey);
/*  87: 97 */     this.lockManager.enableWithCachedCredentials();
/*  88:    */     
/*  89: 99 */     log.info("APPLET IS INITIALIZED");
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void start()
/*  93:    */   {
/*  94:106 */     this.started = true;
/*  95:107 */     BrowserHandler.started();
/*  96:108 */     log.info("startUp: " + (System.currentTimeMillis() - this.startDate.getTime()));
/*  97:    */   }
/*  98:    */   
/*  99:    */   public static String getSessionKey()
/* 100:    */   {
/* 101:112 */     return sessionKey != null ? sessionKey : "";
/* 102:    */   }
/* 103:    */   
/* 104:    */   public static String getHostname()
/* 105:    */   {
/* 106:116 */     return hostname != null ? hostname : "";
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void stop()
/* 110:    */   {
/* 111:120 */     log.info("CRYPTO_APPLET STOP");
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void destroy()
/* 115:    */   {
/* 116:124 */     log.info("CRYPTO_APPLET DESTROY");
/* 117:    */     try
/* 118:    */     {
/* 119:126 */       this.currentChooser.cancelSelection();
/* 120:    */     }
/* 121:    */     catch (Exception e) {}
/* 122:128 */     this.osHandler = null;
/* 123:129 */     this.uiHandler = null;
/* 124:130 */     this.lockManager = null;
/* 125:131 */     this.signatureHandler = null;
/* 126:132 */     this.cryptoHandler = null;
/* 127:133 */     applet = null;
/* 128:    */   }
/* 129:    */   
/* 130:    */   public String getKeyStorePath()
/* 131:    */   {
/* 132:137 */     log.info("getKeyStorePath");
/* 133:138 */     (String)AccessController.doPrivileged(new PrivilegedAction()
/* 134:    */     {
/* 135:    */       public String run()
/* 136:    */       {
/* 137:    */         try
/* 138:    */         {
/* 139:142 */           return OSHandler.getPathCache();
/* 140:    */         }
/* 141:    */         catch (Exception e) {}
/* 142:144 */         return "";
/* 143:    */       }
/* 144:    */     });
/* 145:    */   }
/* 146:    */   
/* 147:    */   public void keyStoreBrowse()
/* 148:    */   {
/* 149:151 */     browseInNewThread(BrowseType.KEYSTORE);
/* 150:    */   }
/* 151:    */   
/* 152:    */   private void runKeyStoreBrowse()
/* 153:    */   {
/* 154:155 */     log.info("runKeyStoreBrowse");
/* 155:156 */     AccessController.doPrivileged(new PrivilegedAction()
/* 156:    */     {
/* 157:    */       public JFileChooser run()
/* 158:    */       {
/* 159:159 */         JFileChooser fc = new JFileChooser();
/* 160:160 */         CryptoApplet.this.setCurrentChooser(fc);
/* 161:161 */         FileFilter filter = new FileNameExtensionFilter("Your Digital File Private Key (.pfx)", new String[] { "pfx" });
/* 162:    */         
/* 163:163 */         fc.addChoosableFileFilter(filter);
/* 164:164 */         fc.setFileFilter(filter);
/* 165:    */         
/* 166:166 */         File dir = null;
/* 167:    */         try
/* 168:    */         {
/* 169:168 */           dir = OSHandler.getYdfDirectory();
/* 170:    */         }
/* 171:    */         catch (IOException e)
/* 172:    */         {
/* 173:170 */           CryptoApplet.log.severe("Error getting YDF directory: " + e.getMessage());
/* 174:    */         }
/* 175:172 */         if (dir != null) {
/* 176:173 */           fc.setCurrentDirectory(dir);
/* 177:    */         }
/* 178:175 */         if (fc.showOpenDialog(CryptoApplet.applet) == 0)
/* 179:    */         {
/* 180:176 */           String selectedFile = fc.getSelectedFile().getAbsolutePath();
/* 181:177 */           BrowserHandler.setField("keystorePath", selectedFile);
/* 182:    */         }
/* 183:179 */         CryptoApplet.this.setCurrentChooser(null);
/* 184:180 */         return null;
/* 185:    */       }
/* 186:    */     });
/* 187:    */   }
/* 188:    */   
/* 189:    */   public void exportKeystore()
/* 190:    */   {
/* 191:186 */     browseInNewThread(BrowseType.EXPORT);
/* 192:    */   }
/* 193:    */   
/* 194:    */   private void runExportKeystore()
/* 195:    */   {
/* 196:190 */     log.info("runExportKeystore");
/* 197:    */     try
/* 198:    */     {
/* 199:192 */       Exception ex = (Exception)AccessController.doPrivileged(new PrivilegedAction()
/* 200:    */       {
/* 201:    */         public Exception run()
/* 202:    */         {
/* 203:    */           try
/* 204:    */           {
/* 205:196 */             File keystore = new File(CryptoHandler.getKeyStorePath());
/* 206:198 */             if ((keystore.exists()) && (keystore.isFile()))
/* 207:    */             {
/* 208:199 */               JFileChooser fileChooser = new SaveKeystoreFileChooser();
/* 209:200 */               CryptoApplet.this.setCurrentChooser(fileChooser);
/* 210:    */               
/* 211:202 */               fileChooser.setFileHidingEnabled(true);
/* 212:    */               
/* 213:204 */               fileChooser.setSelectedFile(new File(keystore.getName()));
/* 214:205 */               if (fileChooser.showSaveDialog(CryptoApplet.applet) == 0) {
/* 215:206 */                 Util.copyFile(keystore, fileChooser.getSelectedFile());
/* 216:    */               }
/* 217:208 */               CryptoApplet.this.setCurrentChooser(null);
/* 218:    */             }
/* 219:    */           }
/* 220:    */           catch (IOException e)
/* 221:    */           {
/* 222:211 */             return e;
/* 223:    */           }
/* 224:213 */           return null;
/* 225:    */         }
/* 226:    */       });
/* 227:216 */       if (ex != null) {
/* 228:217 */         throw ex;
/* 229:    */       }
/* 230:    */     }
/* 231:    */     catch (Exception e)
/* 232:    */     {
/* 233:220 */       log.log(Level.SEVERE, e.getLocalizedMessage(), e);
/* 234:221 */       BrowserHandler.error(e.getLocalizedMessage());
/* 235:    */     }
/* 236:    */   }
/* 237:    */   
/* 238:    */   public void keyStoreEnable(final String path, String pass)
/* 239:    */   {
/* 240:226 */     log.info("keyStoreEnable");
/* 241:227 */     String error = "Unable to enable your Cryptoloc Manager. If the problem persists please contact support";
/* 242:    */     
/* 243:229 */     final char[] password = pass.toCharArray();
/* 244:    */     try
/* 245:    */     {
/* 246:231 */       Exception ex = (Exception)AccessController.doPrivileged(new PrivilegedAction()
/* 247:    */       {
/* 248:    */         public Exception run()
/* 249:    */         {
/* 250:    */           try
/* 251:    */           {
/* 252:235 */             CryptoHandler.setKeyStorePath(path);
/* 253:236 */             CryptoHandler.loadKeystore(password);
/* 254:237 */             Util.blankOutCharArray(password);
/* 255:    */           }
/* 256:    */           catch (GeneralSecurityException e)
/* 257:    */           {
/* 258:239 */             return e;
/* 259:    */           }
/* 260:    */           catch (IOException e)
/* 261:    */           {
/* 262:241 */             return e;
/* 263:    */           }
/* 264:243 */           return null;
/* 265:    */         }
/* 266:    */       });
/* 267:246 */       if (ex != null) {
/* 268:247 */         throw ex;
/* 269:    */       }
/* 270:249 */       BrowserHandler.setField("keystorePassword", "");
/* 271:    */     }
/* 272:    */     catch (Exception e)
/* 273:    */     {
/* 274:251 */       log.log(Level.WARNING, e.getMessage(), e);
/* 275:252 */       error = "Your password is not correct, or perhaps the selected file is not your Private Key";
/* 276:    */     }
/* 277:255 */     if (!CryptoHandler.isEnabled()) {
/* 278:256 */       BrowserHandler.error(error);
/* 279:    */     }
/* 280:    */   }
/* 281:    */   
/* 282:261 */   private boolean browseLock = false;
/* 283:262 */   private JFileChooser currentChooser = null;
/* 284:263 */   private Object BROWSE_LOCK_MUTEX = new Object();
/* 285:    */   
/* 286:    */   private boolean getBrowseLock()
/* 287:    */   {
/* 288:266 */     synchronized (this.BROWSE_LOCK_MUTEX)
/* 289:    */     {
/* 290:267 */       if (this.browseLock) {
/* 291:268 */         return false;
/* 292:    */       }
/* 293:270 */       this.browseLock = true;
/* 294:271 */       return true;
/* 295:    */     }
/* 296:    */   }
/* 297:    */   
/* 298:    */   private void releaseBrowseLock()
/* 299:    */   {
/* 300:276 */     synchronized (this.BROWSE_LOCK_MUTEX)
/* 301:    */     {
/* 302:277 */       this.browseLock = false;
/* 303:278 */       this.currentChooser = null;
/* 304:    */     }
/* 305:    */   }
/* 306:    */   
/* 307:    */   public void setCurrentChooser(JFileChooser currentChooser)
/* 308:    */   {
/* 309:283 */     synchronized (this.BROWSE_LOCK_MUTEX)
/* 310:    */     {
/* 311:284 */       this.currentChooser = currentChooser;
/* 312:    */     }
/* 313:    */   }
/* 314:    */   
/* 315:    */   private void runUploadBrowse()
/* 316:    */   {
/* 317:289 */     log.info("runUploadBrowse");
/* 318:290 */     BrowserHandler.setField("uploadPath", "");
/* 319:291 */     AccessController.doPrivileged(new PrivilegedAction()
/* 320:    */     {
/* 321:    */       public JFileChooser run()
/* 322:    */       {
/* 323:294 */         JFileChooser fc = new JFileChooser();
/* 324:295 */         CryptoApplet.this.setCurrentChooser(fc);
/* 325:296 */         CryptoApplet.this.currentChooser = fc;
/* 326:297 */         if (fc.showOpenDialog(CryptoApplet.applet) == 0)
/* 327:    */         {
/* 328:298 */           File f = fc.getSelectedFile();
/* 329:299 */           BrowserHandler.setField("uploadPath", f.getAbsolutePath());
/* 330:    */         }
/* 331:301 */         CryptoApplet.this.setCurrentChooser(null);
/* 332:302 */         return null;
/* 333:    */       }
/* 334:    */     });
/* 335:    */   }
/* 336:    */   
/* 337:    */   private static enum BrowseType
/* 338:    */   {
/* 339:309 */     UPLOAD,  KEYSTORE,  EXPORT,  CREATE;
/* 340:    */     
/* 341:    */     private BrowseType() {}
/* 342:    */   }
/* 343:    */   
/* 344:    */   public void browseInNewThread(final BrowseType browseType)
/* 345:    */   {
/* 346:313 */     if (getBrowseLock())
/* 347:    */     {
/* 348:314 */       log.info("got lock");
/* 349:315 */       new Thread(new Runnable()
/* 350:    */       {
/* 351:    */         public void run()
/* 352:    */         {
/* 353:    */           try
/* 354:    */           {
/* 355:318 */             switch (CryptoApplet.10.$SwitchMap$au$com$yourdigitalfile$crypto_applet$CryptoApplet$BrowseType[browseType.ordinal()])
/* 356:    */             {
/* 357:    */             case 1: 
/* 358:320 */               CryptoApplet.this.runUploadBrowse();
/* 359:321 */               break;
/* 360:    */             case 2: 
/* 361:323 */               CryptoApplet.this.runKeyStoreBrowse();
/* 362:324 */               break;
/* 363:    */             case 3: 
/* 364:326 */               CryptoApplet.this.runCreateKeystore();
/* 365:327 */               break;
/* 366:    */             case 4: 
/* 367:329 */               CryptoApplet.this.runExportKeystore();
/* 368:    */             }
/* 369:    */           }
/* 370:    */           finally
/* 371:    */           {
/* 372:335 */             CryptoApplet.this.releaseBrowseLock();
/* 373:    */           }
/* 374:    */         }
/* 375:    */       }).start();
/* 376:    */     }
/* 377:    */     else
/* 378:    */     {
/* 379:340 */       log.info("no lock");
/* 380:    */     }
/* 381:    */   }
/* 382:    */   
/* 383:    */   public void uploadBrowse()
/* 384:    */   {
/* 385:355 */     browseInNewThread(BrowseType.UPLOAD);
/* 386:    */   }
/* 387:    */   
/* 388:358 */   private boolean uploadLock = false;
/* 389:359 */   private Object UPLOAD_LOCK_MUTEX = new Object();
/* 390:    */   
/* 391:    */   private boolean getUploadLock()
/* 392:    */   {
/* 393:362 */     synchronized (this.UPLOAD_LOCK_MUTEX)
/* 394:    */     {
/* 395:363 */       if (this.uploadLock) {
/* 396:364 */         return false;
/* 397:    */       }
/* 398:366 */       this.uploadLock = true;
/* 399:367 */       return true;
/* 400:    */     }
/* 401:    */   }
/* 402:    */   
/* 403:    */   private void releaseUploadLock()
/* 404:    */   {
/* 405:372 */     synchronized (this.UPLOAD_LOCK_MUTEX)
/* 406:    */     {
/* 407:373 */       this.uploadLock = false;
/* 408:    */     }
/* 409:    */   }
/* 410:    */   
/* 411:    */   public void upload(final String csrfMiddlewareToken, final String uploadPath, final String name, final String notes, final String fileproxyId)
/* 412:    */   {
/* 413:379 */     if (getUploadLock())
/* 414:    */     {
/* 415:380 */       log.info("got lock");
/* 416:381 */       new Thread(new Runnable()
/* 417:    */       {
/* 418:    */         public void run()
/* 419:    */         {
/* 420:    */           try
/* 421:    */           {
/* 422:384 */             CryptoApplet.this.runUpload(csrfMiddlewareToken, uploadPath, name, notes, fileproxyId);
/* 423:    */           }
/* 424:    */           finally
/* 425:    */           {
/* 426:386 */             CryptoApplet.this.releaseUploadLock();
/* 427:    */           }
/* 428:    */         }
/* 429:    */       }).start();
/* 430:    */     }
/* 431:    */     else
/* 432:    */     {
/* 433:391 */       log.info("no lock");
/* 434:392 */       BrowserHandler.error("Upload already in progress");
/* 435:    */     }
/* 436:    */   }
/* 437:    */   
/* 438:    */   private void runUpload(final String csrfMiddlewareToken, String uploadPath, String name, String notes, String fileproxyId)
/* 439:    */   {
/* 440:397 */     log.info("upload");
/* 441:398 */     final File file = new File(uploadPath);
/* 442:399 */     final Map<String, String> parameters = new HashMap();
/* 443:400 */     parameters.put("filename", file.getName());
/* 444:401 */     parameters.put("name", name);
/* 445:402 */     parameters.put("notes", notes);
/* 446:403 */     parameters.put("fileproxy_id", fileproxyId);
/* 447:    */     try
/* 448:    */     {
/* 449:406 */       Exception ex = (Exception)AccessController.doPrivileged(new PrivilegedAction()
/* 450:    */       {
/* 451:    */         public Exception run()
/* 452:    */         {
/* 453:    */           try
/* 454:    */           {
/* 455:410 */             CryptoHandler.doUpload(file, csrfMiddlewareToken, parameters);
/* 456:    */           }
/* 457:    */           catch (Exception e)
/* 458:    */           {
/* 459:412 */             return e;
/* 460:    */           }
/* 461:414 */           return null;
/* 462:    */         }
/* 463:    */       });
/* 464:417 */       if (ex != null) {
/* 465:418 */         throw ex;
/* 466:    */       }
/* 467:420 */       BrowserHandler.uploadComplete();
/* 468:421 */       BrowserHandler.success();
/* 469:    */     }
/* 470:    */     catch (Exception e)
/* 471:    */     {
/* 472:423 */       log.log(Level.SEVERE, e.getLocalizedMessage(), e);
/* 473:424 */       BrowserHandler.uploadComplete();
/* 474:425 */       BrowserHandler.error("Error uploading your file: " + e.getLocalizedMessage());
/* 475:    */     }
/* 476:    */   }
/* 477:    */   
/* 478:    */   public String decryptKeySplits(String base64Splits)
/* 479:    */   {
/* 480:430 */     log.info("decryptKeySplits");
/* 481:    */     try
/* 482:    */     {
/* 483:432 */       return CryptoHandler.decryptKeySplits(base64Splits);
/* 484:    */     }
/* 485:    */     catch (Exception e)
/* 486:    */     {
/* 487:434 */       log.log(Level.SEVERE, e.getLocalizedMessage(), e);
/* 488:435 */       throw new RuntimeException(e);
/* 489:    */     }
/* 490:    */   }
/* 491:    */   
/* 492:    */   public boolean isKeyStoreEnabled()
/* 493:    */   {
/* 494:440 */     return CryptoHandler.isEnabled();
/* 495:    */   }
/* 496:    */   
/* 497:    */   public String sign(String signatureInput)
/* 498:    */   {
/* 499:    */     try
/* 500:    */     {
/* 501:445 */       byte[] signature = SignatureHandler.sign(Util.getBytes(signatureInput), CryptoHandler.getPrivateKey());
/* 502:    */       
/* 503:447 */       return Base64Utils.base64Encode(signature);
/* 504:    */     }
/* 505:    */     catch (Exception e)
/* 506:    */     {
/* 507:449 */       log.log(Level.SEVERE, e.getLocalizedMessage(), e);
/* 508:450 */       BrowserHandler.error(e.getLocalizedMessage());
/* 509:    */     }
/* 510:451 */     return "";
/* 511:    */   }
/* 512:    */   
/* 513:    */   public void keyStoreDisable() {}
/* 514:    */   
/* 515:    */   public void createKeystore()
/* 516:    */   {
/* 517:461 */     browseInNewThread(BrowseType.CREATE);
/* 518:    */   }
/* 519:    */   
/* 520:    */   private void runCreateKeystore()
/* 521:    */   {
/* 522:465 */     log.info("runCreateKeystore");
/* 523:466 */     String passwordField = BrowserHandler.getField("keystorePassword");
/* 524:467 */     String confirmField = BrowserHandler.getField("keystoreConfirmPassword");
/* 525:468 */     if ((passwordField == null) || (!passwordField.equals(confirmField)))
/* 526:    */     {
/* 527:469 */       BrowserHandler.error("Your password and confirm do not match!");
/* 528:470 */       return;
/* 529:    */     }
/* 530:474 */     final char[] password = passwordField.toCharArray();
/* 531:475 */     passwordField = null;
/* 532:476 */     confirmField = null;
/* 533:    */     
/* 534:478 */     String error = Util.checkPasswordAcceptable(password);
/* 535:479 */     if (error.length() == 0) {
/* 536:    */       try
/* 537:    */       {
/* 538:481 */         KeyStore ks = (KeyStore)AccessController.doPrivileged(new PrivilegedAction()
/* 539:    */         {
/* 540:    */           public KeyStore run()
/* 541:    */           {
/* 542:    */             try
/* 543:    */             {
/* 544:485 */               return CryptoHandler.createKeyStore(password);
/* 545:    */             }
/* 546:    */             catch (Exception e)
/* 547:    */             {
/* 548:487 */               throw new RuntimeException(e);
/* 549:    */             }
/* 550:    */           }
/* 551:491 */         });
/* 552:492 */         Util.blankOutCharArray(password);
/* 553:493 */         if (ks == null)
/* 554:    */         {
/* 555:494 */           error = "Keystore not created.";
/* 556:    */         }
/* 557:    */         else
/* 558:    */         {
/* 559:496 */           BrowserHandler.setField("keystorePassword", "");
/* 560:497 */           BrowserHandler.setField("keystoreConfirmPassword", "");
/* 561:498 */           BrowserHandler.setField("private_key_path", CryptoHandler.getKeyStorePath());
/* 562:499 */           BrowserHandler.success();
/* 563:    */         }
/* 564:    */       }
/* 565:    */       catch (Exception e)
/* 566:    */       {
/* 567:504 */         error = "Errors have occurred: " + e.getLocalizedMessage();
/* 568:505 */         log.log(Level.SEVERE, e.getLocalizedMessage(), e);
/* 569:    */       }
/* 570:    */     }
/* 571:508 */     BrowserHandler.error(error);
/* 572:    */   }
/* 573:    */ }


/* Location:           F:\Program Files\JDGUI\YDF\CryptoApplet-20140519102015.jar
 * Qualified Name:     au.com.yourdigitalfile.crypto_applet.CryptoApplet
 * JD-Core Version:    0.7.0.1
 */