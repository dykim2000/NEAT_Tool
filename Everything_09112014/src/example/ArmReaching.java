package example;

public class ArmReaching {
	
	public ArmReaching( int x, int y, int len1, int len2, int tool_len ) {
		bx = x;
		by = y;
		l1 = len1;
		l2 = len2;
		l3 = tool_len;
		hand_size = 10; //3
		
		pick_x = bx + l1 + l2;
		pick_y = by;
		drop_x = bx - l1 - l2;
		drop_y = by;
		real_l2 = l2;
		holding = false;
		
		resetArm();
		resetTargetBeyond();
	}
	
	public void resetArm() {
	//reset the arm position with random angles for the joint 1 and 2
	//the joint angle will be between -90 and 90
		
		theta1 = 180 * (Math.random() - 0.5);
		theta2 = 180 * (Math.random() - 0.5);
		holding = false;
		
		setArmConfiguration();
	}
	
	private void setArmConfiguration() {
	//set the arm configuration according to the current joints angles
		
		if(theta1 > 150) theta1 = 150.0;
		//if(theta2 > 90) theta2 = 90.0;
		if(theta2 > 150) theta2 = 150.0;
		
		if(theta1 < -150) theta1 = -150.0;
		//if(theta2 < -90) theta2 = -90.0;
		if(theta2 < -150) theta2 = -150.0;
//		if( theta1 < 0 ) theta1 += 360;
//		if( theta1 > 360 ) theta1 -= 360;
//		if( theta2 < 0 ) theta1 += 360;
//		if( theta2 > 360 ) theta1 -= 360;
//		System.out.println("theta1: " + theta1 + " theta2: " + theta2);
		
//		mx = bx + (int)(Math.sin(Math.PI*theta1/180)*l1);
//		my = by - (int)(Math.cos(Math.PI*theta1/180)*l1);
//		ex = mx + (int)(Math.cos(Math.PI*theta2/180)*l2);
//		ey = my - (int)(Math.sin(Math.PI*theta2/180)*l2);
		
		mx = bx + (int)(Math.sin(Math.PI*theta1/180)*l1);
		my = by - (int)(Math.cos(Math.PI*theta1/180)*l1);
		int arm2 = l2;
		if( holding ) arm2 += l3;
		ex = mx + (int)(Math.sin(Math.PI*theta2/180)*arm2);
		ey = my - (int)(Math.cos(Math.PI*theta2/180)*arm2);
	}
	
	//public double move( double angle1, double angle2 ) {
	public void move( double angle1, double angle2 ) {
		//double fitness = 0.0;
		
		theta1 += angle1;
		theta2 += angle2;
//		if(theta1 > 360) theta1 -= 360;
//		if(theta2 > 360) theta2 -= 360;
//		if(theta1 < -360) theta1 += 360;
//		if(theta2 < -360) theta2 += 360;
		

		setArmConfiguration();
		
		if( isPick() ) {
			pick();
		}
		
//		if( isDrop() ) {
//			drop();
//		}
		
//		if( hasReach() )
//		{
//			//System.out.println("GOT IT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//			//fitness = 2;
//			fitness = 2;
//			//fitness = 300000.0;
//			resetTarget();
//		} 
//		//else if(theta1 == 90 || theta1 == -90 || theta2 == 90 || theta2 == -90) {
//			//fitness = 0;
//		//} 
//		else {
//			//double d = Math.pow((ex-tx), 2) + Math.pow((ey-ty), 2);
//			//fitness = 1/Math.sqrt(d) * 10000;
//		}
		//System.out.println("fitness: " + (int)fitness);
		//return fitness;
	}
	
	public double getDist2Target() {
		double d = 0.0;
		
		d = Math.pow((ex-tx), 2) + Math.pow((ey-ty), 2);
		d = Math.sqrt(d);
		
		return d;
	}

	
	public void resetTarget() {
		boolean valid = false;
		while( !valid )
		{
			int a = (int)(2*(l1+l2)*Math.random())-(l1+l2);
			int b = (int)(2*(l1+l2)*Math.random())-(l1+l2);
			tx = bx + a;
			ty = by + b;
			if( (tx-bx)*(tx-bx) + (ty-by)*(ty-by) < (l1+l2)*(l1+l2) ) { // reachable
				if( l2 > l1 && ((tx-bx)*(tx-bx) + (ty-by)*(ty-by) > (l2 - l1)*(l2 - l1)) )
					valid = true;
			}
			if( by - ty < 10 ) // no target on baseline
				valid = false;
		}
	}
	
	public void resetTargetBeyond() {
		boolean valid = false;
		while( !valid )
		{
			int a = (int)(2*(l1+l2+l3)*Math.random())-(l1+l2+l3);
			int b = (int)(2*(l1+l2+l3)*Math.random())-(l1+l2+l3);
			tx = bx + a;
			ty = by + b;
			if( (tx-bx)*(tx-bx) + (ty-by)*(ty-by) < (l1+l2+l3)*(l1+l2+l3) ) { // reachable
				if( l2 > l1 && ((tx-bx)*(tx-bx) + (ty-by)*(ty-by) > (l2 - l1)*(l2 - l1)) )
					valid = true;
			}
			if( by - ty < 10 ) // no target on baseline
				valid = false;
		}
	}
	
	public void resetTool() {
		boolean valid = false;
		int x = 0;
		int y = 0;
		while( !valid )
		{
			int a = (int)(2*(l1+l2)*Math.random())-(l1+l2);
			int b = (int)(2*(l1+l2)*Math.random())-(l1+l2);
			x = bx + a;
			y = by + b;
			if( (x-bx)*(x-bx) + (y-by)*(y-by) < (l1+l2)*(l1+l2) ) { // reachable
				if( l2 > l1 && ((tx-bx)*(tx-bx) + (ty-by)*(ty-by) > (l2 - l1)*(l2 - l1)) )
					valid = true;
			}
			if( by - y < 20 ) // no target on baseline
				valid = false;
		}
		//pick_x = bx + l1 + l2-50;
		//pick_y = by+30;
		pick_x = x;
		pick_y = y;
	}
	
	public void setTarget(int x, int y) {
		tx = x;
		ty = y;
	}
	
	public boolean hasReach() {
//		int x = tx + 3;
//		int y = ty + 3;
		int x = tx;
		int y = ty;
		
		if( Math.abs(ex-x) < hand_size && Math.abs(ey-y) < hand_size )
			return true;
		return false;
	}
	
	public boolean isPick() {
		int x = pick_x;
		int y = pick_y;
		
		if( Math.abs(ex-x) < hand_size && Math.abs(ey-y) < hand_size )
			return true;
		return false;
	}
	
	public void pick() {
		holding = true;
	}
	
	public boolean isDrop() {
		int x = drop_x;
		int y = drop_y;
		
		if( Math.abs(ex-x) < hand_size && Math.abs(ey-y) < hand_size )
			return true;
		return false;
	}
	
	public void drop() {
		holding = false;
	}

	public int bx, by;
	public int mx, my;
	public int ex, ey;
	public int tx, ty;
	public int l1, l2, l3;
	public int hand_size; //radius of the hand size
	public double theta1, theta2;
	public final int box_len = 400;
	public final int box_wid = 400;
	public int pick_x, pick_y;
	public int drop_x, drop_y;
	public int real_l2;
	boolean holding;
}
