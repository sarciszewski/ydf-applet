/*  1:   */ package au.com.yourdigitalfile.crypto_applet.ui;
/*  2:   */ 
/*  3:   */ import au.com.yourdigitalfile.crypto_applet.CryptoApplet;
/*  4:   */ import java.awt.Container;
/*  5:   */ import java.util.logging.Level;
/*  6:   */ import java.util.logging.Logger;
/*  7:   */ import javax.swing.JOptionPane;
/*  8:   */ import javax.swing.UIManager;
/*  9:   */ 
/* 10:   */ public class UIHandler
/* 11:   */ {
/* 12:12 */   private static final Logger log = Logger.getLogger(UIHandler.class.getName());
/* 13:   */   private ImagePanel imagePanel;
/* 14:   */   
/* 15:   */   public UIHandler()
/* 16:   */   {
/* 17:18 */     log.info("UI init");
/* 18:19 */     initLayout();
/* 19:   */     try
/* 20:   */     {
/* 21:21 */       UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
/* 22:   */     }
/* 23:   */     catch (Exception e)
/* 24:   */     {
/* 25:23 */       log.log(Level.SEVERE, "Error setting UIManager", e);
/* 26:   */     }
/* 27:   */   }
/* 28:   */   
/* 29:   */   public void closeApplet()
/* 30:   */   {
/* 31:28 */     JOptionPane.showMessageDialog(CryptoApplet.applet, "Error starting Crypto manager!");
/* 32:   */   }
/* 33:   */   
/* 34:   */   public void updateImage()
/* 35:   */   {
/* 36:33 */     if (this.imagePanel != null) {
/* 37:34 */       this.imagePanel.updateImage();
/* 38:   */     }
/* 39:   */   }
/* 40:   */   
/* 41:   */   protected void initLayout()
/* 42:   */   {
/* 43:39 */     if (!CryptoApplet.testMode)
/* 44:   */     {
/* 45:40 */       this.imagePanel = new ImagePanel();
/* 46:41 */       CryptoApplet.applet.getContentPane().add(this.imagePanel);
/* 47:42 */       CryptoApplet.applet.setSize(240, 100);
/* 48:   */     }
/* 49:   */   }
/* 50:   */ }


/* Location:           F:\Program Files\JDGUI\YDF\CryptoApplet-20140519102015.jar
 * Qualified Name:     au.com.yourdigitalfile.crypto_applet.ui.UIHandler
 * JD-Core Version:    0.7.0.1
 */