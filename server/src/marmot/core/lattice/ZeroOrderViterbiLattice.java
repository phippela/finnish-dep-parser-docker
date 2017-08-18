// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core.lattice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import marmot.core.State;



public class ZeroOrderViterbiLattice implements ViterbiLattice {
	private LatticeEntry[][] lattice_;
	private List<List<State>> candidates_;
	
	private int beam_size_;
	private boolean initilized_;

	public ZeroOrderViterbiLattice(List<List<State>> candidates, int beam_size) {
		candidates_ = candidates;
		beam_size_ = beam_size;
		initilized_ = false;
	}

	public void init() {
		
		if (initilized_) {
			return;
		}
		initilized_ = true;
		
		lattice_ = new LatticeEntry[candidates_.size()][];
		PriorityQueue<LatticeEntry> queue = new PriorityQueue<LatticeEntry>();
		
		int index = 0;
		for (List<State> states : candidates_) {
			queue.clear();
			
			int state_index = 0;
			for (State state : states) {
				queue.add(new LatticeEntry(state.getScore(), state_index));
				state_index ++;
			}
			
			int length = Math.min(beam_size_, queue.size());
			lattice_[index] = new LatticeEntry[length];
			assert length > 0;
			
			for (int rank = 0; rank < length; rank++) {
				LatticeEntry entry = queue.poll();
				if (entry == null)
					break;
					
				lattice_[index][rank] = entry;
			}
			
			index ++;
		}
	}

	public Hypothesis getViterbiSequence() {
		init();
		int[] signature = new int[candidates_.size()];
		return getSequenceBySignature(signature);
	}

	public Hypothesis getSequenceBySignature(int[] signature) {
		init();
		List<Integer> list = new LinkedList<Integer>();
		double score = 0.;

		for (int index = 0; index < signature.length; index ++) {
			int rank = signature[index];

			if (rank >= lattice_[index].length) {
				return null;
			}
			
			LatticeEntry entry = lattice_[index][rank];
			if (entry == null) {
				return null;
			}

			score += entry.getScore();
			list.add(entry.getPreviousStateIndex());
		}
		return new Hypothesis(list, score, signature);
	}
	
	/*public List<List<State>> filter() {
		List<List<State>> candidates = getCandidates();

		List<Set<Integer>> candidate_sets = new ArrayList<Set<Integer>>(
				candidates.size());
		for (int index = 0; index < candidates.size(); index++) {
			candidate_sets.add(new HashSet<Integer>());
		}

		for (Hypothesis h : getNbestSequences()) {
			int index = 0;
			for (int state_index : h.getStates()) {
				candidate_sets.get(index).add(state_index);
				index++;
			}
		}

		List<List<State>> new_candidates = new ArrayList<List<State>>(
				candidates.size());
		for (int index = 0; index < candidates.size(); index++) {
			Set<Integer> candidate_set = candidate_sets.get(index);
			int[] new_index_map = new int[candidates.get(index).size()];
			Arrays.fill(new_index_map, -1);

			List<State> states = new ArrayList<State>(candidate_set.size());
			for (int state_index : candidate_set) {
				State state = candidates.get(index).get(state_index);
				int new_state_index = states.size();
				new_index_map[state_index] = new_state_index;
				states.add(state);
			}
			new_candidates.add(states);
		}

		return new_candidates;
	}*/

	public List<List<State>> prune() {
		init();
		List<List<State>> candidates = new ArrayList<List<State>>(candidates_.size());

		for (int index = 0; index < candidates_.size(); index ++) {
			
			List<State> states = new ArrayList<State>(lattice_[index].length);
			
			for (int rank = 0; rank < lattice_[index].length; rank++) {
				LatticeEntry entry = lattice_[index][rank];
				int candidate_index = entry.getPreviousStateIndex();
				
				states.add(candidates_.get(index).get(candidate_index));
			}
			
			candidates.add(states);
		}
		
		assert candidates.size() > 0;
		return candidates;
	}
	
	public List<Hypothesis> getNbestSequences() {
		init();
		List<Hypothesis> list = new LinkedList<Hypothesis>();

		int[] signature = new int[candidates_.size()];
		PriorityQueue<Hypothesis> queue = new PriorityQueue<Hypothesis>();
		Set<int[]> used_signatures = new HashSet<int[]>();
		queue.add(getSequenceBySignature(signature));
		used_signatures.add(signature);

		while (list.size() < beam_size_) {
			Hypothesis h = queue.poll();

						
			if (h == null) {
				break;
			}

			list.add(h);
			signature = h.getSignature();

			for (int index = 0; index < signature.length; index++) {
				int[] new_signature = new int[signature.length];
				System.arraycopy(signature, 0, new_signature, 0,
						signature.length);
				new_signature[index]++;
				if (!used_signatures.contains(new_signature)) {
					used_signatures.add(new_signature);
					h = getSequenceBySignature(new_signature);
					if (h != null) {
						queue.add(h);
					}
				}
			}
		}
		return list;
	}

	@Override
	public List<List<State>> getCandidates() {
		return candidates_;
	}
}
