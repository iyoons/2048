import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
 
class Tile {
	private int number;
    private boolean Merged;
    
    Tile(int n) {
    	number = n;
    }
 
    int getNumber() {
        return number;
    }
    
    boolean getMerged() {
        return Merged;
    }
    
    void setNumber(int n) {
        number = n;
    }
 
    void setMerged(boolean m) {
        Merged = m;
    }
 
    boolean canMerge(Tile t) {
    	if (t != null && !Merged && !t.Merged && t.getNumber() == number)
    		return true;
    	else
    		return false;
    }
}

class RoundedButton extends JButton {
	public RoundedButton(String s) { 
		super(s); 
		decorate(); 
	} 
	
	protected void decorate() { 
		setBorderPainted(false); 
		setOpaque(false); 
	} 
	
	public void paintComponent(Graphics g) {
		int width = getWidth(); 
		int height = getHeight(); 
		
		Graphics2D g2 = (Graphics2D) g; 
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
		
		g2.setColor(new Color(0xBBADA0));
		g2.fillRoundRect(0, 0, width, height, 10, 10);
		
		FontMetrics fm = g2.getFontMetrics();
		int a = fm.getAscent();
        int d = fm.getDescent();
		
		String s = getText();
		int posX = (width - (int)(fm.stringWidth(s) * 1.4)) / 2;
        int posY = a + (height - (a + d)) / 2;
        
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("SansSerif", Font.BOLD, 17));
		g2.drawString(s, posX, posY);
		g2.dispose();
		
		super.paintComponent(g);
	}
}

class MyThread implements Runnable {
	private JLabel label;

	public MyThread(JLabel label) {
		this.label = label;
	}
	
	public void run() {
		while(true) {
			try {
				Thread.sleep(500);
				break;
			}
			catch(InterruptedException e) {
				return;
			}
		}
		
		label.setText("");
	}
}

public class Game2048 extends JPanel {
	static int max;
    static int score;
    static int newS;
    static int goal;

    final String Moment[] = { "start", "ongoing", "clear", "over" };
    final int diff[] = { 1024, 2048, 4096 };
    
    final Color borderColor = new Color(0xBBADA0);
    
    final Color[] numColor = { new Color(0x786965), new Color(0xFFFFFF) };
    
    final Color[] tileColor = {
    	new Color(0xCDC1B4), new Color(0xF2E4D7), new Color(0xFFF4D3),
    	new Color(0xFEB982), new Color(0xEF926D), new Color(0xF7786B), new Color(0xF16043),
        new Color(0xF9DA8D), new Color(0xF6DD65), new Color(0xFFD835), new Color(0xFFBC43) };
    
    private RoundedButton Lv1 = new RoundedButton("Lv1 : 1024");
    private RoundedButton Lv2 = new RoundedButton("Lv2 : 2048");
    private RoundedButton Lv3 = new RoundedButton("Lv3 : 4096");
    private JLabel l = new JLabel("0");
    private JLabel addS = new JLabel();
    
    private Tile[][] Tiles;
    private int NT = 4;
    private String now = Moment[0];
    private boolean Movable;
 
