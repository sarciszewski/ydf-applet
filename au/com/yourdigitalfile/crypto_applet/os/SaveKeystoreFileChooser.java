/*  1:   */ package au.com.yourdigitalfile.crypto_applet.os;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ import java.io.PrintStream;
/*  5:   */ import javax.swing.JFileChooser;
/*  6:   */ import javax.swing.JOptionPane;
/*  7:   */ import javax.swing.filechooser.FileFilter;
/*  8:   */ import javax.swing.filechooser.FileNameExtensionFilter;
/*  9:   */ 
/* 10:   */ public class SaveKeystoreFileChooser
/* 11:   */   extends JFileChooser
/* 12:   */ {
/* 13:   */   private static final long serialVersionUID = 7919427933588163126L;
/* 14:   */   
/* 15:   */   public SaveKeystoreFileChooser()
/* 16:   */   {
/* 17:16 */     setFileHidingEnabled(false);
/* 18:17 */     FileFilter filter = new FileNameExtensionFilter("Your Digital File Private Key (.pfx)", new String[] { "pfx" });
/* 19:18 */     addChoosableFileFilter(filter);
/* 20:19 */     setFileFilter(filter);
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void approveSelection()
/* 24:   */   {
/* 25:23 */     if ((!getFileFilter().accept(getSelectedFile())) && 
/* 26:24 */       ((getFileFilter() instanceof FileNameExtensionFilter)))
/* 27:   */     {
/* 28:25 */       String[] extensions = ((FileNameExtensionFilter)getFileFilter()).getExtensions();
/* 29:26 */       String ext = extensions.length > 0 ? extensions[0] : "";
/* 30:27 */       setSelectedFile(new File(getSelectedFile().getAbsolutePath() + ((ext == null) || (ext.length() == 0) ? "" : new StringBuilder().append(".").append(ext).toString())));
/* 31:   */     }
/* 32:32 */     File f = getSelectedFile();
/* 33:33 */     if ((f.exists()) && (getDialogType() == 1))
/* 34:   */     {
/* 35:34 */       int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to override existing file?", "Existing file", 1, 3);
/* 36:40 */       switch (result)
/* 37:   */       {
/* 38:   */       case 0: 
/* 39:42 */         super.approveSelection();
/* 40:43 */         return;
/* 41:   */       case 2: 
/* 42:45 */         cancelSelection();
/* 43:46 */         return;
/* 44:   */       }
/* 45:48 */       return;
/* 46:   */     }
/* 47:51 */     super.approveSelection();
/* 48:   */   }
/* 49:   */   
/* 50:   */   public static void main(String[] args)
/* 51:   */   {
/* 52:55 */     SaveKeystoreFileChooser chooser = new SaveKeystoreFileChooser();
/* 53:56 */     chooser.setSelectedFile(new File("test.pfx"));
/* 54:57 */     chooser.showSaveDialog(null);
/* 55:58 */     System.out.println("sel:" + chooser.getSelectedFile());
/* 56:   */   }
/* 57:   */ }


/* Location:           F:\Program Files\JDGUI\YDF\CryptoApplet-20140519102015.jar
 * Qualified Name:     au.com.yourdigitalfile.crypto_applet.os.SaveKeystoreFileChooser
 * JD-Core Version:    0.7.0.1
 */