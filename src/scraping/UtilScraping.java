package scraping;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import constantes.Palo;
import logica.Carta;

public class UtilScraping {
	
	public static void clickar(int x, int y){
		Robot bot;
		try {
			bot = new Robot();
			bot.mouseMove(x, y);
	        bot.mousePress(InputEvent.BUTTON1_MASK);
	        espera(50);
	        bot.mouseRelease(InputEvent.BUTTON1_MASK);
	        espera(50);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public static BufferedImage captureScreen(Rectangle screenRectangle) {
		//Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		Robot robot;
		BufferedImage image = null;
		try {
			robot = new Robot();
			image = robot.createScreenCapture(screenRectangle);
		} catch (AWTException e) {
			e.printStackTrace();
		}
		return image;
	}

	public static String copiarTodoElTexto(){
        String texto="";
        Robot bot;
        
		try {
			bot = new Robot();
        
	        //selecciona todo el texto.
	        bot.keyPress(KeyEvent.VK_CONTROL);
	        bot.keyPress(KeyEvent.VK_A);
	        espera(10);
	        bot.keyRelease(KeyEvent.VK_A);
	        bot.keyRelease(KeyEvent.VK_CONTROL);
	        espera(100);
	        
	        //copia al portatpapeles todo el texto.
	        bot.keyPress(KeyEvent.VK_CONTROL);
	        bot.keyPress(KeyEvent.VK_C);
	        espera(10);
	        bot.keyRelease(KeyEvent.VK_C);
	        bot.keyRelease(KeyEvent.VK_CONTROL);
	        espera(100);
	        	        
	        // Obtenemos el contenido del portapapeles del sistema.
	        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
	        try{
	            // Comprobamos que la información sea de tipo cadena, lo recuperamos y lo devolvemos.
	            if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor))            {
	                texto = (String) t.getTransferData(DataFlavor.stringFlavor);
	            }
	        }catch (UnsupportedFlavorException e) {e.printStackTrace();}catch (IOException e){e.printStackTrace();}

		} catch (AWTException e1) {
			e1.printStackTrace();
		}
        return texto;
    }
	
	public static void espera(long s) {
		try {
			Thread.currentThread().sleep(s * 1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
