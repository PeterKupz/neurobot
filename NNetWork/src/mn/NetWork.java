package mn;

import java.util.ArrayList;
import java.util.Iterator;

public class NetWork {

	Layer topLayer;

	ArrayList<Layer> llist = new ArrayList<>();

	public NetWork(double d, int... n) {
		topLayer = new Layer(null, n[0], d);
		Layer l = topLayer;
		llist.add(topLayer);
		for (int i = 1; i < n.length; i++) {
			Layer layer = new Layer(l, n[i], d);
			llist.add(layer);
			l = layer;
		}
	}

	double[] calculate(double[] inputs) {
		try {
			topLayer.setLayerValues(inputs);
		} catch (WrongDimentionException e) {
			e.printStackTrace();
		}
		for (Layer l : llist) {
			// System.out.println("расчет слоя " + l);
			l.calcOutput();
		}
		return llist.get(llist.size() - 1).getLayerValues();
	}

	public NetWork getMutation() {
		int size = this.llist.size();
		int[] f = new int[size];
		for (int i = 0; i < size; i++) {
			f[i] = this.llist.get(i).getSize();

		}
		NetWork netWork = new NetWork(0.1, f);

		for (int i = 0; i < size; i++) {
			netWork.llist.get(i).copyM(this.llist.get(i));
		}

		return netWork;
	}

}
