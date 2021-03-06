// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core;


public class State {
	private FeatureVector vector_;
	private double score_;
	
	protected double estimated_count_;
	
	private Transition[] transitions_;
	private int index_;
	private State sub_level_state_;
	
	public State() {
		index_ = -1;
	}
	
	public State(int index) {
		assert index >= 0;
		index_ = index;
	}
	
	public State(int index, State sub_level_state) {
		this(index);
		sub_level_state_ = sub_level_state;
	}

	public void setVector(FeatureVector vector) {
		vector_ = vector;
	}
	
	public void setScore(double score) {
		score_ = score;
	}
	
	public FeatureVector getVector() {
		return vector_;
	}

	public double getScore() {
		return score_;
	}

	public void setTransitions(Transition[] transitions) {
		transitions_ = transitions;
	}
	
	public int getIndex() {
		return index_;
	}

	public State getZeroOrderState() {
		return this;
	}

	public Transition getTransition(int previous_state_index) {
		return transitions_[previous_state_index];
	}

	public int getOrder() {
		return 1;
	}

	public boolean canTransitionTo(State other) {
		if (other.getOrder() != 1) {
			assert getIndex() == 0;
		}
		
		return true;
	}
	
	public Transition[] getTransitions() {
		return transitions_;
	}

	public void incrementEstimatedCounts(double d) {
		estimated_count_ += d;
	}

	public void updateWeights(WeightVector weights) {
		if (estimated_count_ != 0.0) {
			weights.updateWeights(this, estimated_count_, true);
			estimated_count_ = 0.0;
		}
	}

	public int getLevel() {
		if (sub_level_state_ == null) {
			return 0;
		}
		return sub_level_state_.getLevel() + 1;
	}

	public State getSubLevel(int depth) {
		assert depth >= 0;
		
		if (depth == 0)
			return this;
		
		if (sub_level_state_ == null) {
			
			if (depth == 1)
				return null;
			
			throw new RuntimeException("Can't reach depth!");
		}
		
		return sub_level_state_.getSubLevel(depth - 1);
	}

	public State getSubLevelState() {
		return sub_level_state_;
	}

	public void setSubLevelState(State sub_level_state) {
		sub_level_state_ = sub_level_state;
	}

	public boolean equalIndexes(State other) {
		
		if (index_ != other.getIndex()) {
			return false;
		}
		
		assert other.getLevel() == this.getLevel();
		
		if (other.getSubLevelState() != null) {
			return other.getSubLevelState().equalIndexes(getSubLevelState());
		}
		
		return true;
	}
	
	public State getSubOrderState() {
		return null;
	}
	
	public State getPreviousSubOrderState() {
		return null;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append('[');
		
		if (sub_level_state_ != null) {
			sb.append(sub_level_state_);
		}
		sb.append(' ');
		sb.append(index_);
		sb.append(']');
		
		return sb.toString();
	}

	public void check() {
		assert transitions_ == null;
	}

	public State copy(State state) {
		state.vector_ = vector_;
		state.score_ = score_;
		state.transitions_ = transitions_;
		state.index_ = index_;
		state.sub_level_state_ = sub_level_state_;
		state.estimated_count_ = estimated_count_;
		return state;
	}
	
	public State copy() {
		State state = copy(new State());
		assert state.index_ >= 0;
		return state;
	}

}
