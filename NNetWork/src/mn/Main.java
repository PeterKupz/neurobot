package mn;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JFrame {

	NetWork netWork;

	static ArrayList<Bot> bots = new ArrayList();
	static Areal areal;
	Displayer disp;

	public static double activation(double d) {
		
		return 1/(1 + Math.exp(-d));
		
//		return 2 * Math.atan(d) / Math.PI;
	}

	public static void main(String[] args) {
		Main main = new Main();
	}

	void train() {

	}

	static BufferedImage nv;

	public Main() {

//		netWork = new NetWork(6, 5, 2);
//		netWork.llist.get(0).outMatrix();

//		double[] ddd = { 1, 0.8, 0.6, 0.4, 0.2, 0 };

//		for (int i = 0; i < ddd.length; i++) {
//			ddd[i] = Math.random();
//		}

//		double[] g = netWork.calculate(ddd);

		// add(new Displayer());

		try {
			nv = ImageIO.read(new File("1.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// areal = new Areal("ptn.jpg");
		areal = new Areal(700, 700);

		for (int i = 0; i < 50; i++) {
			bots.add(new Bot(areal));
		}

		Timer timer = new Timer();

		TimerTask task = new TimerTask() {
			long time = System.currentTimeMillis();

			@Override
			public void run() {

				ArrayList<Bot> bs = new ArrayList<>();
				bs.addAll(bots);

				if (getAliveBots() < 8) {
					areal.renew();
					System.out.println(
							"Клонирование;" + System.currentTimeMillis() + ";" + (System.currentTimeMillis() - time));
					time = System.currentTimeMillis();
					// Bot bot = getAliveBot();

					for (Bot bot : bs) {
						if (!bot.dead) {
							for (int i = 0; i < 50; i++) {
								bots.add(new Bot(bot, 1));
							}
						}
					}
				}

				for (Bot bot : bs) {
					bot.step(30);
					// System.out.println(bot);
				}
			}

			private Bot getAliveBot() {
				for (Bot b : bots) {
					if (!b.dead) {
						return b;
					}
				}
				return new Bot(areal);
			}

			private int getAliveBots() {
				int i = 0;
				for (Bot b : bots) {
					i += b.dead ? 0 : 1;
				}
				return i;
			}
		};

		timer.schedule(task, 0, 4);

		Timer p_timer = new Timer();

		TimerTask p_task = new TimerTask() {
			@Override
			public void run() {
				repaint();
			}
		};

		p_timer.schedule(p_task, 0, 40);

		JPanel mp = new JPanel();
		BoxLayout boxLayout = new BoxLayout(mp, BoxLayout.X_AXIS);
		mp.setLayout(boxLayout);

		Field field = new Field();
		field.setBackground(Color.WHITE);
		mp.add(field);
		disp = new Displayer();
		mp.add(disp);

		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					disp.setcurentBot(bots.get(bots.size() - 1));
				}
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					disp.setcurentBot(bots.get(bots.size() - 2));
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});

		add(mp);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		// setSize(800, 400);
//		g = netWork.calculate(ddd);
	}

	class Field extends JPanel {

		public Field() {
			setBackground(Color.white);
			setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {
				}

				@Override
				public void mousePressed(MouseEvent e) {
				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseClicked(MouseEvent e) {
					// System.out.println(e.getX() + " " + e.getY());
					double d = 2000;
					Bot bt = null;
					for (Bot b : bots) {
						if (getDist(b, e) < d) {
							d = getDist(b, e);
							bt = b;
						}
					}
					disp.curentBot = bt;
				}
			});
		}

		protected double getDist(Bot b, MouseEvent e) {
			double dx = b.x - e.getX();
			double dy = b.y - e.getY();
			return Math.sqrt(dx * dx + dy * dy);
		}

		@Override
		protected void paintComponent(Graphics g) {

			Graphics2D g2d = (Graphics2D) g;

			g2d.drawRect(0, 0, areal.x(), areal.y());

//			 paintField(g2d);

			ArrayList<Bot> bs = new ArrayList<>();
			bs.addAll(bots);

			for (Bot b : bs) {
				int radius = (int) Math.pow(b.mass * 2, 0.5);
				int xl = (int) Math.round(b.x);
				int rl = (int) Math.round(b.y);
				if (b.dead) {
					g2d.setColor(Color.LIGHT_GRAY);
				} else {
					g2d.setColor(b.getBotColor());
				}
				g2d.fillOval(xl - radius / 2, rl - radius / 2, radius, radius);
				if (b == disp.curentBot) {
					g2d.setColor(Color.RED);
					g2d.drawOval(xl - radius, rl - radius, radius * 2, radius * 2);
				}
//				 g2d.drawImage(nv.getScaledInstance(50, 50, BufferedImage.SCALE_DEFAULT),
//				 xl-25, rl-25, this);
				g2d.drawLine(xl, rl, b.getSX(), b.getSY());

//				 g2d.drawString(""+Math.round(b.mass*100)*0.01, (int)b.x, (int)b.y);

				if (!b.dead) {
					g2d.setColor(Color.yellow);
					if (b.prey != null) {
						g2d.drawLine(xl, rl, (int) b.prey.x, (int) b.prey.y);
					}
				}
			}

		}

		private void paintField(Graphics2D g2d) {
			for (int i = 0; i < areal.x(); i++) {
				for (int j = 0; j < areal.y(); j++) {
					int j2 = Math.min((int) (400 * areal.food[i][j]), 255);
					g2d.setColor(new Color(j2, j2, j2));
					g2d.drawLine(i, j, i, j);
				}
			}
		}
	}

	class Displayer extends JPanel {

		DecimalFormat decimalFormat = new DecimalFormat("##.000");

		public Displayer() {
			setBackground(Color.WHITE);
		}

		int radius = 20;
		Bot curentBot = bots.get(0);

		void setcurentBot(Bot curentBot) {
			this.curentBot = curentBot;
		}

		int xC(Layer l) {
			return 100 + l.layerPos * 250;
		}

		int yC(Layer l, int p) {
			return 600 - (l.getSize() * 50) / 2 + 50 * p;
		}

		@Override
		protected void paintComponent(Graphics g) {
			// TODO Auto-generated method stub
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2d.setRenderingHints(rh);

//			 рисуем линии
			NetWork netWork2 = curentBot.netWork;
			for (Layer l : netWork2.llist) {
				if (l.upLayer != null) {
					for (int i = 0; i < l.getSize(); i++) {
						for (int j = 0; j < l.upLayer.getSize(); j++) {
							if (l.weightMatrix[i][j] > 0) {
								g2d.setColor(Color.RED);
							} else {
								g2d.setColor(Color.BLUE);
							}
							g2d.setStroke(new BasicStroke(Math.abs(Math.round(4 * l.weightMatrix[i][j]) + 0f)));
							g2d.drawLine(xC(l), yC(l, i), xC(l.upLayer), yC(l.upLayer, j));
						}
					}
				}
			}
			// рисуем вершины

			for (Layer l : netWork2.llist) {
				for (int i = 0; i < l.getSize(); i++) {
					g2d.setColor(Color.getHSBColor(Math.round(20 * l.getValue(i)) * 0.004f, 1, 1));
					g2d.fillOval(xC(l) - radius / 2, yC(l, i) - radius / 2, radius, radius);
					g2d.setColor(Color.black);
					g2d.setFont(new Font("Arial", Font.BOLD, 30));
					g2d.drawString(decimalFormat.format(l.getValue(i)), xC(l) + radius, yC(l, i));

				}
			}
		}
	}

}
