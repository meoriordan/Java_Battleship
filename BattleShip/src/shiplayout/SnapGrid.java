package shiplayout;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.util.HashMap;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import controller.SaveButtonListener;

public class SnapGrid extends JPanel{

	private static final ArrayList<String> LETTERS = new ArrayList<String>(Arrays.asList("A","B","C","D","E","F","G","H","I","J"));
	private static final ArrayList<String> NUMBERS = new ArrayList<String>(Arrays.asList("1","2","3","4","5","6","7","8","9","10"));
	private int squareSize;
	private ArrayList<Ship> ships;
	private Ship selected;
	private Color shipColor;
	private int xstart;
	private int ystart;
	private int xend;
	private int yend;
	private HashMap<String,ArrayList<Integer>> finalShipLocations;
	private HashMap<Point,Boolean> gridOccupy; // midpoint of box, isOccupied
	private HashMap<Integer,Integer> shipGlue; // shipNumber, boxNumber
	private HashMap<Integer,Point> gridPoints; //box,midpoint of box
	private JButton saveButton;
	private MouseAdapter ma;
	private Boolean saved;
	private HashMap<Integer,Boolean> hitMap;
	
	public SnapGrid() {
		this.setVisible(true);
		ships = new ArrayList<Ship>();

		gridOccupy = new HashMap<Point,Boolean>();
		gridPoints = new HashMap<Integer,Point>();
		shipGlue = new HashMap<Integer,Integer>();
		squareSize = Math.min(getWidth(), getHeight())/11;
		shipColor = Color.GRAY;
		Ship carrier = new Ship("Carrier",squareSize,squareSize);
		ships.add(carrier);
		shipGlue.put(0, 15);
		Ship battleship = new Ship("Battleship",squareSize,squareSize);
		ships.add(battleship);
		shipGlue.put(1, 17);
		Ship cruiser = new Ship("Cruiser",100,squareSize);
		ships.add(cruiser);
		shipGlue.put(2, 18);
		Ship submarine = new Ship("Submarine",squareSize,squareSize);
		ships.add(submarine);
		shipGlue.put(3, 19);
		Ship destroyer = new Ship("Destroyer",squareSize,squareSize);
		ships.add(destroyer);
		shipGlue.put(4, 20);
		saved = false;
		
		hitMap = new HashMap<Integer,Boolean>();
		
		for(int i = 1; i <= 100; i++) {
			hitMap.put(1, false);
		}
		
		ma = new MouseAdapter(){
			private Ship currShip;
			private Point change;
			
			@Override
			public void mousePressed(MouseEvent e) {
				currShip = selected;
				if(selected == null || !selected.contains(e.getPoint())) {
					for(Ship ship: ships) {
						if(ship.contains(e.getPoint())) {
							selected = ship;
							change = new Point(e.getX()-selected.x,e.getY()-selected.y);
							repaint();
						}
					}
				}
				else if(selected != null) {
					change = new Point(e.getX()-selected.x,e.getY()-selected.y);
				}
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				if(selected == currShip && selected != null && selected.contains(e.getPoint())) {
					selected.rotateShip();
					selected.x = selected.x - (selected.x%squareSize);
					selected.y = selected.y-(selected.y%squareSize);
				}
				
				int x = selected.x;
				while(x+selected.getWidth()>getSize().width) {
					x = x -squareSize;	
				}
				selected.x = x;
				updateBox();
				repaint();	
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if(selected != null) {
					int startx = selected.x;
					int starty = selected.y;
					int x = (e.getX() - change.x);
					int y = (e.getY() - change.y);
					if(x<squareSize) {
						x = squareSize;
					}else if(x + selected.getWidth() > getSize().width) {
						x = (int) (getSize().width - selected.getWidth());
					}		
					if(y<squareSize) {
						y = squareSize;
					}else if (y+selected.getHeight()>getSize().height) {
						y = (int) (getSize().height - selected.getHeight());
					}
					selected.setLocation(x,y);
					selected.x = selected.x - (selected.x%squareSize);
					selected.y = selected.y -(selected.y%squareSize);
					
					for(HashMap.Entry<Point,Boolean> entry: gridOccupy.entrySet()) {
						if(selected.contains(entry.getKey()) && entry.getValue()) {
							selected.x = startx;
							selected.y = starty;
						}
					}
					updateBox();
					repaint();	
				}
			}
		};
		addMouseListener(ma);
		addMouseMotionListener(ma);
		this.setLayout(new FlowLayout());
	}
	
	public void addSaveButtonListener(SaveButtonListener sbl) {
		saveButton = new JButton("Save");
		saveButton.addActionListener(sbl);
		saveButton.setBounds(xend/2, yend +10, 100, 40);
		saveButton.setVisible(true);
		add(saveButton,BorderLayout.SOUTH);
	}
	
