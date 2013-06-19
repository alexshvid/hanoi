import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;



public class Solution {

	public static final int MAX_STEPS = 8;
	
	public static class State {
		
		int[] state = null;
		
		public State(int n) {
			this.state = new int[n];
		}

		public State(int n, int[] istate) {
			this.state = new int[n];
			for (int i = 0; i != n; ++i) {
				this.state[i] = istate[i] - 1;
			}
		}

		public State(int[] state) {
			this.state = new int[state.length];
			System.arraycopy(state, 0, this.state, 0, state.length);
		}

		public State set(int radius, int ped) {
			state[radius] = ped;
			return this;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(state);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			State other = (State) obj;
			if (!Arrays.equals(state, other.state))
				return false;
			return true;
		}

		boolean hasSmallerDisks(int radius, int ped) {
			for (int i = 0; i != radius; ++i) {
				if (state[i] == ped) {
					return true;
				}
			}
			return false;
		}
		
		public State apply(Move move) {
			for (int i = 0; i != state.length; ++i) {
				if (state[i] == move.getFrom() && !hasSmallerDisks(i, move.getTo())) {
					return new State(state).set(i, move.getTo());
				}
			}
			return null;
		}
		
	}
	
	public static class Move {
		int from;
		int to;
		
		public Move(int from, int to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public String toString() {
			return "" + (from+1) + " " + (to+1);
		}

		public int getFrom() {
			return from;
		}

		public int getTo() {
			return to;
		}
		
	}
	
	public static List<Move> genAtomicMoves(int k) {
		List<Move> list = new ArrayList<Move>();
		for (int i = 0; i != k; ++i) {
			for (int j = 0; j != k; ++j) {
				if (i != j) {
				    list.add(new Move(i, j));
				}
			}
		}
		return list;
	}
	
	
	public static class Step implements Comparable<Step> {

		int steps = 0;
		State state;
		Move move = null;
		Step prev = null;
		
		public Step(State state) {
			this.state = state;
		}
		
		public Step(Step prev, State newState, Move move) {
			this.steps = prev.steps + 1;
			this.state = newState;
			this.move = move;
			this.prev = prev;
		}
		
		boolean hasAlready(State newState) {
			Step step = this;
			while(step != null) {
				if (newState.equals(step.state)) {
					return true;
				}
				step = step.prev;
			}
			return false;
		}
		
		public List<Move> getMoves() {
			List<Move> result = new ArrayList<Move>();
			Step step = this;
			while(step.move != null) {
				result.add(step.move);
				step = step.prev;
			}
			Collections.reverse(result);
			return result;
		}
		
		public Step next(Move move) {
			State newState = state.apply(move);
			if (newState != null) {
				if (hasAlready(newState)) {
					return null;
				}
				return new Step(this, newState, move);
			}
			return null;
		}

		@Override
		public int compareTo(Step o) {
			return Integer.valueOf(steps).compareTo(o.steps);
		}

		public State getState() {
			return state;
		}
		
	}

	public static class Solver {

		List<Move> atomicMoves;
		State initState;
		State finalState;
		List<Step> solutions = new ArrayList<Step>();
		
		public Solver(int n, int k, int[] iState, int[] fState) {
		    atomicMoves = genAtomicMoves(k);
			initState = new State(n, iState);
			finalState = new State(n, fState);
		}

		public void solve() {
			solve(new Step(initState));
		}

		public boolean isSolution(Step step) {
			return step.getState().equals(finalState);
		}
		
		public void solve(Step step) {
			for (Move move : atomicMoves) {
				Step nextStep = step.next(move);
				if (nextStep != null) {
					
					if (isSolution(nextStep)) {
						solutions.add(nextStep);
					}
					else if (nextStep.steps <= MAX_STEPS) {
						solve(nextStep);
					}
					
				}
			}
			
			Collections.sort(solutions);
		}

		public List<Step> getSolutions() {
			return solutions;
		}

		public List<Step> getTopSolutions() {
			if (!solutions.isEmpty()) {
				return getSolutions(solutions.get(0).steps);
			}
			return solutions;
		}

		public List<Step> getSolutions(int maxSteps) {
			List<Step> result = new ArrayList<Step>();
			for (Step sol : solutions) {
				if (sol.steps <= maxSteps) {
					result.add(sol);
				}
			}
			return result;
		}
		
	}

	private static int[] parseInts(String line) {
		String[] parts = line.split("\\s+");
		int[] result = new int[parts.length];
		for (int i = 0; i != parts.length; ++i) {
			result[i] = Integer.parseInt(parts[i]);
		}
		return result;
	}
	
	public static void main(String[] args) {

		Solver solver = null;
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		long time = 0;
		
		try {
			int[] nk = parseInts(in.readLine());
			int[] initState = parseInts(in.readLine());
			int[] finalState = parseInts(in.readLine());
			
			time = System.currentTimeMillis();
			
			int n = nk[0];
			int k = nk[1];
			
			if (initState.length != n || finalState.length != n) {
				throw new IllegalStateException();
			}
			
			solver = new Solver(n, k, initState, finalState);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		solver.solve();
		
		List<Step> topSol = solver.getTopSolutions();

		time = System.currentTimeMillis() - time;
		
		System.out.println("time = " + time);
		
		for (Step step : topSol) {
			System.out.println(step.steps);
			for (Move move : step.getMoves()) {
				System.out.println(move);
			}
		}
		
	}
	
}
