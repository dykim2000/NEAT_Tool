package example;

import javax.swing.*; 

import java.awt.*; 
import java.util.ArrayList;

class Jframe extends JFrame{
	
	public Jframe(){
//		this.setSize(400,300); 
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setVisible(true); 
	}
	
	public Jframe( int x, int y, int boxlen, int boxwid ){
		bx = x;
		by = y;
		this.setSize(boxlen,boxwid);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		//setVisible(false);
	}
	
	@Override 
	public void paint(Graphics g) { 
		super.paint(g); 
		
		// draw the range
		g.setColor(Color.black);
		g.drawArc(bx-(l1+l2), by-(l1+l2), 2*(l1+l2), 2*(l1+l2), 0, 180);
		g.drawArc(bx-(l1+l2+l3), by-(l1+l2+l3), 2*(l1+l2+l3), 2*(l1+l2+l3), 0, 180);
		
		Graphics2D g2 = (Graphics2D) g;
	    g2.setStroke(new BasicStroke(3));
	    
		// draw base point
		g.drawRect(bx, by, 2, 2);
		g.fillRect(bx, by, 2, 2);
		
		// draw arm 1
		g.setColor(Color.blue);
		//g.drawLine(bx, by, mx, my);
		g.drawRect(mx, my, 2, 2);
		g.fillRect(mx, my, 2, 2);
		
	    g2.drawLine(bx, by, mx, my);   //thick
		
		// draw arm 2
		g.setColor(Color.red);
		g.drawLine(mx, my, ex, ey);
		g.drawRect(ex, ey, 2, 2);
		g.fillRect(ex, ey, 2, 2);
		//if( !holding )
			g.drawOval(ex-hand_size, ey-hand_size, 2*hand_size, 2*hand_size);
		
		// draw the target
		g.setColor(Color.black);
		g.drawRect(tx-3, ty-3, 6, 6);
		g.fillRect(tx-3, ty-3, 6, 6);
		
		if( !holding ) {
			g.setColor(Color.green);
			g.drawLine(pick_x, pick_y, pick_x+l3, pick_y);
			g.drawRect(pick_x, pick_y, 2, 2);
			g.fillRect(pick_x, pick_y, 2, 2);
		}
	}
	
	public int bx, by, mx, my, ex, ey, tx, ty, hand_size, l1, l2, l3, pick_x, pick_y;
	boolean holding;
}

public class Display { 
	public Display(){}
	
	public void init( int x, int y, int boxlen, int boxwid ) {
		jf = new Jframe( x, y, boxlen, boxwid );
	}
	
	public Display( int x, int y, int boxlen, int boxwid ) {
		jf = new Jframe( x, y, boxlen, boxwid );
	}
	
	public void nextFrame( ArmReaching arm ) throws InterruptedException {
		jf.mx = arm.mx;
		jf.my = arm.my;
		jf.ex = arm.ex;
		jf.ey = arm.ey;
		jf.tx = arm.tx;
		jf.ty = arm.ty;
		jf.hand_size = arm.hand_size;
		jf.l1 = arm.l1;
		jf.l2 = arm.l2;
		jf.l3 = arm.l3;
		jf.holding = arm.holding;
		jf.pick_x = arm.pick_x;
		jf.pick_y = arm.pick_y;
		
		jf.repaint();
		
		Thread.sleep(5);
		//Thread.sleep(40);
	}
	
	private Jframe jf;
}
