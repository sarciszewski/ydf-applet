/*   1:    */ package au.com.yourdigitalfile.crypto_applet.util;
/*   2:    */ 
/*   3:    */ public class Base64Utils
/*   4:    */ {
/*   5:    */   private static byte[] mBase64EncMap;
/*   6:    */   private static byte[] mBase64DecMap;
/*   7:    */   
/*   8:    */   static
/*   9:    */   {
/*  10: 14 */     byte[] base64Map = { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
/*  11:    */     
/*  12:    */ 
/*  13:    */ 
/*  14:    */ 
/*  15:    */ 
/*  16:    */ 
/*  17:    */ 
/*  18:    */ 
/*  19:    */ 
/*  20:    */ 
/*  21:    */ 
/*  22:    */ 
/*  23: 27 */     mBase64EncMap = base64Map;
/*  24: 28 */     mBase64DecMap = new byte[''];
/*  25: 29 */     for (int i = 0; i < mBase64EncMap.length; i++) {
/*  26: 30 */       mBase64DecMap[mBase64EncMap[i]] = ((byte)i);
/*  27:    */     }
/*  28:    */   }
/*  29:    */   
/*  30:    */   public static String base64Encode(byte[] aData)
/*  31:    */   {
/*  32: 48 */     if ((aData == null) || (aData.length == 0)) {
/*  33: 49 */       throw new IllegalArgumentException("Can not encode NULL or empty byte array.");
/*  34:    */     }
/*  35: 51 */     byte[] encodedBuf = new byte[(aData.length + 2) / 3 * 4];
/*  36:    */     
/*  37:    */ 
/*  38:    */ 
/*  39: 55 */     int srcIndex = 0;
/*  40: 55 */     for (int destIndex = 0; srcIndex < aData.length - 2; srcIndex += 3)
/*  41:    */     {
/*  42: 56 */       encodedBuf[(destIndex++)] = mBase64EncMap[(aData[srcIndex] >>> 2 & 0x3F)];
/*  43: 57 */       encodedBuf[(destIndex++)] = mBase64EncMap[(aData[(srcIndex + 1)] >>> 4 & 0xF | aData[srcIndex] << 4 & 0x3F)];
/*  44:    */       
/*  45: 59 */       encodedBuf[(destIndex++)] = mBase64EncMap[(aData[(srcIndex + 2)] >>> 6 & 0x3 | aData[(srcIndex + 1)] << 2 & 0x3F)];
/*  46:    */       
/*  47: 61 */       encodedBuf[(destIndex++)] = mBase64EncMap[(aData[(srcIndex + 2)] & 0x3F)];
/*  48:    */     }
/*  49: 65 */     if (srcIndex < aData.length)
/*  50:    */     {
/*  51: 66 */       encodedBuf[(destIndex++)] = mBase64EncMap[(aData[srcIndex] >>> 2 & 0x3F)];
/*  52: 67 */       if (srcIndex < aData.length - 1)
/*  53:    */       {
/*  54: 68 */         encodedBuf[(destIndex++)] = mBase64EncMap[(aData[(srcIndex + 1)] >>> 4 & 0xF | aData[srcIndex] << 4 & 0x3F)];
/*  55:    */         
/*  56: 70 */         encodedBuf[(destIndex++)] = mBase64EncMap[(aData[(srcIndex + 1)] << 2 & 0x3F)];
/*  57:    */       }
/*  58:    */       else
/*  59:    */       {
/*  60: 73 */         encodedBuf[(destIndex++)] = mBase64EncMap[(aData[srcIndex] << 4 & 0x3F)];
/*  61:    */       }
/*  62:    */     }
/*  63: 78 */     while (destIndex < encodedBuf.length)
/*  64:    */     {
/*  65: 79 */       encodedBuf[destIndex] = 61;
/*  66: 80 */       destIndex++;
/*  67:    */     }
/*  68: 83 */     String result = new String(encodedBuf);
/*  69: 84 */     return result;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public static byte[] base64Decode(String aData)
/*  73:    */   {
/*  74:105 */     if ((aData == null) || (aData.length() == 0)) {
/*  75:106 */       throw new IllegalArgumentException("Can not decode NULL or empty string.");
/*  76:    */     }
/*  77:108 */     byte[] data = aData.getBytes();
/*  78:    */     
/*  79:    */ 
/*  80:111 */     int tail = data.length;
/*  81:112 */     while (data[(tail - 1)] == 61) {
/*  82:113 */       tail--;
/*  83:    */     }
/*  84:115 */     byte[] decodedBuf = new byte[tail - data.length / 4];
/*  85:118 */     for (int i = 0; i < data.length; i++) {
/*  86:119 */       data[i] = mBase64DecMap[data[i]];
/*  87:    */     }
/*  88:123 */     int srcIndex = 0;
/*  89:123 */     for (int destIndex = 0; destIndex < decodedBuf.length - 2; destIndex += 3)
/*  90:    */     {
/*  91:125 */       decodedBuf[destIndex] = ((byte)(data[srcIndex] << 2 & 0xFF | data[(srcIndex + 1)] >>> 4 & 0x3));
/*  92:    */       
/*  93:127 */       decodedBuf[(destIndex + 1)] = ((byte)(data[(srcIndex + 1)] << 4 & 0xFF | data[(srcIndex + 2)] >>> 2 & 0xF));
/*  94:    */       
/*  95:129 */       decodedBuf[(destIndex + 2)] = ((byte)(data[(srcIndex + 2)] << 6 & 0xFF | data[(srcIndex + 3)] & 0x3F));srcIndex += 4;
/*  96:    */     }
/*  97:134 */     if (destIndex < decodedBuf.length) {
/*  98:135 */       decodedBuf[destIndex] = ((byte)(data[srcIndex] << 2 & 0xFF | data[(srcIndex + 1)] >>> 4 & 0x3));
/*  99:    */     }
/* 100:137 */     destIndex++;
/* 101:137 */     if (destIndex < decodedBuf.length) {
/* 102:138 */       decodedBuf[destIndex] = ((byte)(data[(srcIndex + 1)] << 4 & 0xFF | data[(srcIndex + 2)] >>> 2 & 0xF));
/* 103:    */     }
/* 104:141 */     return decodedBuf;
/* 105:    */   }
/* 106:    */ }


/* Location:           F:\Program Files\JDGUI\YDF\CryptoApplet-20140519102015.jar
 * Qualified Name:     au.com.yourdigitalfile.crypto_applet.util.Base64Utils
 * JD-Core Version:    0.7.0.1
 */