    public Game2048() {
        setBackground(new Color(0xFAF8EF));
        setFont(new Font("SansSerif", Font.BOLD, 48));
        setFocusable(true);
 
        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
            	switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    moveUp();
                    break;
                case KeyEvent.VK_LEFT:
                    moveLeft();
                    break;
                case KeyEvent.VK_DOWN:
                    moveDown();
                    break;
                case KeyEvent.VK_RIGHT:
                    moveRight();
                    break;
                }
                repaint();
            }
        });
    }
 
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 
        drawBoard(g2);
    }
 
    void startGame() {
        if (now != Moment[1]) {
            score = 0;
            max = 0;
            now = Moment[1];
            remove(Lv1);
            remove(Lv2);
            remove(Lv3);
            Tiles = new Tile[NT][NT];
            makeTile();
            makeTile();
        }
    }
 
    void drawBoard(Graphics2D g) {
        g.setColor(borderColor);
        g.fillRoundRect(100, 100, 499, 499, 15, 15);
 
        if (now == Moment[1]) {
            for (int h = 0; h < NT; h++) {
                for (int w = 0; w < NT; w++) {
                    if (Tiles[h][w] == null) {
                        g.setColor(tileColor[0]);
                        g.fillRoundRect(115 + w * 121, 115 + h * 121, 106, 106, 7, 7);
                    } 
                    else
                        drawTile(g, h, w);
                }
            }
            
            g.setFont(new Font("SansSerif", Font.BOLD + Font.ITALIC, 90));
            g.setColor(tileColor[10]);
            g.drawString("2", 110, 90);
            g.setColor(tileColor[7]);
            g.drawString("0", 160, 90);
            g.setColor(tileColor[3]);
            g.drawString("4", 210, 90);
            g.setColor(tileColor[4]);
            g.drawString("8", 260, 90);
            
            String not = "Join the numbers and get to the " + goal + " tile!";
            g.setColor(tileColor[0]);
            g.setFont(new Font("SansSerif", Font.BOLD, 25));
            g.drawString(not, 106, 635);
            
            g.setColor(borderColor);
            g.fillRoundRect(480, 40, 120, 50, 7, 7);
            g.setColor(tileColor[1]);
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.drawString("SCORE", 518, 57);
            String s = Integer.toString(score);
            l.setBounds(490, 25, 100, 100);
            l.setForeground(Color.WHITE);
            l.setFont(new Font("SansSerif", Font.BOLD, 30));
            l.setText(s);
            l.setHorizontalAlignment(JLabel.CENTER);
            add(l);
            
            s = "+" + Integer.toString(newS);
            addS.setBounds(610, 24, 100, 100);
           
            if (newS != 0) {
            	addS.setForeground(numColor[0].brighter());
            	addS.setFont(new Font("SansSerif", Font.PLAIN, 20));
            	addS.setText(s);
            	add(addS);
            	MyThread runnable = new MyThread(addS);
                Thread th = new Thread(runnable);
                th.start();
                newS = 0;
            }
        } 
        else {
            g.setColor(new Color(0xFFEBCD));
            g.fillRoundRect(115, 115, 469, 469, 7, 7);
 
            g.setFont(new Font("SansSerif", Font.ITALIC, 50));
            g.setColor(borderColor.darker());
            g.drawString("Game", 280, 200);
      
            g.setFont(new Font("SansSerif", Font.BOLD, 128));
            g.setColor(tileColor[10]);
            g.drawString("2", 210, 310);
            g.setColor(tileColor[7]);
            g.drawString("0", 280, 310);
            g.setColor(tileColor[3]);
            g.drawString("4", 350, 310);
            g.setColor(tileColor[4]);
            g.drawString("8", 420, 310);
 
            g.setFont(new Font("Monospaced", Font.BOLD, 40));
 
            if (now == Moment[2]) {
            	remove(l);
            	remove(addS);
            	newS = 0;
            	g.setColor(Color.GREEN);
                g.drawString("Clear!!!", 270, 400);
            }
            else if (now == Moment[3]) {
            	remove(l);
            	remove(addS);
            	newS = 0;
            	g.setColor(Color.RED);
            	g.drawString("Game Over...", 230, 400);
            }

            Lv1.setSize(120, 40);
            Lv1.setLocation(150, 460);
            Lv1.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				 goal = diff[0];
    				 requestFocus();
    				 startGame();
    	             repaint();
    			}
    		});
            add(Lv1);
            
            Lv2.setSize(120, 40);
            Lv2.setLocation(290, 460);
            Lv2.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				 goal = diff[1];
    				 requestFocus();
    				 startGame();
    	             repaint();
    			}
    		});
            add(Lv2);
            
            Lv3.setSize(120, 40);
            Lv3.setLocation(430, 460);
            Lv3.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				 goal = diff[2];
    				 requestFocus();
    				 startGame();
    	             repaint();
    			}
    		});
            add(Lv3);
            
            g.setFont(new Font("Monospaced", Font.PLAIN, 20));
            
            g.setColor(borderColor);
            g.drawString("(Use ��, ��, ��, ��)", 245, 530);
        }
    }
    
    boolean moveUp() {
        return moveTile(0, -1);
    }
 
    boolean moveLeft() {
        return moveTile(-1, 0);
    }
    
    boolean moveDown() {
        return moveTile(0, 1);
    }
    
    boolean moveRight() {
        return moveTile(1, 0);
    }
 
    void initMerged() {
    	for (int i = 0; i < NT; i++) {
    		for (int j = 0; j < NT; j++) {
    			Tile t = Tiles[i][j];
    			if (t != null)
    				t.setMerged(false);
    		}
    	}
    }
 
    boolean canMove() {
    	boolean rest;
        Movable = true;
        rest = moveUp() || moveLeft() || moveDown() || moveRight();
        Movable = false;
        return rest;
    }
 
    void drawTile(Graphics2D g, int h, int w) {
        int number = Tiles[h][w].getNumber();
 
        g.setColor(tileColor[(int)(Math.log(number) / Math.log(2))]);
        g.fillRoundRect(115 + w * 121, 115 + h * 121, 106, 106, 7, 7);
 
        FontMetrics fm = g.getFontMetrics();
        int a = fm.getAscent();
        int d = fm.getDescent();
 
        String s = Integer.toString(number);
        int posX = 115 + w * 121 + (106 - fm.stringWidth(s)) / 2;
        int posY = 115 + h * 121 + (a + (106 - (a + d)) / 2);
 
        g.setColor(number <= 4 ? numColor[0] : numColor[1]);
        g.drawString(s, posX, posY);
    }
 
    private void makeTile() {
    	int loc, w, h;
        do {
        	loc = (int)(Math.random() * (NT * NT));
            w = loc % NT;
            h = loc / NT;
        } while (Tiles[h][w] != null);
 
        int number = (int)(Math.random() * 10) <= 6 ? 2 : 4;
        Tiles[h][w] = new Tile(number);
    }
 
    private boolean moveTile(int addW, int addH) {
    	boolean Moved = false;
        int first;
        
        if (addW < 0 || addH < 0)
        	first = 0;
        else
        	first = NT * NT - 1;
        
        for (int i = 0; i < NT * NT; i++) {
            int tileNum = Math.abs(first - i);
            
 
            int oldW = tileNum % NT;
            int oldH = tileNum / NT;
 
            if (Tiles[oldH][oldW] == null)
                continue;
 
            int newW = oldW + addW;
            int newH = oldH + addH;
 
            while (newW >= 0 && newW < NT && newH >= 0 && newH < NT) {
 
            	Tile oldT = Tiles[oldH][oldW];
                Tile newT = Tiles[newH][newW];
 
                if (newT == null) {
 
                    if (Movable)
                        return true;
 
                    Tiles[oldH][oldW] = null;
                    Tiles[newH][newW] = oldT;
                    oldH = newH;
                    oldW = newW;
                    newH += addH;
                    newW += addW;
                    Moved = true;
 
                }
                else if (newT.canMerge(oldT)) {
 
                    if (Movable)
                        return true;
                    
                    newT.setNumber(2 * newT.getNumber());
                    newT.setMerged(true);
                    
                    if (newT.getNumber() > max)
                        max = newT.getNumber();
                    
                    score += newT.getNumber();
                    newS += newT.getNumber();
                    
                    Tiles[oldH][oldW] = null;
                    Moved = true;
                    break;
                }
                else
                	break;
            }
        }
 
        if (Moved) {
        	if (max == goal)
                now = Moment[2];
            else if (max < goal) {
                makeTile();
                initMerged();
                if (!canMove())
                    now = Moment[3];
            } 
        }
        
        return Moved;
    }
 
    public static void main(String[] args) {
    	JFrame f = new JFrame();
    	f.setTitle("2048 ����");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Container contentPane = f.getContentPane();
        contentPane.setLayout(new BorderLayout());
        
        f.add(new Game2048(), BorderLayout.CENTER);
        
        f.setResizable(false);
        f.setSize(710,740);
        f.setLocation(400,50);
        f.setVisible(true);
    }
}