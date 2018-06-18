package dlx;

import java.util.List;


/**
 * Dancing Linksの実装です。
 *
 */
public class DancingLinks {

	private int[] row;


	private int head;

	private int[] left;
	private int[] right;
	private int[] up;
	private int[] down;

	private int[] colHead;

	private int[] bits;

	/**
	 *
	 * @param subSets
	 * @param cols
	 */
	public DancingLinks(List<List<Integer>> subSets, int cols) {
		bits = new int[cols];
		int size = 0;
		for(List<Integer> sub : subSets) {
			size += sub.size();
		}
		size += cols + 1;
		row = new int[size];
		left = new int[size];
		right = new int[size];
		up = new int[size];
		down = new int[size];
		colHead = new int[size];

		int[] front = new int[cols];
		int next = initHeads(cols, front);
		for(int r=0; r<subSets.size(); r++) {
			next = addRow(subSets.get(r), r, next, front);
		}
		for(int c=0; c<cols; c++) {
			down[front[c]] = colHead[front[c]];
			up[colHead[front[c]]] = front[c];
		}
	}

	public int row(int n) {
		return row[n];
	}

	public int head() {
		return head;
	}

	public int colHead(int n) {
		return colHead[n];
	}

	public int left(int n) {
		return left[n];
	}

	public int right(int n) {
		return right[n];
	}

	public int up(int n) {
		return up[n];
	}

	public int down(int n) {
		return down[n];
	}

	/**
	 * 垂直方向につながっているノードの数が最も小さい、削除されていない列ヘッダーを返します。
	 * 列ヘッダーがすべて削除されている場合は、全体ヘッダーを返します。
	 * @return 垂直方向につながっているノードの数が最も小さい、削除されていない列ヘッダー
	 */
	public int searchMinCol() {
		int h = right[head];
		int minC = h;
		while(h != head) {
			if(bits[h] < bits[minC]) {
				minC = h;
			}
			h = right[h];
		}
		return minC;
	}

	public void removeVertically(int n) {
		up[down[n]] = up[n];
		down[up[n]] = down[n];
		bits[colHead[n]]--;
	}

	public void removeHorizontally(int n) {
		right[left[n]] = right[n];
		left[right[n]] = left[n];
	}

	public void insertVertically(int n) {
		up[down[n]] = n;
		down[up[n]] = n;
		bits[colHead[n]]++;
	}

	public void insertHorizontally(int n) {
		right[left[n]] = n;
		left[right[n]] = n;
	}

///////////////////private methods///////////////////////

	private int initHeads(int cols, int[] front) {
		head = cols;
		for(int i=0; i<=cols; i++) {
			row[i] = -1;
			colHead[i] = i;
			left[i] = i-1;
			right[i] = i+1;
		}
		left[0] = cols;
		right[cols] = 0;
		up[head] = down[head] = head;
		for(int i=0; i<cols; i++) {
			front[i] = i;
		}
		return cols + 1;
	}

	private int addRow(List<Integer> oneRow, int r, int next, int[] front) {
		if(oneRow.isEmpty()) return next;
		int st = next;
		for(int c : oneRow) {
			bits[c]++;
			row[next] = r;
			colHead[next] = colHead[front[c]];
			down[front[c]] = next;
			up[next] = front[c];
			front[c] = next;
			left[next] = next - 1;
			right[next] = next + 1;
			next++;
		}
		left[st] = next - 1;
		right[next-1] = st;
		return next;
	}

}
