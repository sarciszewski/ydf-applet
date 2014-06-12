/*  1:   */ package au.com.yourdigitalfile.crypto_applet.ui;
/*  2:   */ 
/*  3:   */ import au.com.yourdigitalfile.crypto_applet.crypto.CryptoHandler;
/*  4:   */ import java.awt.Dimension;
/*  5:   */ import java.awt.Graphics;
/*  6:   */ import java.awt.Image;
/*  7:   */ import java.io.IOException;
/*  8:   */ import java.util.logging.Level;
/*  9:   */ import java.util.logging.Logger;
/* 10:   */ import javax.imageio.ImageIO;
/* 11:   */ import javax.swing.JPanel;
/* 12:   */ 
/* 13:   */ public class ImagePanel
/* 14:   */   extends JPanel
/* 15:   */ {
/* 16:   */   private static final long serialVersionUID = 3931900105533506488L;
/* 17:18 */   private static final Logger log = Logger.getLogger(ImagePanel.class.getName());
/* 18:   */   
/* 19:   */   public ImagePanel()
/* 20:   */   {
/* 21:21 */     updateImage();
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void updateImage()
/* 25:   */   {
/* 26:25 */     log.info("updateImage: isEnabled: " + CryptoHandler.isEnabled());
/* 27:26 */     if (CryptoHandler.isEnabled()) {
/* 28:27 */       this.current = getUnlocked();
/* 29:   */     } else {
/* 30:29 */       this.current = getLocked();
/* 31:   */     }
/* 32:31 */     repaint();
/* 33:   */   }
/* 34:   */   
/* 35:   */   protected void paintComponent(Graphics g)
/* 36:   */   {
/* 37:35 */     super.paintComponent(g);
/* 38:36 */     if (this.current != null) {
/* 39:37 */       g.drawImage(this.current, 0, 0, this);
/* 40:   */     }
/* 41:   */   }
/* 42:   */   
/* 43:41 */   private Image unlocked = null;
/* 44:42 */   private Image locked = null;
/* 45:43 */   private Image current = null;
/* 46:   */   
/* 47:   */   private Image getUnlocked()
/* 48:   */   {
/* 49:46 */     if (this.unlocked == null) {
/* 50:   */       try
/* 51:   */       {
/* 52:48 */         this.unlocked = ImageIO.read(getClass().getResource("/au/com/yourdigitalfile/crypto_applet/img/crypto_on.png"));
/* 53:   */       }
/* 54:   */       catch (IOException e)
/* 55:   */       {
/* 56:50 */         log.log(Level.SEVERE, e.getLocalizedMessage(), e);
/* 57:   */       }
/* 58:   */     }
/* 59:53 */     setPreferredSize(new Dimension(this.unlocked.getWidth(this), this.unlocked.getHeight(this)));
/* 60:54 */     return this.unlocked;
/* 61:   */   }
/* 62:   */   
/* 63:   */   private Image getLocked()
/* 64:   */   {
/* 65:57 */     if (this.locked == null) {
/* 66:   */       try
/* 67:   */       {
/* 68:59 */         this.locked = ImageIO.read(getClass().getResource("/au/com/yourdigitalfile/crypto_applet/img/crypto_off.png"));
/* 69:   */       }
/* 70:   */       catch (IOException e)
/* 71:   */       {
/* 72:61 */         log.log(Level.SEVERE, e.getLocalizedMessage(), e);
/* 73:   */       }
/* 74:   */     }
/* 75:64 */     return this.locked;
/* 76:   */   }
/* 77:   */ }


/* Location:           F:\Program Files\JDGUI\YDF\CryptoApplet-20140519102015.jar
 * Qualified Name:     au.com.yourdigitalfile.crypto_applet.ui.ImagePanel
 * JD-Core Version:    0.7.0.1
 */