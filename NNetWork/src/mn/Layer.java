package mn;

import java.text.DecimalFormat;

public class Layer {
	int layerPos = 0;
	Layer upLayer;
	double[][] weightMatrix;

	private double[] layerValues;

	void setLayerValues(double[] layerValues) throws WrongDimentionException {

		if (layerValues.length != this.layerValues.length) {
			throw new WrongDimentionException();
		} else {
			for (int y = 0; y < layerValues.length; y++) {
				this.layerValues[y] = Main.activation(layerValues[y]);
			}
		}
	}

	double getValue(int i) {
		return layerValues[i];
	}

	double[] getLayerValues() {
		return layerValues;
	}

	public Layer(Layer upLayer, int size, double d) {
		this.upLayer = upLayer;
		layerValues = new double[size];
		try {
			this.layerPos = this.upLayer.layerPos + 1;
			weightMatrix = new double[size][this.upLayer.getSize()];
			for (double[] ww : weightMatrix) {
				for (int i = 0; i < ww.length; i++) {
					ww[i] = d * (Math.random() - 0.5);
				}
			}
		} catch (NullPointerException e) {
			// TODO: handle exception
		}

	}

	void outMatrix() {
		DecimalFormat format = new DecimalFormat("#.00");
		try {
			for (double[] ww : weightMatrix) {
				for (int i = 0; i < ww.length; i++) {
					System.out.print(format.format(ww[i]) + " ");
				}
				System.out.println();
			}
		} catch (NullPointerException e) {
			System.out.println("Матрица весов не определена");
		}
	}

	public int getSize() {
		return layerValues.length;
	}

	public void calcOutput() {
		if (upLayer != null) {
			for (int i = 0; i < layerValues.length; i++) {
				double dd = 0;
				// System.out.println(upLayer == null);
				for (int j = 0; j < upLayer.getSize(); j++) {
					dd += upLayer.getValue(j) * weightMatrix[i][j];
				}
				layerValues[i] = dd;
			}
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "слой " + layerValues.length;
	}

	public void copyM(Layer layer) {
		System.out.println("generation " + getSize() + "  " + weightMatrix);
		for (int i = 0; i < weightMatrix.length; i++) {
			double[] dw = weightMatrix[i];

			for (int j = 0; j < dw.length; j++) {
				layer.weightMatrix[i][j] = dw[j];
			}
		}
	}

}
