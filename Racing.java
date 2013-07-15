import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Racing extends JFrame {

        /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		int width=600;  // Breite des Fensters
        int height=800; // H�he des Fensters
        
        RacingPanel rpanel;
        boolean isStarted = false;
        int score = 0;
        
        JLabel scoreLabel;
        
        int step=0; // Geschwindigkeit der Animation

        public static void main(String[] args) { @SuppressWarnings("unused")
		Racing wnd = new Racing(); }

        public Racing() {
          setSize(width,height); 
          setTitle("Das ultimativ langweilige Rennspiel");
          setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

          rpanel=new RacingPanel(this);
          scoreLabel = new JLabel("Score: "+score);
          Container cpane=getContentPane();
          cpane.setLayout(new BorderLayout());
          cpane.add(rpanel,BorderLayout.CENTER);
          cpane.add(scoreLabel,BorderLayout.NORTH);
          JButton startbutton = new JButton("Start");
          cpane.add(startbutton,BorderLayout.WEST);
          startbutton.resetKeyboardActions();

          addKeyListener(new KeyAdapter() {
                           public void keyPressed(KeyEvent e) {
                               if (e.getKeyCode()==KeyEvent.VK_DOWN) {
                                   // Aktion, wenn "Pfeil nach unten" gedr�ckt
                                   rpanel.yFahrrad=rpanel.yFahrrad+4; }
                               if (e.getKeyCode()==KeyEvent.VK_UP) {
                                   // Aktion, wenn "Pfeil nach oben" gedr�ckt
                                  rpanel.yFahrrad=rpanel.yFahrrad-4; }
                           } } );

          startbutton.addActionListener(new ActionListener()
           { public void actionPerformed(ActionEvent e){
                   if (step==0) { step=1; ((JButton)e.getSource()).setText("Stop"); isStarted = true;}
                    else { step=0; ((JButton)e.getSource()).setText("Start"); isStarted = false;} } } );
                              
          pack();
          setVisible(true);

          Thread t=new Thread(rpanel);
          t.start(); }
}

class RacingPanel extends JPanel implements Runnable {
      /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Racing rc;
      int offset=0;
      int streckenlaenge=8000;
      int yFahrrad =300; // die vertikale Position des Fahrradfahrers
      int xFahrrad = 10;
      int yMotorrad = 300;
      int xMotorrad = 300;
      
      boolean MotorradIsVisible = false;
      boolean sameX;
      boolean collison = false;
      boolean insideStreet;
      int y1=0,y2=0;
      

      RacingPanel(Racing racing){
          Bilder.init(this);
    	  rc=racing;
          setPreferredSize(new Dimension(rc.width,rc.height)); }

  
      public void paint(Graphics g) {
         g.setColor(Color.blue);
         Dimension d=getSize();
         g.fillRect(0,0,d.width,d.height);
         for(int i=0; i<d.width; i++) {
           double x= (Math.PI*2.0*(offset+i))/streckenlaenge;
           g.setColor(new Color((int)(140+115*Math.sin(2*x)),
        		   				(int)(140+115*Math.sin(x)),
        		   				(int)(140+115*Math.sin(3*x))));
           g.setColor(Color.green);
           y1=(int) (300+200*Math.sin(2*x)*Math.cos(3*x));
           y2= y1 +200;
           g.drawLine(i,y1,i,y2); }

         double xanfang = (Math.PI*2.0*(offset+Bilder.bildBreite[0]+10))/streckenlaenge;
         int yanfangOben = (int) (300+200*Math.sin(2*xanfang)*Math.cos(3*xanfang));
         int yanfangUnten = yanfangOben+200;
         g.setColor(Color.black);
         
         // das Fahrrad kann nun gezeichnet werden
         Bilder.zeichneBild(this,g,0,xFahrrad,yFahrrad);
         
         Point fahrradUnten = new Point(xFahrrad+Bilder.bildBreite[0], yFahrrad+Bilder.bildHoehe[0]);
         Point fahrradOben = new Point(xFahrrad+Bilder.bildBreite[0], yFahrrad);
         
         MotorradIsVisible = xMotorrad >= 5;
         if (!MotorradIsVisible) {
        	 yMotorrad = (int) (y1+ Math.random()*200 - Bilder.bildHoehe[1]);
        	 xMotorrad = d.width;
         }else{
			xMotorrad = d.width -(offset % d.width);
			
		}
         Bilder.zeichneBild(this,g,1,xMotorrad,yMotorrad);

         Point MotorradOben = new Point(xMotorrad, yMotorrad);
         Point MotorradUnten = new Point(xMotorrad, yMotorrad+Bilder.bildHoehe[1]);
            
         sameX= fahrradOben.x > MotorradOben.x; 
         
         collison = (((fahrradOben.y < MotorradOben.y) && (fahrradUnten.y > MotorradOben.y))||( (fahrradUnten.y > MotorradUnten.y) && (fahrradOben.y < MotorradUnten.y )));
         
         insideStreet = ((fahrradUnten.y < yanfangUnten)&&( fahrradUnten.y > yanfangOben));
         
         if (rc.isStarted&&insideStreet)
        	 rc.score++;
         if (collison&&sameX&&rc.isStarted)
         	 rc.score = 0;
         
         rc.scoreLabel.setText("Score: "+rc.score);
         // um Probleme mit dem KeyEvents der Buttons zu vermeiden
         rc.requestFocusInWindow(); }

      public void run() {
         int speed=10;
         long now,last=System.currentTimeMillis();
         while(true) {
            now=System.currentTimeMillis();
            long delay=(long)speed-(now-last);
            last=now;
            try { Thread.sleep(delay); } catch (Exception e)  { }
            repaint();

            // Die horizontale Position des Bildausschnitts wird um eins "weitergeschoben":
            offset=offset+rc.step;
            if (offset>=streckenlaenge) offset=0; }
      }    
}


class Bilder
{
    static int bilderzahl=3;
    static Image bild[]=new Image[bilderzahl];
    static int bildHoehe[]= new int[bilderzahl];
    static int bildBreite[]= new int[bilderzahl];
    static String bildName[] = new String[]{"bike1.gif", "motorbike2.gif", "motorbike1.gif"};
    static ClassLoader cl = Bilder.class.getClassLoader();
	
    public static void init(JPanel panel)
     { for(int id=0; id<bilderzahl; id++) {    	
        Image img=panel.getToolkit().getImage(cl.getResource(bildName[id]));
        panel.prepareImage(img, panel);
        while ((panel.checkImage(img, panel) & ImageObserver.WIDTH) != ImageObserver.WIDTH) {
              try { Thread.sleep(50); } catch(Exception e) { } }
        bild[id]=img;
        bildHoehe[id]=bild[id].getHeight(panel);
        bildBreite[id]=bild[id].getWidth(panel); }
     }
    
    /* x, y sind die Koordinaten der linken oberen Ecke! */
    public static void zeichneBild(JPanel panel,  Graphics g, int id, int x, int y) {
       if (bild[id]==null) init(panel);
       g.drawImage(bild[id],x,y, panel); }
}





