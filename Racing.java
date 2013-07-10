import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

//MAIN
//-------------------------------------------------------------------------------------------------------
public class Racing extends JFrame {

	int width = 600; 	// Breite des Fensters
	int height = 800; 	// Höhe des Fensters

	RacingPanel rpanel; // erstellen eines neuen Panels rpanel
	JTextField score;

	int step = 0; 		// Geschwindigkeit der Animation

	public static void main(String[] args) {
		Racing wnd = new Racing();
	}

	public Racing() {
		setSize(width, height); 							// setzte Fenstergröße fest
		setTitle("Das ultimativ langweilige Rennspiel"); 	// setze den Titel
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 	// setze X auf Close

		rpanel = new RacingPanel(this); 					// initialisieren des Panels rpanel

		Container cpane = getContentPane(); 				// erstellen eines Containers
		cpane.setLayout(new BorderLayout()); 				// und zuweisen eines Standard-Layouts
		cpane.add(rpanel, BorderLayout.CENTER); 			// added das rpanel zum Container (mittig)

		JButton startbutton = new JButton("Start"); 		// erstellen des Startbuttons mit Aufschrift "start"
		cpane.add(startbutton, BorderLayout.WEST); 			// added den button rechts an den Container
		startbutton.resetKeyboardActions(); 				// resetet alle Keys
		
		score = new JTextField("0");
		cpane.add(score,BorderLayout.NORTH);

		// Keylistener für Pfeiltasten
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				// wenn Pfeil nach unten gedrückt
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					rpanel.y = rpanel.y + 10;
				} // bewege rpanel runter

				// wenn Pfeil nach oben gedrückt
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					rpanel.y = rpanel.y - 10;
				} // bewege rpanel nach oben
			}
		});

		// Listener für den Startbutton
		startbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// wenn Button auf 0 steht, so startet er das Spiel
				if (step == 0) {
					step = 1;
					((JButton) e.getSource()).setText("Stop");
				}

				// wenn Button auf 1 steht, so stoppt er das Spiel
				else {
					step = 0;
					((JButton) e.getSource()).setText("Start");
				}
			}
		});

		pack(); 						// "verpackt" das Frame
		setVisible(true); 				// zeige die Komponenten an

		Thread t = new Thread(rpanel); 	// rpanel wird zu einem seperierten
										// Thread
		t.start(); 						// startet den Thread
	}
	
	public void setScore(String score){
		this.score.setText(score);
	}
	
	public int getCyclePos(){
		return this.rpanel.y;			// liefert den y-Wert vom Fahrradfahrer
	}
}
// -------------------------------------------------------------------------------------------------------

// Racing Panel
// -------------------------------------------------------------------------------------------------------
class RacingPanel extends JPanel implements Runnable {
	Racing rc;
	int offset = 0;
	int streckenlaenge = 8000;
	int y = 300; 	// die vertikale Position des Fahrradfahrers
	
	boolean start = false;
	int x2 = 0;
	
	int points = 0;
	
	int y1 = 0, y2 = 0;
	
	// Konstruktor
	RacingPanel(Racing racing) {
		Bilder.init(this);
		rc = racing;
		setPreferredSize(new Dimension(rc.width, rc.height));
	}

