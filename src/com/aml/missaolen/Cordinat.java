package com.aml.missaolen;

public class Cordinat {

	public static int[] WGS84toETRSTM35FIN(double lat,double lon){
		double la = lat/180.0 * Math.PI;
		double lo = lon/180.0 * Math.PI;
		double lo0 = 27.0/180.0 * Math.PI;
		double f = 1.0 / 298.257223563;
		double n = f / (2.0 - f);
		double e = Math.sqrt(2.0 * f - Math.pow(f, 2.0));
		double k0 = 0.9996;
		double h1p = 1.0/2.0 * n - 2.0/3.0 * Math.pow(n, 2.0) + 5.0/16.0 * Math.pow(n, 3.0) + 41.0/180.0 * Math.pow(n, 4.0);
		double h2p = 13.0/48.0 * Math.pow(n, 2.0) - 3.0/5.0 * Math.pow(n, 3.0) + 557.0/1440.0 * Math.pow(n, 4.0);
		double h3p = 61.0/240.0 * Math.pow(n, 3.0) - 103.0/140.0 * Math.pow(n, 4.0);
		double h4p = 49561.0/161280.0 * Math.pow(n, 4.0);
		double A1 = 6378137.0 / (1.0 + n) * (1.0 + Math.pow(n, 2.0) / 4.0 + Math.pow(n, 4.0) / 64.0);
		double Q = asinh(Math.tan(la)) - e * atanh(e * Math.sin(la));
		double be = Math.atan(Math.sinh(Q));
		double nnp = atanh(Math.cos(be) * Math.sin(lo - lo0));
		double Ep = Math.asin(Math.sin(be) * Math.cosh(nnp));
		double E1 = h1p * Math.sin(2.0 * Ep) * Math.cosh(2.0 * nnp);
		double E2 = h2p * Math.sin(4.0 * Ep) * Math.cosh(4.0 * nnp);
		double E3 = h3p * Math.sin(6.0 * Ep) * Math.cosh(6.0 * nnp);
		double E4 = h4p * Math.sin(8.0 * Ep) * Math.cosh(8.0 * nnp);
		double nn1 = h1p * Math.cos(2.0 * Ep) * Math.sinh(2.0 * nnp);
		double nn2 = h2p * Math.cos(4.0 * Ep) * Math.sinh(4.0 * nnp);
		double nn3 = h3p * Math.cos(6.0 * Ep) * Math.sinh(6.0 * nnp);
		double nn4 = h4p * Math.cos(8.0 * Ep) * Math.sinh(8.0 * nnp);
		double E = Ep + E1 + E2 + E3 + E4;
		double nn = nnp + nn1 + nn2 + nn3 + nn4;
		int out[] = new int[2];	
		out[0] = (int)(A1 * E * k0); // N lat
		out[1] = (int)(A1 * nn * k0 + 500000.0); // E lon
		return out;
	}
	
	private static double asinh(double x){
		return Math.log(x + Math.sqrt(x * x + 1.0));
	}

	private static double atanh(double x){
		return Math.log((1.0 + x) / (1.0 - x)) / 2.0;
	}
}
