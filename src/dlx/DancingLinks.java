package dlx;

import java.util.List;


/**
 * Dancing Linksの実装です。Dancing Linksは、"root", "column header", その他の３種類のノードからなります。
 * それぞれのノードには互いに異なる不変なノード番号（非負整数）が割り当てられています。
 * @see <a href="https://arxiv.org/abs/cs/0011047">
 * Knuth, Donald (2000). "Dancing links". arXiv:cs/0011047</a>
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
	 * 指定された列数（rootを除く）で、各行の指定された列の要素が1であるDancing Linksを作成します。
	 * <p>例えば、{@code subSets.get(0).get(x) == 3}であれば、
	 * root, column headerを除く0行目3列目(0_origin)の要素が1であることを示します。
	 * @param subSets 各行で1となる列のリストを集めたリスト。null、nullの要素は不可。
	 * 各リストの要素の定義域は[0, cols)で、また各リスト内では互いに異なる値である必要があります。
	 * @param cols 作成するDancing Linksの、rootを除く列数(>=1)
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

	/**
	 * 指定されたノード番号のノードがcolumn headerを除く何行目(0_origin)にあるか返す。
	 * 指定されたノードがrootまたはcolumn headerの場合は-1を返す。
	 * @param n 何行目にあるか返されるノード
	 * @return 指定されたノード番号のノードが属する行
	 */
	public int row(int n) {
		return row[n];
	}

	/**
	 * rootのノード番号を返します。
	 * @return rootのノード番号
	 */
	public int head() {
		return head;
	}

	/**
	 * 指定されたノード番号のノードが属する列のcolumn headerのノード番号を返します。
	 * @param n 属する列のcolumn headerのノード番号が返されるノードのノード番号
	 * @return 指定されたノード番号のノードが属する列のcolumn headerのノード番号
	 */
	public int colHead(int n) {
		return colHead[n];
	}

	/**
	 * 指定されたノード番号のノードの左向きポインタの先のノードのノード番号を返します。
	 * @param n 左向きポインタの先のノードのノード番号を返されるノードのノード番号
	 * @return 指定されたノード番号のノードの左向きポインタの先のノードのノード番号
	 */
	public int left(int n) {
		return left[n];
	}

	/**
	 * 指定されたノード番号のノードの右向きポインタの先のノードのノード番号を返します。
	 * @param n 右向きポインタの先のノードのノード番号を返されるノードのノード番号
	 * @return 指定されたノード番号のノードの右向きポインタの先のノードのノード番号
	 */
	public int right(int n) {
		return right[n];
	}

	/**
	 * 指定されたノード番号のノードの上向きポインタの先のノードのノード番号を返します。
	 * 指定されたノードがrootの場合はrootのノード番号を返します。
	 * @param n 上向きポインタの先のノードのノード番号を返されるノードのノード番号
	 * @return 指定されたノード番号のノードの上向きポインタの先のノードのノード番号
	 */
	public int up(int n) {
		return up[n];
	}

	/**
	 * 指定されたノード番号のノードの下向きポインタの先のノードのノード番号を返します。
	 * 指定されたノードがrootの場合はrootのノード番号を返します。
	 * @param n 下向きポインタの先のノードのノード番号を返されるノードのノード番号
	 * @return 指定されたノード番号のノードの下向きポインタの先のノードのノード番号
	 */
	public int down(int n) {
		return down[n];
	}

	/**
	 * 上下方向につながっているノードの数が最も小さい、行から削除されていないcolumn headerを返します。
	 * column headerが行からすべて削除されている場合は、rootを返します。
	 * @return 垂直方向につながっているノードの数が最も小さい、削除されていないcolumn header
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

	/**
	 * 指定されたノード番号のノードの上向きポインタが指すノードと
	 * 下向きポインタが指すノードを上下に直結させます。
	 * <p>これは、指定されたノードをそれが属する「列」から削除することに対応します。
	 * その際、削除されるノードのポインタは一切変更しません。
	 * @param n 列から削除されるノードのノード番号
	 */
	public void removeVertically(int n) {
		up[down[n]] = up[n];
		down[up[n]] = down[n];
		bits[colHead[n]]--;
	}

	/**
	 * 指定されたノード番号のノードの左向きポインタが指すノードと
	 * 右向きポインタが指すノードを左右に直結させます。
	 * <p>これは、指定されたノードをそれが属する「行」から削除することに対応します。
	 * その際、削除されるノードのポインタは一切変更しません。
	 * @param n 行から削除されるノードのノード番号
	 */
	public void removeHorizontally(int n) {
		right[left[n]] = right[n];
		left[right[n]] = left[n];
	}

	/**
	 * 指定されたノード番号のノード(xとする)の上向きポインタが指すノードの下向きポインタが指す先をxに、
	 * xの下向きポインタが指すノードの上向きポインタが指す先をxにそれぞれ変更します。
	 * <p>これは、指定されたノードをそれが属する「列」の元の場所に復元することに対応します。
	 * @param n 列の元の場所に復元されるノードのノード番号
	 */
	public void insertVertically(int n) {
		up[down[n]] = n;
		down[up[n]] = n;
		bits[colHead[n]]++;
	}

	/**
	 * 指定されたノード番号のノード(xとする)の左向きポインタが指すノードの右向きポインタが指す先をxに、
	 * xの右向きポインタが指すノードの左向きポインタが指す先をxにそれぞれ変更します。
	 * <p>これは、指定されたノードをそれが属する「行」の元の場所に復元することに対応します。
	 * @param n 行の元の場所に復元されるノードのノード番号
	 */
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
