/*   1:    */ package au.com.yourdigitalfile.crypto_applet.crypto;
/*   2:    */ 
/*   3:    */ import au.com.yourdigitalfile.crypto_applet.os.OSHandler;
/*   4:    */ import au.com.yourdigitalfile.crypto_applet.util.Base64Utils;
/*   5:    */ import au.com.yourdigitalfile.crypto_applet.util.Util;
/*   6:    */ import java.lang.reflect.Field;
/*   7:    */ import java.security.AlgorithmParameters;
/*   8:    */ import java.security.InvalidAlgorithmParameterException;
/*   9:    */ import java.security.InvalidKeyException;
/*  10:    */ import java.security.NoSuchAlgorithmException;
/*  11:    */ import java.security.SecureRandom;
/*  12:    */ import java.security.spec.InvalidKeySpecException;
/*  13:    */ import java.util.Arrays;
/*  14:    */ import java.util.logging.Level;
/*  15:    */ import java.util.logging.Logger;
/*  16:    */ import javax.crypto.BadPaddingException;
/*  17:    */ import javax.crypto.Cipher;
/*  18:    */ import javax.crypto.IllegalBlockSizeException;
/*  19:    */ import javax.crypto.KeyGenerator;
/*  20:    */ import javax.crypto.NoSuchPaddingException;
/*  21:    */ import javax.crypto.SecretKey;
/*  22:    */ import javax.crypto.SecretKeyFactory;
/*  23:    */ import javax.crypto.spec.IvParameterSpec;
/*  24:    */ import javax.crypto.spec.PBEKeySpec;
/*  25:    */ import javax.crypto.spec.SecretKeySpec;
/*  26:    */ 
/*  27:    */ public class LockManager
/*  28:    */ {
/*  29: 30 */   private static final Logger log = Logger.getLogger(LockManager.class.getName());
/*  30:    */   private static final int AES_UNLIMITED_KEY_SIZE = 256;
/*  31:    */   private static final int AES_SALT_SIZE = 32;
/*  32:    */   private static final int AES_KEY_ITERATION_COUNT = 65536;
/*  33:    */   private static final String SECRET_KEY_ALGO = "PBKDF2WithHmacSHA1";
/*  34:    */   private static final int SHA1_KEY_SIZE = 160;
/*  35:    */   private static final int SHA1_SALT_SIZE = 16;
/*  36:    */   private static final int SHA1_KEY_ITERATION_COUNT = 10000;
/*  37:    */   private static final String DJANGO_PASSWORD_ALGO = "pbkdf2_sha1";
/*  38:    */   private static final String AES_CIPHER_TYPE = "AES";
/*  39:    */   private static final String AES_CIPHER_ALGO = "AES/CBC/PKCS5Padding";
/*  40:    */   private SecretKey aesSecretKey;
/*  41: 44 */   private static int aesKeySize = 128;
/*  42:    */   private IvParameterSpec initParamSpec;
/*  43:    */   
/*  44:    */   public LockManager(String sessionKey)
/*  45:    */   {
/*  46: 50 */     log.finest("LockManager.constructor. Session key =" + sessionKey);
/*  47: 51 */     enableStrongEncryption();
/*  48:    */     try
/*  49:    */     {
/*  50: 53 */       byte[] salt = OSHandler.getSalt();
/*  51: 54 */       log.finest("Salt=" + Util.newString(salt));
/*  52: 56 */       if (salt.length == 0)
/*  53:    */       {
/*  54: 57 */         salt = getSalt(32);
/*  55: 58 */         OSHandler.setSalt(salt);
/*  56:    */       }
/*  57: 60 */       if (salt.length == 0)
/*  58:    */       {
/*  59: 61 */         log.severe("ERROR: Unable to obtain salt for LockManager initialisation!!");
/*  60: 62 */         return;
/*  61:    */       }
/*  62: 65 */       log.finest("Setting up AES InitialisationVector. Salt=" + Util.newString(salt));
/*  63: 66 */       byte[] key = getSecretKey(sessionKey.toCharArray(), salt, 65536, aesKeySize);
/*  64: 67 */       log.finest("Raw key length is " + key.length + " -->" + Arrays.toString(key));
/*  65: 68 */       this.aesSecretKey = new SecretKeySpec(key, "AES");
/*  66: 69 */       Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
/*  67: 70 */       c.init(1, this.aesSecretKey);
/*  68:    */       
/*  69: 72 */       byte[] aesInitialisationVector = OSHandler.getIv();
/*  70: 73 */       if (aesInitialisationVector.length == 0)
/*  71:    */       {
/*  72: 74 */         AlgorithmParameters params = c.getParameters();
/*  73: 75 */         aesInitialisationVector = ((IvParameterSpec)params.getParameterSpec(IvParameterSpec.class)).getIV();
/*  74: 76 */         OSHandler.setIv(aesInitialisationVector);
/*  75:    */       }
/*  76: 78 */       this.initParamSpec = new IvParameterSpec(aesInitialisationVector);
/*  77:    */     }
/*  78:    */     catch (Exception e)
/*  79:    */     {
/*  80: 81 */       log.info("LockManager initialisation unsuccessful: " + e.getMessage());
/*  81:    */     }
/*  82:    */   }
/*  83:    */   
/*  84:    */   private static byte[] getSecretKey(char[] plaintext, byte[] salt, int iterations, int keySize)
/*  85:    */     throws NoSuchAlgorithmException, InvalidKeySpecException
/*  86:    */   {
/*  87:124 */     PBEKeySpec spec = new PBEKeySpec(plaintext, salt, iterations, keySize);
/*  88:125 */     SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
/*  89:126 */     return skf.generateSecret(spec).getEncoded();
/*  90:    */   }
/*  91:    */   
/*  92:    */   private static void setAesKeySize()
/*  93:    */   {
/*  94:130 */     aesKeySize = isStrongEncryptionAvailable() ? 256 : aesKeySize;
/*  95:    */   }
/*  96:    */   
/*  97:    */   private static boolean isStrongEncryptionAvailable()
/*  98:    */   {
/*  99:    */     try
/* 100:    */     {
/* 101:135 */       Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
/* 102:136 */       KeyGenerator kge = KeyGenerator.getInstance("AES");
/* 103:137 */       kge.init(256);
/* 104:138 */       c.init(1, kge.generateKey());
/* 105:    */     }
/* 106:    */     catch (Exception e)
/* 107:    */     {
/* 108:140 */       log.info("Strong encryption currently unavailable: " + e.getMessage());
/* 109:141 */       return false;
/* 110:    */     }
/* 111:143 */     log.info("Strong encryption available");
/* 112:144 */     return true;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public static void enableStrongEncryption()
/* 116:    */   {
/* 117:152 */     if (isStrongEncryptionAvailable()) {
/* 118:153 */       return;
/* 119:    */     }
/* 120:155 */     log.finest("Attempting to enable strong encryption");
/* 121:    */     try
/* 122:    */     {
/* 123:    */       Field isRestricted;
/* 124:    */       try
/* 125:    */       {
/* 126:159 */         Class<?> c = Class.forName("javax.crypto.JceSecurity");
/* 127:160 */         isRestricted = c.getDeclaredField("isRestricted");
/* 128:    */       }
/* 129:    */       catch (ClassNotFoundException e)
/* 130:    */       {
/* 131:    */         try
/* 132:    */         {
/* 133:164 */           Class<?> c = Class.forName("javax.crypto.SunJCE_b");
/* 134:165 */           isRestricted = c.getDeclaredField("g");
/* 135:    */         }
/* 136:    */         catch (ClassNotFoundException ne)
/* 137:    */         {
/* 138:167 */           throw ne;
/* 139:    */         }
/* 140:    */       }
/* 141:170 */       isRestricted.setAccessible(true);
/* 142:171 */       isRestricted.set(null, Boolean.valueOf(false));
/* 143:    */     }
/* 144:    */     catch (Exception e)
/* 145:    */     {
/* 146:173 */       log.log(Level.SEVERE, e.getLocalizedMessage(), e);
/* 147:    */     }
/* 148:175 */     setAesKeySize();
/* 149:    */   }
/* 150:    */   
/* 151:    */   private static byte[] getSalt(int keyLength)
/* 152:    */   {
/* 153:    */     try
/* 154:    */     {
/* 155:180 */       SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
/* 156:181 */       byte[] salt = new byte[keyLength];
/* 157:182 */       random.nextBytes(salt);
/* 158:183 */       return salt;
/* 159:    */     }
/* 160:    */     catch (Exception e)
/* 161:    */     {
/* 162:185 */       log.log(Level.SEVERE, e.getLocalizedMessage(), e);
/* 163:    */     }
/* 164:187 */     return new byte[0];
/* 165:    */   }
/* 166:    */   
/* 167:    */   public void enableWithCachedCredentials()
/* 168:    */   {
/* 169:    */     try
/* 170:    */     {
/* 171:192 */       log.finest("LockManager.enableWithCachedCredentials");
/* 172:193 */       byte[] encryptedSecret = OSHandler.getSecret();
/* 173:194 */       byte[] decryptedSecret = decryptAes(encryptedSecret);
/* 174:195 */       char[] secret = Util.getChars(decryptedSecret);
/* 175:196 */       Util.blankOutByteArray(decryptedSecret);
/* 176:197 */       assert (secret != null);
/* 177:198 */       CryptoHandler.loadKeystore(secret);
/* 178:199 */       Util.blankOutCharArray(secret);
/* 179:    */       
/* 180:201 */       log.info("Successfully enabled access with cached credentials");
/* 181:    */     }
/* 182:    */     catch (Exception e)
/* 183:    */     {
/* 184:203 */       log.info("Cached credentials failed: " + e.getLocalizedMessage());
/* 185:    */     }
/* 186:    */   }
/* 187:    */   
/* 188:    */   public char[] loadPassword()
/* 189:    */     throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException
/* 190:    */   {
/* 191:210 */     byte[] encryptedSecret = OSHandler.getSecret();
/* 192:211 */     byte[] decryptedSecret = decryptAes(encryptedSecret);
/* 193:212 */     char[] secret = Util.getChars(decryptedSecret);
/* 194:213 */     Util.blankOutByteArray(decryptedSecret);
/* 195:214 */     return secret;
/* 196:    */   }
/* 197:    */   
/* 198:    */   void storeSecret(char[] secret)
/* 199:    */     throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException
/* 200:    */   {
/* 201:219 */     byte[] encryptedSecret = encryptedAes(secret);
/* 202:220 */     OSHandler.setSecret(encryptedSecret);
/* 203:    */   }
/* 204:    */   
/* 205:    */   public byte[] encryptedAes(char[] plaintext)
/* 206:    */     throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException
/* 207:    */   {
/* 208:226 */     Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
/* 209:227 */     c.init(1, this.aesSecretKey, this.initParamSpec);
/* 210:228 */     byte[] ciphertext = c.doFinal(Util.getBytes(plaintext));
/* 211:229 */     return ciphertext;
/* 212:    */   }
/* 213:    */   
/* 214:    */   public byte[] decryptAes(byte[] ciphertext)
/* 215:    */     throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException
/* 216:    */   {
/* 217:235 */     Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
/* 218:236 */     c.init(2, this.aesSecretKey, this.initParamSpec);
/* 219:237 */     byte[] plaintext = c.doFinal(ciphertext);
/* 220:238 */     return plaintext;
/* 221:    */   }
/* 222:    */   
/* 223:    */   static String hashPasswordForWebsiteLogin(char[] pwd)
/* 224:    */     throws NoSuchAlgorithmException, InvalidKeySpecException
/* 225:    */   {
/* 226:248 */     String salt = Util.createRandomAplhaNum(16);
/* 227:249 */     byte[] key = getSecretKey(pwd, Util.getBytes(salt), 10000, 160);
/* 228:250 */     String keyEncoded = Base64Utils.base64Encode(key);
/* 229:    */     
/* 230:    */ 
/* 231:253 */     return String.format("%s$%s$%s$%s", new Object[] { "pbkdf2_sha1", Integer.valueOf(10000), salt, keyEncoded });
/* 232:    */   }
/* 233:    */ }


/* Location:           F:\Program Files\JDGUI\YDF\CryptoApplet-20140519102015.jar
 * Qualified Name:     au.com.yourdigitalfile.crypto_applet.crypto.LockManager
 * JD-Core Version:    0.7.0.1
 */