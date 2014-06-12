/*  1:   */ package au.com.yourdigitalfile.crypto_applet.crypto;
/*  2:   */ 
/*  3:   */ import au.com.yourdigitalfile.crypto_applet.util.Base64Utils;
/*  4:   */ import au.com.yourdigitalfile.crypto_applet.util.Util;
/*  5:   */ import java.io.File;
/*  6:   */ import java.io.FileInputStream;
/*  7:   */ import java.security.DigestInputStream;
/*  8:   */ import java.security.MessageDigest;
/*  9:   */ import java.security.PrivateKey;
/* 10:   */ import java.security.Signature;
/* 11:   */ import java.text.SimpleDateFormat;
/* 12:   */ import java.util.Date;
/* 13:   */ import java.util.TimeZone;
/* 14:   */ import java.util.logging.Logger;
/* 15:   */ 
/* 16:   */ public class SignatureHandler
/* 17:   */ {
/* 18:25 */   private static final Logger log = Logger.getLogger(SignatureHandler.class.getName());
/* 19:   */   private static final String SIG_INPUT_FORMAT = "%s\n\n%s";
/* 20:   */   private static final String SIGNATURE_ALGORITHM = "SHA512withRSA";
/* 21:   */   private static final String HASH_ALGORITHM = "SHA-512";
/* 22:   */   
/* 23:   */   static byte[] getHash(File file)
/* 24:   */     throws Exception
/* 25:   */   {
/* 26:32 */     MessageDigest md = MessageDigest.getInstance("SHA-512");
/* 27:33 */     FileInputStream fis = null;
/* 28:34 */     DigestInputStream dis = null;
/* 29:   */     try
/* 30:   */     {
/* 31:36 */       log.info("hashing");
/* 32:37 */       fis = new FileInputStream(file);
/* 33:38 */       dis = new DigestInputStream(fis, md);
/* 34:39 */       byte[] buffer = new byte[8192];
/* 35:40 */       while (dis.read(buffer) != -1) {}
/* 36:   */     }
/* 37:   */     finally
/* 38:   */     {
/* 39:42 */       if (dis != null) {
/* 40:42 */         dis.close();
/* 41:   */       }
/* 42:43 */       if (fis != null) {
/* 43:43 */         fis.close();
/* 44:   */       }
/* 45:   */     }
/* 46:45 */     return md.digest();
/* 47:   */   }
/* 48:   */   
/* 49:   */   static String getTimestamp()
/* 50:   */     throws Exception
/* 51:   */   {
/* 52:52 */     SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
/* 53:53 */     sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 54:54 */     return sdf.format(new Date());
/* 55:   */   }
/* 56:   */   
/* 57:   */   static byte[] getSignatureInput(byte[] hash, String generalizedTimestamp)
/* 58:   */     throws Exception
/* 59:   */   {
/* 60:61 */     return Util.getBytes(String.format("%s\n\n%s", new Object[] { generalizedTimestamp, Base64Utils.base64Encode(hash) }));
/* 61:   */   }
/* 62:   */   
/* 63:   */   public static byte[] sign(byte[] timestampedHash, PrivateKey privateKey)
/* 64:   */     throws Exception
/* 65:   */   {
/* 66:65 */     log.finest("timestampedHash: " + Util.newString(timestampedHash));
/* 67:66 */     Signature signature = Signature.getInstance("SHA512withRSA");
/* 68:67 */     signature.initSign(privateKey);
/* 69:   */     
/* 70:69 */     signature.update(timestampedHash);
/* 71:70 */     return signature.sign();
/* 72:   */   }
/* 73:   */ }


/* Location:           F:\Program Files\JDGUI\YDF\CryptoApplet-20140519102015.jar
 * Qualified Name:     au.com.yourdigitalfile.crypto_applet.crypto.SignatureHandler
 * JD-Core Version:    0.7.0.1
 */