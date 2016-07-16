package mujava.source;

public class cal {
	void basep(int n, int x, int y) {
		int r;
		x = 0;
		r = 0;
		while (r < n) {
			r = r + 2 * x - 1; /* fault in r */
			x = x + 1;
		}
		while (r > n) {
			int rsave;
			rsave = r;
			y = 0;
			while (r > n) {
				r = r - 2 * y + 1; /* fault in r */
				y = y + 1;
			}
			if (r < n) {
				r = rsave + 2 * x - 1; /* fault in r */
				x = x + 1;
			}
		}
	}
}
