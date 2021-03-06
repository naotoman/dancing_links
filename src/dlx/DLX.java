package dlx;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * {@link dlx#DancingLinks.java}をテストするために作成したクラスです。
 * Exact Cover Problemを、Donald E. Knuth考案のアルゴリズムDLXにより解きます。
 * @see <a href="https://arxiv.org/abs/cs/0011047">
 * Knuth, Donald (2000). "Dancing links". arXiv:cs/0011047</a>
 */
public class DLX {

	DancingLinks dl;
	Deque<Integer> sol;


	public boolean solve(List<List<Integer>> subSets, int cols) {
		dl = new DancingLinks(subSets, cols);
		sol = new ArrayDeque<>();
		return dlx();
	}

	public void printSol() {
		System.out.println(sol);
	}

	private boolean dlx() {
		int c = dl.searchMinCol();
		if(c == dl.head()) return true;//found a solution
		cover(c);
		int r = dl.down(c);
		while(r != c) {
			sol.addLast(dl.row(r));
			int j = dl.right(r);
			while(j != r) {
				cover(j);
				j = dl.right(j);
			}
			if(dlx()) return true;

			//backtrack
			sol.removeLast();
			int k = dl.left(r);
			while(k != r) {
				uncover(k);
				k = dl.left(k);
			}

			r = dl.down(r);
		}
		uncover(c);//backtrack
		return false;
	}

	private void cover(int c) {
		c = dl.colHead(c);
		dl.removeHorizontally(c);
		int i = dl.down(c);
		while(i != c) {
			int j = dl.right(i);
			while(j != i) {
				dl.removeVertically(j);
				j = dl.right(j);
			}
			i = dl.down(i);
		}
	}

	private void uncover(int c) {
		c = dl.colHead(c);
		int i = dl.up(c);
		while(i != c) {
			int j = dl.left(i);
			while(j != i) {
				dl.insertVertically(j);
				j = dl.left(j);
			}
			i = dl.up(i);
		}
		dl.insertHorizontally(c);
	}

	/*
	 * テスト
	 */
	public static void main(String[] args) {
		List<List<Integer>> ma = new ArrayList<>();
		int row = 1000;
		int col = 40;
		for(int i=0; i<row; i++){
			ma.add(new ArrayList<>());
			for(int j=0; j<col; j++) {
				if(Math.random() < 0.2) {
					ma.get(i).add(j);
				}
			}
		}

		DLX dlx = new DLX();
		if(dlx.solve(ma, col)) {
			while(!dlx.sol.isEmpty()) {
				System.out.println(ma.get(dlx.sol.removeFirst()));
			}
		}

	}
}
