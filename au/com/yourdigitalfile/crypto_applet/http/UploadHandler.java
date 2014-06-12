/*   1:    */ package au.com.yourdigitalfile.crypto_applet.http;
/*   2:    */ 
/*   3:    */ import au.com.yourdigitalfile.crypto_applet.CryptoApplet;
/*   4:    */ import au.com.yourdigitalfile.crypto_applet.browser.BrowserHandler;
/*   5:    */ import java.io.BufferedReader;
/*   6:    */ import java.io.File;
/*   7:    */ import java.io.FileInputStream;
/*   8:    */ import java.io.IOException;
/*   9:    */ import java.io.InputStream;
/*  10:    */ import java.io.InputStreamReader;
/*  11:    */ import java.io.OutputStream;
/*  12:    */ import java.io.OutputStreamWriter;
/*  13:    */ import java.io.PrintWriter;
/*  14:    */ import java.net.HttpURLConnection;
/*  15:    */ import java.net.MalformedURLException;
/*  16:    */ import java.net.URL;
/*  17:    */ import java.net.URLConnection;
/*  18:    */ import java.net.URLEncoder;
/*  19:    */ import java.util.Map;
/*  20:    */ import java.util.logging.Logger;
/*  21:    */ 
/*  22:    */ public class UploadHandler
/*  23:    */ {
/*  24: 26 */   private static final Logger log = Logger.getLogger(UploadHandler.class.getName());
/*  25:    */   private static final String CRLF = "\r\n";
/*  26:    */   private static final String SUCCESS = "Status: success";
/*  27:    */   private static final String NO_REPLY_ERROR_MESSAGE = "Sorry the server is not available!";
/*  28:    */   private static final String UPLOAD_URI = "/upload/";
/*  29:    */   private Map<String, String> parameters;
/*  30:    */   private File upload;
/*  31:    */   private String csrfMiddlewareToken;
/*  32:    */   
/*  33:    */   public UploadHandler(File upload, String csrfMiddlewareToken, Map<String, String> parameters)
/*  34:    */   {
/*  35: 53 */     this.upload = upload;
/*  36: 54 */     this.csrfMiddlewareToken = csrfMiddlewareToken;
/*  37: 55 */     this.parameters = parameters;
/*  38:    */   }
/*  39:    */   
/*  40:    */   private URL createURL()
/*  41:    */     throws MalformedURLException
/*  42:    */   {
/*  43: 59 */     return new URL((CryptoApplet.testMode ? "http://localhost:8000" : CryptoApplet.getHostname()) + (CryptoApplet.testMode ? "/test_upload/" : "/upload/") + createQueryString());
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void doUpload()
/*  47:    */     throws IOException
/*  48:    */   {
/*  49: 67 */     String boundary = Long.toHexString(System.currentTimeMillis());
/*  50:    */     
/*  51:    */ 
/*  52:    */ 
/*  53:    */ 
/*  54: 72 */     String fileHeader = "--" + boundary + "\r\n" + "Content-Disposition: form-data; name=\"userfile\"; filename=\"" + this.upload.getName() + "\"" + "\r\n";
/*  55:    */     
/*  56:    */ 
/*  57: 75 */     fileHeader = fileHeader + "Content-Type: application/octet-stream\r\n\r\n";
/*  58:    */     
/*  59: 77 */     String fileFooter = "\r\n--" + boundary + "--" + "\r\n";
/*  60:    */     
/*  61: 79 */     HttpURLConnection connection = (HttpURLConnection)createURL().openConnection();
/*  62: 80 */     setupConnectionHeaders(connection, boundary, fileHeader.length() + this.upload.length() + fileFooter.length());
/*  63:    */     try
/*  64:    */     {
/*  65: 82 */       writeFileToServer(connection, fileHeader, fileFooter);
/*  66:    */     }
/*  67:    */     catch (IOException e) {}
/*  68: 85 */     String reply = fetchResponse(connection);
/*  69: 86 */     log.info("REPLY: " + reply);
/*  70: 87 */     if ((reply != null) && (reply.length() > 0))
/*  71:    */     {
/*  72: 88 */       if (reply.indexOf("Status: success") > -1) {
/*  73: 89 */         return;
/*  74:    */       }
/*  75: 91 */       log.severe("UploadHandler: File upload failed: \n\n" + reply);
/*  76: 92 */       String errorParsed = reply;
/*  77:    */       try
/*  78:    */       {
/*  79: 94 */         errorParsed = reply.split("\\n\\n")[1];
/*  80:    */       }
/*  81:    */       catch (Exception e) {}
/*  82: 96 */       throw new RuntimeException(errorParsed);
/*  83:    */     }
/*  84: 99 */     log.severe("UploadHandler: File upload failed for " + this.upload.getName() + ": No response from server");
/*  85:100 */     throw new RuntimeException("Sorry the server is not available!");
/*  86:    */   }
/*  87:    */   
/*  88:    */   private String fetchResponse(HttpURLConnection connection)
/*  89:    */     throws IOException
/*  90:    */   {
/*  91:105 */     StringBuilder sb = new StringBuilder("");
/*  92:    */     
/*  93:107 */     int status = connection.getResponseCode();
/*  94:109 */     if (status >= 400)
/*  95:    */     {
/*  96:110 */       BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
/*  97:    */       try
/*  98:    */       {
/*  99:    */         String line;
/* 100:112 */         while ((line = reader.readLine()) != null) {
/* 101:113 */           sb.append(line);
/* 102:    */         }
/* 103:115 */         if (sb.length() > 0) {
/* 104:115 */           return sb.toString();
/* 105:    */         }
/* 106:    */         try
/* 107:    */         {
/* 108:117 */           reader.close();
/* 109:    */         }
/* 110:    */         catch (IOException logOrIgnore) {}
/* 111:121 */         reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
/* 112:    */       }
/* 113:    */       catch (IOException logOrIgnore) {}finally
/* 114:    */       {
/* 115:    */         try
/* 116:    */         {
/* 117:117 */           reader.close();
/* 118:    */         }
/* 119:    */         catch (IOException logOrIgnore) {}
/* 120:    */       }
/* 121:    */     }
/* 122:    */     try
/* 123:    */     {
/* 124:    */       BufferedReader reader;
/* 125:    */       String line;
/* 126:123 */       while ((line = reader.readLine()) != null) {
/* 127:124 */         sb.append(line);
/* 128:    */       }
/* 129:126 */       if (sb.length() > 0) {
/* 130:126 */         return sb.toString();
/* 131:    */       }
/* 132:    */       try
/* 133:    */       {
/* 134:128 */         reader.close();
/* 135:    */       }
/* 136:    */       catch (IOException logOrIgnore) {}
/* 137:    */       try
/* 138:    */       {
/* 139:131 */         sb.append(connection.getResponseMessage());
/* 140:    */       }
/* 141:    */       catch (IOException logOrIgnore) {}
/* 142:    */     }
/* 143:    */     catch (IOException logOrIgnore) {}finally
/* 144:    */     {
/* 145:    */       try
/* 146:    */       {
/* 147:128 */         reader.close();
/* 148:    */       }
/* 149:    */       catch (IOException logOrIgnore) {}
/* 150:    */     }
/* 151:133 */     return sb.toString();
/* 152:    */   }
/* 153:    */   
/* 154:    */   private void setupConnectionHeaders(HttpURLConnection connection, String boundary, long contentLength)
/* 155:    */     throws IOException
/* 156:    */   {
/* 157:138 */     connection.setDoOutput(true);
/* 158:139 */     connection.setRequestMethod("POST");
/* 159:140 */     connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
/* 160:141 */     connection.setRequestProperty("Connection", "close");
/* 161:142 */     connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Java/CryptoApplet; rv:0.1.35) Gecko/20100401");
/* 162:    */     
/* 163:    */ 
/* 164:145 */     String cookie = BrowserHandler.getCookie();
/* 165:146 */     log.info("cookie: " + cookie);
/* 166:147 */     if (cookie.length() > 0)
/* 167:    */     {
/* 168:148 */       log.finest("HttpHandler: SETTING COOKIE in headers");
/* 169:149 */       connection.setRequestProperty("Cookie", cookie + "; sessionid=" + CryptoApplet.getSessionKey());
/* 170:    */     }
/* 171:153 */     if (this.csrfMiddlewareToken != null) {
/* 172:153 */       connection.setRequestProperty("X-CSRFToken", this.csrfMiddlewareToken);
/* 173:    */     }
/* 174:155 */     connection.setFixedLengthStreamingMode(contentLength);
/* 175:    */   }
/* 176:    */   
/* 177:    */   private void updateProgress(long totalRead, long uploadSize)
/* 178:    */   {
/* 179:159 */     log.info("Total Read: " + totalRead + ", Total Size: " + uploadSize + " Progress: " + (uploadSize == 0L ? "" : Double.valueOf(totalRead / (uploadSize * 1.0D))));
/* 180:    */     
/* 181:161 */     BrowserHandler.updateUploadProgress(totalRead, uploadSize);
/* 182:    */   }
/* 183:    */   
/* 184:    */   private void writeFileToServer(URLConnection connection, String fileHeader, String fileFooter)
/* 185:    */     throws IOException
/* 186:    */   {
/* 187:165 */     PrintWriter writer = null;
/* 188:    */     try
/* 189:    */     {
/* 190:167 */       OutputStream output = connection.getOutputStream();
/* 191:168 */       writer = new PrintWriter(new OutputStreamWriter(output), true);
/* 192:169 */       writer.append(fileHeader);
/* 193:170 */       writer.flush();
/* 194:171 */       InputStream input = new FileInputStream(this.upload);
/* 195:    */       try
/* 196:    */       {
/* 197:173 */         byte[] buffer = new byte[1024];
/* 198:174 */         long uploadSize = this.upload.length();
/* 199:175 */         long totalRead = 0L;
/* 200:176 */         long lastUpdateTime = 0L;
/* 201:177 */         for (int length = 0; (length = input.read(buffer)) > 0;)
/* 202:    */         {
/* 203:178 */           totalRead += length;
/* 204:179 */           if (lastUpdateTime + 100L < System.currentTimeMillis())
/* 205:    */           {
/* 206:180 */             lastUpdateTime = System.currentTimeMillis();
/* 207:181 */             updateProgress(totalRead, uploadSize);
/* 208:    */           }
/* 209:183 */           output.write(buffer, 0, length);
/* 210:    */         }
/* 211:186 */         output.flush();
/* 212:    */         try
/* 213:    */         {
/* 214:189 */           input.close();
/* 215:    */         }
/* 216:    */         catch (IOException logOrIgnore) {}
/* 217:193 */         writer.append(fileFooter);
/* 218:    */       }
/* 219:    */       finally
/* 220:    */       {
/* 221:    */         try
/* 222:    */         {
/* 223:189 */           input.close();
/* 224:    */         }
/* 225:    */         catch (IOException logOrIgnore) {}
/* 226:    */       }
/* 227:194 */       writer.flush();
/* 228:    */     }
/* 229:    */     finally
/* 230:    */     {
/* 231:197 */       if (writer != null) {
/* 232:197 */         writer.close();
/* 233:    */       }
/* 234:    */     }
/* 235:    */   }
/* 236:    */   
/* 237:    */   private String createQueryString()
/* 238:    */   {
/* 239:202 */     StringBuilder sb = new StringBuilder("");
/* 240:203 */     sb.append("?");
/* 241:204 */     for (String id : this.parameters.keySet())
/* 242:    */     {
/* 243:205 */       sb.append(id).append("=");
/* 244:206 */       String val = (String)this.parameters.get(id);
/* 245:    */       try
/* 246:    */       {
/* 247:208 */         val = URLEncoder.encode(val, "UTF-8");
/* 248:    */       }
/* 249:    */       catch (Exception e)
/* 250:    */       {
/* 251:210 */         val = "";
/* 252:211 */         log.warning("HttpHandler: Unable to URL encode " + id);
/* 253:212 */         log.finest("Value: " + val);
/* 254:    */       }
/* 255:214 */       sb.append(val).append("&");
/* 256:    */     }
/* 257:216 */     sb.setLength(sb.length() - 1);
/* 258:217 */     return sb.toString();
/* 259:    */   }
/* 260:    */ }


/* Location:           F:\Program Files\JDGUI\YDF\CryptoApplet-20140519102015.jar
 * Qualified Name:     au.com.yourdigitalfile.crypto_applet.http.UploadHandler
 * JD-Core Version:    0.7.0.1
 */