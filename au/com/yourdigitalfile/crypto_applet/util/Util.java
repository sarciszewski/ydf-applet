/*   1:    */ package au.com.yourdigitalfile.crypto_applet.util;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.FileInputStream;
/*   5:    */ import java.io.FileOutputStream;
/*   6:    */ import java.io.IOException;
/*   7:    */ import java.io.InputStream;
/*   8:    */ import java.io.OutputStream;
/*   9:    */ import java.io.PrintWriter;
/*  10:    */ import java.io.StringWriter;
/*  11:    */ import java.nio.ByteBuffer;
/*  12:    */ import java.nio.CharBuffer;
/*  13:    */ import java.nio.charset.Charset;
/*  14:    */ import java.security.SecureRandom;
/*  15:    */ import java.util.Random;
/*  16:    */ 
/*  17:    */ public class Util
/*  18:    */ {
/*  19:    */   private static final int PASSWORD_MIN_LENGTH = 8;
/*  20:    */   public static final String PASSWORD_SHORT = "Your password must be a minimum of 8 characters!";
/*  21:    */   public static final String PASSWORD_MISMATCH = "Your password and confirm do not match!";
/*  22:    */   public static final String PASSWORD_WEAK = "Your password must contain a mix of upper case, lower case and digits!";
/*  23:    */   
/*  24:    */   public static void blankOutCharArray(char[] array)
/*  25:    */   {
/*  26: 26 */     for (int i = 0; i < array.length; i++) {
/*  27: 26 */       array[i] = '\000';
/*  28:    */     }
/*  29:    */   }
/*  30:    */   
/*  31:    */   public static void blankOutByteArray(byte[] array)
/*  32:    */   {
/*  33: 30 */     for (int i = 0; i < array.length; i++) {
/*  34: 30 */       array[i] = 0;
/*  35:    */     }
/*  36:    */   }
/*  37:    */   
/*  38:    */   public static String checkPasswordAcceptable(char[] pwd)
/*  39:    */   {
/*  40: 34 */     if (pwd.length < 8) {
/*  41: 35 */       return "Your password must be a minimum of 8 characters!";
/*  42:    */     }
/*  43: 37 */     boolean hasLower = false;
/*  44: 38 */     boolean hasUpper = false;
/*  45: 39 */     boolean hasDigit = false;
/*  46: 41 */     for (char c : pwd) {
/*  47: 42 */       if (Character.isUpperCase(c)) {
/*  48: 43 */         hasUpper = true;
/*  49: 44 */       } else if (Character.isLowerCase(c)) {
/*  50: 45 */         hasLower = true;
/*  51: 46 */       } else if (Character.isDigit(c)) {
/*  52: 47 */         hasDigit = true;
/*  53:    */       }
/*  54:    */     }
/*  55: 49 */     if ((hasUpper) && (hasLower) && (hasDigit)) {
/*  56: 50 */       return "";
/*  57:    */     }
/*  58: 51 */     return "Your password must contain a mix of upper case, lower case and digits!";
/*  59:    */   }
/*  60:    */   
/*  61:    */   public static byte[] getBytes(String str)
/*  62:    */   {
/*  63:    */     try
/*  64:    */     {
/*  65: 56 */       return str.getBytes("UTF-8");
/*  66:    */     }
/*  67:    */     catch (Exception e) {}
/*  68: 58 */     return str.getBytes();
/*  69:    */   }
/*  70:    */   
/*  71:    */   public static byte[] getBytes(char[] c)
/*  72:    */   {
/*  73: 72 */     ByteBuffer bb = Charset.forName("UTF-8").encode(CharBuffer.wrap(c));
/*  74: 73 */     byte[] b = new byte[bb.remaining()];
/*  75: 74 */     bb.get(b);
/*  76: 75 */     blankOutByteArray(bb.array());
/*  77: 76 */     return b;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public static byte[] getBytes(File file)
/*  81:    */     throws IOException
/*  82:    */   {
/*  83: 81 */     FileInputStream fis = new FileInputStream(file);
/*  84:    */     try
/*  85:    */     {
/*  86: 83 */       int fileSize = (int)file.length();
/*  87: 84 */       byte[] data = new byte[fileSize];
/*  88: 85 */       int bytesRead = 0;
/*  89: 86 */       while (bytesRead < fileSize) {
/*  90: 87 */         bytesRead += fis.read(data, bytesRead, fileSize - bytesRead);
/*  91:    */       }
/*  92: 89 */       return data;
/*  93:    */     }
/*  94:    */     finally
/*  95:    */     {
/*  96: 92 */       fis.close();
/*  97:    */     }
/*  98:    */   }
/*  99:    */   
/* 100:    */   public static char[] getChars(byte[] b)
/* 101:    */   {
/* 102: 97 */     CharBuffer cb = Charset.forName("UTF-8").decode(ByteBuffer.wrap(b));
/* 103: 98 */     char[] c = new char[cb.remaining()];
/* 104: 99 */     cb.get(c);
/* 105:100 */     blankOutCharArray(cb.array());
/* 106:101 */     return c;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public static String newString(byte[] data)
/* 110:    */   {
/* 111:105 */     if (data == null) {
/* 112:105 */       return null;
/* 113:    */     }
/* 114:    */     try
/* 115:    */     {
/* 116:107 */       return new String(data, "UTF-8");
/* 117:    */     }
/* 118:    */     catch (Exception e) {}
/* 119:109 */     return new String(data);
/* 120:    */   }
/* 121:    */   
/* 122:141 */   private static final char[] ALPHANUMERIC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
/* 123:143 */   private static Random random = new SecureRandom();
/* 124:    */   
/* 125:    */   public static String createRandomAplhaNum(int length)
/* 126:    */   {
/* 127:146 */     if (length < 1) {
/* 128:146 */       throw new IllegalArgumentException("length < 1: " + length);
/* 129:    */     }
/* 130:147 */     char[] buf = new char[length];
/* 131:148 */     for (int idx = 0; idx < buf.length; idx++) {
/* 132:149 */       buf[idx] = ALPHANUMERIC[random.nextInt(ALPHANUMERIC.length)];
/* 133:    */     }
/* 134:151 */     return new String(buf);
/* 135:    */   }
/* 136:    */   
/* 137:    */   public static void copyFile(File src, File dst)
/* 138:    */     throws IOException
/* 139:    */   {
/* 140:155 */     InputStream is = null;
/* 141:156 */     OutputStream os = null;
/* 142:    */     try
/* 143:    */     {
/* 144:158 */       is = new FileInputStream(src);
/* 145:159 */       os = new FileOutputStream(dst);
/* 146:160 */       byte[] buffer = new byte[1024];
/* 147:    */       int length;
/* 148:162 */       while ((length = is.read(buffer)) > 0) {
/* 149:163 */         os.write(buffer, 0, length);
/* 150:    */       }
/* 151:    */     }
/* 152:    */     finally
/* 153:    */     {
/* 154:166 */       is.close();
/* 155:167 */       os.close();
/* 156:    */     }
/* 157:    */   }
/* 158:    */   
/* 159:    */   public static String stacktraceToString(Throwable e)
/* 160:    */   {
/* 161:172 */     StringWriter sw = new StringWriter();
/* 162:173 */     e.printStackTrace(new PrintWriter(sw));
/* 163:174 */     return sw.toString();
/* 164:    */   }
/* 165:    */ }


/* Location:           F:\Program Files\JDGUI\YDF\CryptoApplet-20140519102015.jar
 * Qualified Name:     au.com.yourdigitalfile.crypto_applet.util.Util
 * JD-Core Version:    0.7.0.1
 */