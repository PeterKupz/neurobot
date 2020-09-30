package mn;

import java.awt.Color;

public class Bot {
	NetWork netWork;
	Color botColor;
	Bot prey;
	Bot parent;
	long lifetime = 1;
	int genertion;

	double distance(Bot bot) {
		double dx = x - bot.x;
		double dy = y - bot.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	void killBot(Bot bot) {
		if (Math.random() < (mass * energy) / (bot.mass * bot.energy)) {
			x_v = 0;
			y_v = 0;
			bot.dead = true;
			double ff = 0.4 * bot.mass;
			while (bot.mass > ff) {
				bot.returnMass(0.01);
			}
			energy += bot.energy;
			mass += bot.mass;
			bot.energy = 0;
			bot.mass = 0;
		} else {
			dead = true;
		}
	}

	void killBot() {

		if (!prey.dead) {
			if (prey.mass < mass) {
				this.dead = true;
			} else {
				prey.dead = true;
			}
		}
		// } else {

		// }
	}

	@Override
	public String toString() {
		return genertion + " " + x + " " + y;
	}

	public Bot(Areal areal) {
		botColor = new Color((int) (200000 * Math.random()));
		genertion = 1;
		netWork = new NetWork(1.1d, 8, 5, 3);
		x = areal.food.length * Math.random();
		y = areal.food[0].length * Math.random();
		x_v = 20 * (Math.random() - .5);
		y_v = 20 * (Math.random() - .5);
		mass = 30 + Math.random();
		energy = 30;
	}

	/**
	 * Конструктор "ребенка"
	 * 
	 * @param bot
	 */
	public Bot(Bot bot, double cld) {

		this(Main.areal);
		parent = bot;
		mass = bot.mass * cld;
		botColor = nextColor(bot);
		bot.mass = bot.mass * (1 - cld);
		energy = bot.energy * cld;
		bot.energy = bot.energy * (1 - cld);
		genertion = bot.genertion + 1;
		x_v = bot.x_v + (.5 - Math.random());
		y_v = bot.y_v + (.5 - Math.random());
		x = bot.x + 10;
		y = bot.y + 10;

		for (int i = 1; i < this.netWork.llist.size(); i++) {
			Layer layer = bot.netWork.llist.get(i);
			for (int j = 0; j < layer.weightMatrix.length; j++) {
				double[] w = layer.weightMatrix[j];
				for (int k = 0; k < w.length; k++) {
					netWork.llist.get(i).weightMatrix[j][k] = w[k] + .01 * (Math.random() - 0.5);
				}
			}
		}
	}

	public Bot(Bot bot, int t) {
		this(Main.areal);
		botColor = nextColor(bot);
		for (int i = 1; i < this.netWork.llist.size(); i++) {
			Layer layer = bot.netWork.llist.get(i);
			for (int j = 0; j < layer.weightMatrix.length; j++) {
				double[] w = layer.weightMatrix[j];
				for (int k = 0; k < w.length; k++) {
					this.netWork.llist.get(i).weightMatrix[j][k] = w[k] + t * .3 * (Math.random() - 0.5);
				}
			}
		}
	}

	private Color nextColor(Bot bot) {
		// Color c = new Color(bot.botColor.getRGB() + (int) (20 * Math.random()));
		Color cb = bot.botColor;
		int i = 20;
		int j = 0;
		int k = 255;
		Color c = new Color(Math.max(Math.min(cb.getRed() + (int) (i * (0.5 - Math.random())), k), j),
				Math.max(Math.min(cb.getGreen() + (int) (i * (0.5 - Math.random())), k), j),
				Math.max(Math.min(cb.getBlue() + (int) (i * (0.5 - Math.random())), k), j));
		return c;
	}

	double energy;
	double x_v, y_v, mass;
	double x, y;
	boolean dead = false;
	public int sensor_radius = 20;

	public void step(int f) {
		if (!dead) {
			x += x_v / f;
			y += y_v / f;
			int dz = 2;
			reflect(dz);
			double dx = (prey != null) ? (prey.x - x) : 0;
			double dy = (prey != null) ? (prey.y - y) : 0;
			double prey_mass = (prey != null) ? prey.mass : 0;
			double dd = (prey != null) ? (prey.dead ? 1 : 0) : 0;
			double[] state = { x_v, y_v, energy, mass, prey_mass, dx, dy, dd };
			double[] control = netWork.calculate(state);

			// if (control[6] > 0.2) {
			searchPrey();
			if (prey != null && distance(prey) < 5) {
				killBot();
			}
			// }

			x_v += Math.min(control[0], energy);
			y_v += Math.min(control[1], energy);

			double velocity = getVelocity();
			energy -= Math.pow(velocity / (f * 3), 2);
			returnMass(0.0001);
			energy -= 0.01;//

			if (Main.areal.in(this)) {
				eatFood();
			}

			// System.out.println(lifetime + " " + energy + " " + velocity);

			if (lifetime > 100 && energy > 30 && velocity > 3) {

				// duplicateBot(Math.abs(control[2]));
				duplicateBot(.5);
			}

			if (energy < 1) {
				returnMass(0.01);
				dead = true;
			}
			lifetime++;
		} else {
			prey = null;
			x_v = 0;
			y_v = 0;
			returnMass(0.01);
			if (mass == 0) {
				Main.bots.remove(this);
			}

		}
	}

	private void reflect(int dz) {
		if ((int) x > Main.areal.x() - dz) {
			x_v = -.9 * Math.abs(x_v);
			y_v = .9 * y_v;
			x = Main.areal.x() - dz;
		}

		if ((int) x < dz) {
			x_v = .9 * Math.abs(x_v);
			y_v = .9 * y_v;
			x = dz;
		}

		if ((int) y > Main.areal.y() - dz) {
			y_v = -.9 * Math.abs(y_v);
			x_v = .9 * x_v;
			y = Main.areal.y() - dz;
		}

		if ((int) y < dz) {
			y_v = .9 * Math.abs(y_v);
			x_v = .9 * x_v;
			y = dz;
		}
	}

	private void searchPrey() {
		Bot bot = null;
		double dist = 100000;
		for (Bot b : Main.bots) {
			if (b != this) {
				double dd = distance(b);
				if (dd < dist) {
					dist = dd;
					bot = b;
				}
			}
		}
		prey = bot;
	}

	public double getVelocity() {

		return Math.sqrt(x_v * x_v + y_v * y_v);
	}

	int getSX() {
		return (int) (x + x_v);
	}

	int getSY() {
		return (int) (y + y_v);
	}

	private void returnMass(double k) {
		double d = Math.min(Math.max(mass * Math.random() * k, 0.001), mass);
		mass -= d;
		Main.areal.returnFood(this, d);
	}

	private void eatFood() {
		for (int i = -1; i < 1; i++) {
			for (int j = -1; j < 1; j++) {
				double d = Main.areal.food[(int) x + i][(int) y + j];
				energy += d;
				mass += d;
				Main.areal.food[(int) x + i][(int) y + j] = 0;
			}
		}
	}

	void duplicateBot(double cld) {
		// System.out.println("dbl " + cld);
		Bot nbot = new Bot(this, cld);
		Main.bots.add(nbot);
		lifetime = 0;
	}

	public Color getBotColor() {
		// TODO Auto-generated method stub
		return botColor;
	}

}
