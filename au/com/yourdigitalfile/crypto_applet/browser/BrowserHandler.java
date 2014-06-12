/*   1:    */ package au.com.yourdigitalfile.crypto_applet.browser;
/*   2:    */ 
/*   3:    */ import au.com.yourdigitalfile.crypto_applet.CryptoApplet;
/*   4:    */ import au.com.yourdigitalfile.crypto_applet.util.Base64Utils;
/*   5:    */ import au.com.yourdigitalfile.crypto_applet.util.Util;
/*   6:    */ import java.io.File;
/*   7:    */ import java.util.logging.Level;
/*   8:    */ import java.util.logging.Logger;
/*   9:    */ import netscape.javascript.JSException;
/*  10:    */ import netscape.javascript.JSObject;
/*  11:    */ 
/*  12:    */ public class BrowserHandler
/*  13:    */ {
/*  14: 16 */   private static final Logger log = Logger.getLogger(BrowserHandler.class.getName());
/*  15:    */   public static final String APPLET_NAME = "cryptoloc_applet";
/*  16:    */   public static final String FORM_NAME = "overlay_form";
/*  17:    */   public static final String FORM_FILE_INPUT_NAME = "filename";
/*  18:    */   public static final String PUBLIC_KEY = "public_key";
/*  19:    */   public static final String PASSWORD_HASH = "password_hash";
/*  20:    */   public static final String HASH = "hash";
/*  21:    */   public static final String TIMESTAMP = "timestamp";
/*  22:    */   public static final String SIGNATURE_INPUT = "signatureInput";
/*  23:    */   public static final String SIGNATURE = "signature";
/*  24:    */   public static final String KEY_SPLITS = "key_splits";
/*  25:    */   public static final String KEY_SPLITS_IN = "key_splits_in";
/*  26:    */   public static final String KEY_SPLITS_OUT = "key_splits_out";
/*  27:    */   public static final String DEFAULT_USERNAME_FIELD = "id_username";
/*  28:    */   public static final String USERNAME_FIELD = "username_field";
/*  29:    */   public static final String PRIVATE_KEY_PATH_FIELD = "private_key_path";
/*  30:    */   public static final String KEYSTORE_PATH = "keystorePath";
/*  31:    */   public static final String KEYSTORE_PASSWORD = "keystorePassword";
/*  32:    */   public static final String KEYSTORE_CONFIRM_PASSWORD = "keystoreConfirmPassword";
/*  33:    */   public static final String UPLOAD_PATH = "uploadPath";
/*  34:    */   private static JSObject browserWindow;
/*  35:    */   
/*  36:    */   private static JSObject getBrowserWindow()
/*  37:    */   {
/*  38: 43 */     if ((browserWindow == null) && (!CryptoApplet.testMode)) {
/*  39: 44 */       browserWindow = JSObject.getWindow(CryptoApplet.applet);
/*  40:    */     }
/*  41: 46 */     return browserWindow;
/*  42:    */   }
/*  43:    */   
/*  44:    */   private static boolean isPasswordField(String fieldname)
/*  45:    */   {
/*  46: 64 */     return ("keystorePassword".equals(fieldname)) || ("keystoreConfirmPassword".equals(fieldname));
/*  47:    */   }
/*  48:    */   
/*  49:    */   public static void updateUploadProgress(long current, long total)
/*  50:    */   {
/*  51: 68 */     safeJsEval("updateUploadProgress(" + current + " ," + total + ")");
/*  52:    */   }
/*  53:    */   
/*  54:    */   public static void setField(String name, String value)
/*  55:    */   {
/*  56: 72 */     log.info("Browser setField. name=" + name);
/*  57: 73 */     value = createJsString(value);
/*  58: 74 */     if (!isPasswordField(name)) {
/*  59: 74 */       log.info("escaped value=" + value);
/*  60:    */     }
/*  61: 75 */     safeJsEval("appletSetField('" + name + "', " + value + ")");
/*  62:    */   }
/*  63:    */   
/*  64:    */   private static String createJsString(String value)
/*  65:    */   {
/*  66: 79 */     return createJsString(value, "'");
/*  67:    */   }
/*  68:    */   
/*  69:    */   private static String createJsString(String value, String quote)
/*  70:    */   {
/*  71: 83 */     return quote + value.replaceAll("\\\\", "\\\\\\\\").replaceAll(quote, new StringBuilder().append("\\\\").append(quote).toString()).replaceAll("\n", new StringBuilder().append("\\\\n").append(quote).append("\\+").append(quote).toString()) + quote;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public static void setFieldB64(String id, byte[] value)
/*  75:    */   {
/*  76: 88 */     setField(id, Base64Utils.base64Encode(value));
/*  77:    */   }
/*  78:    */   
/*  79:    */   public static String getUsernameField()
/*  80:    */   {
/*  81: 92 */     String fieldname = getField("username_field");
/*  82: 93 */     return "".equals(fieldname) ? "id_username" : fieldname;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public static String getField(String name)
/*  86:    */   {
/*  87: 98 */     log.info("Browser getField.appletGetField name=" + name);
/*  88:    */     try
/*  89:    */     {
/*  90:100 */       String value = (String)safeJsEval("appletGetField('" + name + "')");
/*  91:101 */       if (!isPasswordField(name)) {
/*  92:101 */         log.info("value: " + value);
/*  93:    */       }
/*  94:102 */       return value == null ? "" : value;
/*  95:    */     }
/*  96:    */     catch (Exception e)
/*  97:    */     {
/*  98:104 */       log.severe("BrowserHandler: " + name + " not found in form data!");
/*  99:    */     }
/* 100:105 */     return "";
/* 101:    */   }
/* 102:    */   
/* 103:    */   public static String getFieldB64(String id)
/* 104:    */   {
/* 105:110 */     return Util.newString(Base64Utils.base64Decode(getField(id)));
/* 106:    */   }
/* 107:    */   
/* 108:    */   public static void setUploadFile(File f)
/* 109:    */   {
/* 110:114 */     log.finest("Browser setUploadFile");
/* 111:115 */     setField("filename", f.getName());
/* 112:    */   }
/* 113:    */   
/* 114:    */   public static String getCookie()
/* 115:    */   {
/* 116:    */     try
/* 117:    */     {
/* 118:121 */       String cookie = new String();
/* 119:122 */       return (String)safeJsEval("try{document.cookie;}catch(e){;}");
/* 120:    */     }
/* 121:    */     catch (Exception e)
/* 122:    */     {
/* 123:126 */       log.log(Level.SEVERE, e.getLocalizedMessage(), e.getMessage());
/* 124:    */     }
/* 125:128 */     return "";
/* 126:    */   }
/* 127:    */   
/* 128:    */   public static void success()
/* 129:    */   {
/* 130:132 */     log.info("success");
/* 131:133 */     safeJsEval("appletSuccess();");
/* 132:    */   }
/* 133:    */   
/* 134:    */   public static void error(String error)
/* 135:    */   {
/* 136:137 */     safeJsEval("appletError(" + createJsString(error) + ");");
/* 137:    */   }
/* 138:    */   
/* 139:    */   public static void uploadComplete()
/* 140:    */   {
/* 141:141 */     safeJsEval("uploadComplete();");
/* 142:    */   }
/* 143:    */   
/* 144:    */   public static void started()
/* 145:    */   {
/* 146:145 */     safeJsEval("cryptoAppletStarted();");
/* 147:    */   }
/* 148:    */   
/* 149:    */   public static void keystoreEnabled()
/* 150:    */   {
/* 151:149 */     safeJsEval("cryptoAppletKeystoreEnabled();");
/* 152:    */   }
/* 153:    */   
/* 154:    */   private static Object safeJsEval(String js)
/* 155:    */   {
/* 156:    */     try
/* 157:    */     {
/* 158:154 */       return getBrowserWindow().eval(js);
/* 159:    */     }
/* 160:    */     catch (JSException e)
/* 161:    */     {
/* 162:156 */       log.log(Level.SEVERE, "FAiled to eval: " + js, e);
/* 163:    */     }
/* 164:157 */     return null;
/* 165:    */   }
/* 166:    */ }


/* Location:           F:\Program Files\JDGUI\YDF\CryptoApplet-20140519102015.jar
 * Qualified Name:     au.com.yourdigitalfile.crypto_applet.browser.BrowserHandler
 * JD-Core Version:    0.7.0.1
 */