	// Malt alle Graphiken
	public void paint(Graphics g) {
		g.setColor(Color.blue);					//färbt den Hintergrund blau
		Dimension d = getSize();
		g.fillRect(0, 0, d.width, d.height);
		for (int i = 0; i < d.width; i++) {
			//int y1 = 0, y2 = 0;
			// g.setColor(Color.green);
			double x = (Math.PI * 2.0 * (offset + i)) / streckenlaenge;
			g.setColor(new Color((int) (140 + 115 * Math.sin(2 * x)),(int) (140 + 115 * Math.sin(x)), (int) (140 + 115 * Math
							.sin(3 * x))));
			y1 = (int) (300 + 200 * Math.sin(2 * x) * Math.cos(3 * x));
			y2 = y1 + 200;
			g.drawLine(i, y1, i, y2);
		}

		//das Fahrrad kann nun gezeichnet werden
		Bilder.zeichneBild(this, g, 0, 10, y);
		
		int og = (int) (300 + 200 * Math.sin(2 * (Math.PI * 2.0 * (offset + (10+Bilder.bildBreite[0]-d.width))) / streckenlaenge) * Math.cos(3 * (Math.PI * 2.0 * (offset + (10+Bilder.bildBreite[0]-d.width))) / streckenlaenge));
		int ug = og + 200;
		
		System.out.println("Position: " + rc.getCyclePos());		
				
		//Punkte zählen
		if(rc.getCyclePos() >= og && rc.getCyclePos() <= ug){
			System.out.println("drin" + og + " " +ug);
			points = points + rc.step;
			rc.setScore("Punkte: "+points);
		}else{
			System.out.println("draußen" + og + " " +ug);
		}
		
		//solange der Biker vor dem x wert von visible bleibt ist er sichtbar
		int visible = 0-Bilder.bildBreite[1];
		
		//Wenn der Biker noch nicht fährt oder außerhalb des Bildes ist,
		if (start == false  || x2<=visible) {
			this.x2 = d.width - Bilder.bildBreite[1] - 10;	//dann setze ihn auf diese Position
			this.start = true;
		//Wenn der Biker schon fährt, so verschiebe ihn
		} else {
			this.x2 = x2 - 2 * rc.step;	//2-mal schneller damit es aussieht als wenn er fährt
		}
		
		//Variablen zum berechnen der Position des Bikers
		double xBike = (Math.PI * 2.0 * (offset + x2)) / streckenlaenge;
		int yBike = (int) (300 + 200 * Math.sin(2 * xBike)* Math.cos(3 * xBike));
		
		//Mottorradfahrer zeichnen
		Bilder.zeichneBild(this, g, 1, x2,yBike);

		//um Probleme mit dem KeyEvents der Buttons zu vermeiden
		rc.requestFocusInWindow();
	}
	

	public void run() {
		int speed = 10;
		long now, last = System.currentTimeMillis();
		while (true) {
			now = System.currentTimeMillis();
			long delay = (long) speed - (now - last);
			last = now;
			try {
				Thread.sleep(delay);
			} catch (Exception e) {}
			
			repaint();

			// Die horizontale Position des Bildausschnitts wird um eins
			// "weitergeschoben":
			offset = offset + rc.step;
			if (offset >= streckenlaenge)
				offset = 0;
		}
	}
}

// -------------------------------------------------------------------------------------------------------

// Bilder
// -------------------------------------------------------------------------------------------------------
class Bilder {
	static int bilderzahl = 3; // anzahl von Bildern
	static Image bild[] = new Image[bilderzahl]; // speichert bilder im Array
	static int bildHoehe[] = new int[bilderzahl]; // speichert Bildhöhe im Array
	static int bildBreite[] = new int[bilderzahl]; // speichert Bildbreite im
													// Array

	// speichert die Namen der Bilder im Array
	static String bildName[] = new String[] { "bike1.gif", "motorbike2.gif",
			"motorbike1.gif" };

	static ClassLoader cl = Bilder.class.getClassLoader(); // läd alle Bilder

	// Laden aller Bilder in ein Array
	public static void init(JPanel panel) {
		for (int id = 0; id < bilderzahl; id++) {
			// läd er in die Variable img das entsprechende Bild
			Image img = panel.getToolkit().getImage(
					cl.getResource(bildName[id]));
			// läd das Image vor um Zeit zu sparen
			panel.prepareImage(img, panel);

			while ((panel.checkImage(img, panel) & ImageObserver.WIDTH) != ImageObserver.WIDTH) {
				try {
					Thread.sleep(50);
				} catch (Exception e) {
				}
			}

			bild[id] = img; 								// speichert bilder im Array
			bildHoehe[id] = bild[id].getHeight(panel); 		// speichert Bildhöhe im Array
			bildBreite[id] = bild[id].getWidth(panel); 		// speichert Bildbreite im Array
		}
	}

	// paint-Methode
	// x, y sind die Koordinaten der linken oberen Ecke!
	public static void zeichneBild(JPanel panel, Graphics g, int id, int x, int y) {
		if (bild[id] == null) { 			// wenn Bild nicht geladen
			init(panel); 					// so lade nochmals alles
		}
		g.drawImage(bild[id], x, y, panel);	// anschließend male das Bild aufs Panel
	}
}
// -------------------------------------------------------------------------------------------------------