	public void paintComponent(Graphics g) {
		setSize(getPreferredSize());
		super.paintComponent(g);
		gridPaint(g);
		shipsPaint(g);
		validate();
	}
	
	private void shipsPaint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		float strokeWidth = 3.0f;
		g2d.setStroke(new BasicStroke(strokeWidth));
		
		if(saved) {
			selected = null;
		}
		for (Ship ship: ships) {
			for(HashMap.Entry<Point,Boolean> entry:gridOccupy.entrySet()) {
				if(ship.contains(entry.getKey())) {
					gridOccupy.put(entry.getKey(),true);
				}
			}
			if(ship != selected) {
				ship.updateSize(squareSize);
				ship.setLocation(gridPoints.get(shipGlue.get(ships.indexOf(ship))));
				g2d.setColor(shipColor);
				g2d.fill(ship);
				g2d.draw(ship);
				g2d.setColor(Color.BLACK);
				g2d.drawRect((int)ship.getX(),(int)ship.getY(),(int)ship.getWidth(),(int)ship.getHeight());
			}
		}
		if(selected != null) {
			selected.updateSize(squareSize);
			selected.setLocation(gridPoints.get(shipGlue.get(ships.indexOf(selected))));
			g2d.setColor(Color.CYAN);
			g2d.fill(selected);
			g2d.draw(selected);
		}
		g2d.dispose();
		
		if(saved) {		
			g.setColor(Color.RED);
			for(HashMap.Entry<Integer,Boolean> entry:hitMap.entrySet()) {
				Boolean hit = entry.getValue();
				int box = entry.getKey();
				if(hit) {
					Point hitPoint = gridPoints.get(box);
					g.fillRect(hitPoint.x, hitPoint.y, squareSize, squareSize);
				}
			}
		}
	}
	
	private void gridPaint(Graphics g) {
		squareSize = Math.min(getWidth(), getHeight())/11;
		int letCount = 0;
		int numCount = 0;
		int fontSize = 16;//squareSize/2;
		int y = (getHeight() - (squareSize * 11))/2;
		ystart = y;
		int x = (getWidth() - (squareSize*11))/2;
		xstart = x;
		gridOccupy.clear();
		gridPoints.clear();
		int boxCount = 1;
		for(int horiz = 0; horiz<11; horiz++) {
			g.setFont(new Font("Arial",Font.BOLD,fontSize));
			x = (getWidth() - (squareSize*11))/2;
			
			for(int vert = 0; vert<11; vert++) {
				g.setColor(Color.BLACK);
				g.drawRect(x,y,squareSize,squareSize);
				if(vert == 0 && horiz >0) {
					g.drawString(NUMBERS.get(numCount), x+(squareSize/2)-fontSize/4, y+(squareSize/2)+fontSize/4);
					numCount++;
				}
				if(horiz == 0 && vert >0) {
					g.drawString(LETTERS.get(letCount), x+(squareSize/2)-fontSize/4, y+(squareSize/2)+fontSize/4);
					letCount++;
				}
				else if(vert>0 && horiz>0){
					Point p = new Point(x+squareSize/2,y+squareSize/2);
					gridOccupy.put(p,false);
					gridPoints.put(boxCount,new Point(x,y));
					boxCount +=1;
				}
				x += squareSize;
				
			}
			y += squareSize;	
		}
		xend = x;
		yend = y;
	}
	
	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		Container c = getParent();
		if (c!=null) {
			d = c.getSize();
		}
		int w = (int) (d.getWidth());
		int h = (int) (d.getHeight());
		int s = (w<h ? w:h);
		Dimension nd = new Dimension(s,s);
		return nd;
		
	}
	
	public SnapGrid getSG() {
		return this;
	}
	
	public Boolean isSaved() {
		return this.saved;
	}

	public int getSquareSize() {
		return squareSize;
	}

	public void updateBox() {
		for(HashMap.Entry<Integer,Point> entry: gridPoints.entrySet()) {
			if(selected.contains(entry.getValue())) {
				shipGlue.put(ships.indexOf(selected),entry.getKey());
				break;
			}
		}
	}
	
	public HashMap<String,ArrayList<Integer>> getFinalShipLocations(){
		
		finalShipLocations = new HashMap<String,ArrayList<Integer>>();
		for(Ship ship: ships) {
			ArrayList<Integer> listOfBoxes = new ArrayList<Integer>();
			for(HashMap.Entry<Integer,Point> entry: gridPoints.entrySet()) {
				Point p = entry.getValue();
				if(ship.contains(p)) {
					listOfBoxes.add(entry.getKey());
				}
			}
			finalShipLocations.put(ship.getShipType(), listOfBoxes);
		}		
		SnapGrid sg = getSG();
		saved = true;
		
		sg.removeMouseListener(ma);
		sg.removeMouseMotionListener(ma);
		repaint();
		return this.finalShipLocations;
	}
	
	public void addHit(int box) {
		hitMap.put(box, true);
		repaint();
	}	
}
