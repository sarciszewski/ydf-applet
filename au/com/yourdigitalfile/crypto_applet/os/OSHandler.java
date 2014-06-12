/*   1:    */ package au.com.yourdigitalfile.crypto_applet.os;
/*   2:    */ 
/*   3:    */ import au.com.yourdigitalfile.crypto_applet.CryptoApplet;
/*   4:    */ import au.com.yourdigitalfile.crypto_applet.browser.BrowserHandler;
/*   5:    */ import au.com.yourdigitalfile.crypto_applet.util.Base64Utils;
/*   6:    */ import java.io.File;
/*   7:    */ import java.io.FileInputStream;
/*   8:    */ import java.io.FileOutputStream;
/*   9:    */ import java.io.IOException;
/*  10:    */ import java.util.Properties;
/*  11:    */ import java.util.logging.Level;
/*  12:    */ import java.util.logging.Logger;
/*  13:    */ 
/*  14:    */ public class OSHandler
/*  15:    */ {
/*  16: 18 */   private static final Logger log = Logger.getLogger(OSHandler.class.getName());
/*  17:    */   private static final String YDF_DIRECTORY_LABEL = ".yourdigitalfile";
/*  18: 21 */   public static final String USER_HOME = System.getProperty("user.home");
/*  19:    */   private static final String PROPERTIES_FILENAME = "cryptoloc.properties";
/*  20:    */   public static final String KEYSTORE_PATH_PROPERTY = "path";
/*  21:    */   private static final String SALT_PROPERTY = "salt";
/*  22:    */   private static final String SECRET_PROPERTY = "secret";
/*  23:    */   private static final String IV_PROPERTY = "aesiv";
/*  24:    */   private static File ydfDirectory;
/*  25:    */   
/*  26:    */   private static File setYdfDirectory()
/*  27:    */     throws IOException
/*  28:    */   {
/*  29: 32 */     log.finest("OSHandler.setYdfDirectory");
/*  30: 33 */     ydfDirectory = new File(USER_HOME + "/" + ".yourdigitalfile");
/*  31: 34 */     ydfDirectory.mkdir();
/*  32: 35 */     return ydfDirectory;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public static File getYdfDirectory()
/*  36:    */     throws IOException
/*  37:    */   {
/*  38: 39 */     log.finest("OSHandler.getYdfDirectory");
/*  39: 40 */     return ydfDirectory == null ? setYdfDirectory() : ydfDirectory;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public static String getPathCache()
/*  43:    */     throws IOException
/*  44:    */   {
/*  45: 44 */     log.finest("OSHandler.getPathCache");
/*  46: 45 */     return getProperty("path");
/*  47:    */   }
/*  48:    */   
/*  49:    */   public static void setPathCache(String pathCache)
/*  50:    */     throws IOException
/*  51:    */   {
/*  52: 49 */     log.finest("OSHandler.setPathCache");
/*  53: 50 */     setProperty("path", pathCache);
/*  54:    */   }
/*  55:    */   
/*  56:    */   private static File getPropertiesFile()
/*  57:    */     throws IOException
/*  58:    */   {
/*  59: 57 */     log.finest("OSHandler.getPropertiesFile");
/*  60: 58 */     ydfDirectory = getYdfDirectory();
/*  61: 59 */     String fileName = ydfDirectory + "/" + getLocalFilePrefix() + "cryptoloc.properties";
/*  62: 60 */     File file = new File(fileName);
/*  63: 61 */     if (!file.exists()) {
/*  64: 62 */       file.createNewFile();
/*  65:    */     }
/*  66: 64 */     return file;
/*  67:    */   }
/*  68:    */   
/*  69:    */   private static String getProperty(String key)
/*  70:    */     throws IOException
/*  71:    */   {
/*  72: 71 */     log.finest("OSHandler.getProperty");
/*  73: 72 */     File propsFile = getPropertiesFile();
/*  74: 73 */     FileInputStream fis = new FileInputStream(propsFile);
/*  75: 74 */     Properties props = new Properties();
/*  76: 75 */     props.load(fis);
/*  77: 76 */     String value = props.getProperty(key);
/*  78: 77 */     fis.close();
/*  79: 78 */     log.finest("Key=" + key + " Value=" + value);
/*  80: 79 */     return value;
/*  81:    */   }
/*  82:    */   
/*  83:    */   private static void setProperty(String key, String value)
/*  84:    */     throws IOException
/*  85:    */   {
/*  86: 83 */     log.finest("OSHandler.setProperty. Key=" + key + " Value=" + value);
/*  87: 84 */     File propsFile = getPropertiesFile();
/*  88: 85 */     FileInputStream fis = new FileInputStream(propsFile);
/*  89: 86 */     Properties props = new Properties();
/*  90: 87 */     props.load(fis);
/*  91: 88 */     props.setProperty(key, value);
/*  92: 89 */     FileOutputStream fos = new FileOutputStream(propsFile);
/*  93: 90 */     props.store(fos, "");
/*  94: 91 */     fos.close();
/*  95:    */   }
/*  96:    */   
/*  97:    */   private static byte[] getPropertyBytes(String key)
/*  98:    */     throws IOException
/*  99:    */   {
/* 100: 95 */     String value = getProperty(key);
/* 101: 96 */     return value == null ? new byte[0] : Base64Utils.base64Decode(value);
/* 102:    */   }
/* 103:    */   
/* 104:    */   private static void setPropertyBytes(String key, byte[] value)
/* 105:    */     throws IOException
/* 106:    */   {
/* 107:100 */     setProperty(key, Base64Utils.base64Encode(value));
/* 108:    */   }
/* 109:    */   
/* 110:    */   public static byte[] getSalt()
/* 111:    */   {
/* 112:    */     try
/* 113:    */     {
/* 114:105 */       return getPropertyBytes("salt");
/* 115:    */     }
/* 116:    */     catch (Exception e)
/* 117:    */     {
/* 118:107 */       log.log(Level.WARNING, "Error getting salt: " + e.getLocalizedMessage());
/* 119:    */     }
/* 120:109 */     return new byte[0];
/* 121:    */   }
/* 122:    */   
/* 123:    */   public static void setSalt(byte[] bytes)
/* 124:    */   {
/* 125:    */     try
/* 126:    */     {
/* 127:114 */       setPropertyBytes("salt", bytes);
/* 128:    */     }
/* 129:    */     catch (Exception e)
/* 130:    */     {
/* 131:116 */       log.log(Level.WARNING, "Error setting salt: " + e.getLocalizedMessage());
/* 132:    */     }
/* 133:    */   }
/* 134:    */   
/* 135:    */   public static byte[] getSecret()
/* 136:    */   {
/* 137:    */     try
/* 138:    */     {
/* 139:122 */       return getPropertyBytes("secret");
/* 140:    */     }
/* 141:    */     catch (Exception e)
/* 142:    */     {
/* 143:124 */       log.warning("Error getting secret: " + e.getMessage());
/* 144:    */     }
/* 145:126 */     return new byte[0];
/* 146:    */   }
/* 147:    */   
/* 148:    */   public static void setSecret(byte[] bytes)
/* 149:    */   {
/* 150:    */     try
/* 151:    */     {
/* 152:131 */       setPropertyBytes("secret", bytes);
/* 153:    */     }
/* 154:    */     catch (Exception e)
/* 155:    */     {
/* 156:133 */       log.warning("Error setting secret: " + e.getMessage());
/* 157:    */     }
/* 158:    */   }
/* 159:    */   
/* 160:    */   public static byte[] getIv()
/* 161:    */   {
/* 162:    */     try
/* 163:    */     {
/* 164:139 */       return getPropertyBytes("aesiv");
/* 165:    */     }
/* 166:    */     catch (Exception e)
/* 167:    */     {
/* 168:141 */       log.warning("Error getting secret: " + e.getMessage());
/* 169:    */     }
/* 170:143 */     return new byte[0];
/* 171:    */   }
/* 172:    */   
/* 173:    */   public static void setIv(byte[] bytes)
/* 174:    */   {
/* 175:    */     try
/* 176:    */     {
/* 177:148 */       setPropertyBytes("aesiv", bytes);
/* 178:    */     }
/* 179:    */     catch (Exception e)
/* 180:    */     {
/* 181:150 */       log.warning("Error setting secret: " + e.getMessage());
/* 182:    */     }
/* 183:    */   }
/* 184:    */   
/* 185:    */   public static String getLocalFilePrefix()
/* 186:    */     throws IOException
/* 187:    */   {
/* 188:155 */     if (CryptoApplet.testMode) {
/* 189:156 */       return "test-mode-";
/* 190:    */     }
/* 191:158 */     String username = BrowserHandler.getField(BrowserHandler.getUsernameField());
/* 192:159 */     String host = CryptoApplet.getHostname().replaceAll("https?://", "").replaceAll(":", "").replaceAll("/", "");
/* 193:160 */     String prefix = username + host;
/* 194:161 */     return prefix + "-";
/* 195:    */   }
/* 196:    */ }


/* Location:           F:\Program Files\JDGUI\YDF\CryptoApplet-20140519102015.jar
 * Qualified Name:     au.com.yourdigitalfile.crypto_applet.os.OSHandler
 * JD-Core Version:    0.7.0.1
 */