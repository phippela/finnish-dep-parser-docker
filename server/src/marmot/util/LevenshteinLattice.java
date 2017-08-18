// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class LevenshteinLattice {
	private int[][] cost_lattice_;
	private short[][] op_lattice_;

	private final static short START = 1;
	private final static short INSERT = 2;
	private final static short DELETE = 4;
	private final static short COPY = 8;
	private final static short REPLACE = 16;

	private String input_;
	private String output_;

	private int replace_cost_;
	private int insert_cost_;
	private int delete_cost_;
	
	private boolean initialized_;

	public LevenshteinLattice(String input, String output) {
		this(input, output, 1, 1, 2);
	}

	public LevenshteinLattice(String input, String output, int insert_cost,
			int delete_cost, int replace_cost) {
		input_ = input;
		output_ = output;
		replace_cost_ = replace_cost;
		insert_cost_ = insert_cost;
		delete_cost_ = delete_cost;
		initialized_ = false;		
	}
	
	private void init() {
		if (! initialized_) {
			fillLattice();
		}
		initialized_ = true;
	}

	protected int min(int a, int b, int c) {
		return Math.min(a, Math.min(b, c));
	}

	protected void fillLattice() {
		int input_length = input_.length();
		int output_length = output_.length();
		cost_lattice_ = new int[input_length + 1][output_length + 1];
		op_lattice_ = new short[input_length + 1][output_length + 1];

		op_lattice_[0][0] = START;

		for (int input_index = 1; input_index <= input_length; input_index++) {
			cost_lattice_[input_index][0] = input_index;
			op_lattice_[input_index][0] = DELETE;
		}

		for (int output_index = 1; output_index <= output_length; output_index++) {
			cost_lattice_[0][output_index] = output_index;
			op_lattice_[0][output_index] = INSERT;
		}

		for (int input_index = 1; input_index <= input_length; input_index++) {
			char current_input = input_.charAt(input_index - 1);
			for (int output_index = 1; output_index <= output_length; output_index++) {
				char current_output = output_.charAt(output_index - 1);

				short diag_op = REPLACE;
				int diag_cost = replace_cost_;
				if (current_input == current_output) {
					diag_cost = getCopyCost(input_index);
					diag_op = COPY;
				}

				int minimal_diag_cost = cost_lattice_[(input_index - 1)][(output_index - 1)]
						+ diag_cost;
				int minimal_delete_cost = cost_lattice_[(input_index - 1)][output_index]
						+ delete_cost_;
				int minimal_insert_cost = cost_lattice_[input_index][(output_index - 1)]
						+ insert_cost_;

				int minimal_cost = min(minimal_delete_cost,
						minimal_insert_cost, minimal_diag_cost);

				cost_lattice_[input_index][output_index] = minimal_cost;

				short minimal_cost_op = 0;

				if (minimal_cost == minimal_diag_cost) {
					minimal_cost_op |= diag_op;
				}

				if (minimal_cost == minimal_delete_cost) {
					minimal_cost_op |= DELETE;
				}

				if (minimal_cost == minimal_insert_cost) {
					minimal_cost_op |= INSERT;
				}

				op_lattice_[input_index][output_index] = minimal_cost_op;

			}
		}
	}

	protected int getCopyCost(int input_index) {
		return 0;
	}

	public String searchOperationSequence() {
		init();
		StringBuilder sb = new StringBuilder();
		int input_index = input_.length();
		int output_index = output_.length();

		boolean stop = false;
		while (!stop) {
			short op = op_lattice_[input_index][output_index];

			if ((op & START) > 0) {
				stop = true;

				assert op == START;

			} else if ((op & COPY) > 0) {
				sb.append('C');
				output_index--;
				input_index--;
			} else if ((op & REPLACE) > 0) {
				sb.append('R');
				output_index--;
				input_index--;
			} else if ((op & INSERT) > 0) {
				sb.append('I');
				output_index--;
			} else if ((op & DELETE) > 0) {
				sb.append('D');
				input_index--;
			} else {
				throw new RuntimeException("Unexpected operation code: "
						+ Integer.toBinaryString(op));
			}
		}

		sb.reverse();

		return sb.toString();
	}

	public List<List<Character>> searchOperationSequences(boolean remove_redundant) {
		init();
		int input_index = input_.length();
		int output_index = output_.length();

		List<List<Character>> lists = searchOperationSequences(input_index,
				output_index);

		if (remove_redundant) {
			ListIterator<List<Character>> iter = lists.listIterator();
			while (iter.hasNext()) {
				List<Character> next = iter.next();

				if (redundant(next)) {
					iter.remove();
				}

			}
		}

		return lists;
	}
	
	public List<List<Character>> searchOperationSequences() {
		return searchOperationSequences(false);
	}

	public List<List<Character>> searchOperationSequences(int input_index,
			int output_index) {
		init();
		short op = op_lattice_[input_index][output_index];

		List<List<Character>> lists = new LinkedList<List<Character>>();

		if ((op & START) > 0) {
			assert op == START;
			lists.add(new LinkedList<Character>());
		} else {

			if ((op & COPY) > 0) {
				lists.addAll(appendToList(
						searchOperationSequences(input_index - 1,
								output_index - 1), 'C'));
			}

			if ((op & REPLACE) > 0) {
				lists.addAll(appendToList(
						searchOperationSequences(input_index - 1,
								output_index - 1), 'R'));
			}

			if ((op & INSERT) > 0) {
				lists.addAll(appendToList(
						searchOperationSequences(input_index, output_index - 1),
						'I'));
			}

			if ((op & DELETE) > 0) {
				lists.addAll(appendToList(
						searchOperationSequences(input_index - 1, output_index),
						'D'));
			}

			if (lists.isEmpty()) {
				throw new RuntimeException("Unexpected operation code: "
						+ Integer.toBinaryString(op));
			}

		}

		return lists;
	}

	private List<List<Character>> appendToList(List<List<Character>> lists,
			char c) {
		for (List<Character> list : lists) {
			list.add(c);
		}
		return lists;
	}

	public int getDistance() {
		init();
		return cost_lattice_[input_.length()][output_.length()];
	}

	public boolean redundant(List<Character> seq) {
		char last_c = 'S';
		for (char c : seq) {
			if (c == 'R' && last_c == 'D') {
				// Canonical form is RD
				return true;
			}
			if (c == 'R' && last_c == 'I') {
				// Canonical form is RI
				return true;
			}
			if (c == 'I' && last_c == 'D') {
				// Canonical form is R
				return true;
			}
			if (c == 'D' && last_c == 'I') {
				// Canonical form is R
				return true;
			}

			last_c = c;
		}
		return false;
	}

}