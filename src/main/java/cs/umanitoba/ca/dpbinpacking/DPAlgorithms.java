/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.umanitoba.ca.dpbinpacking;

/**
 *
 * @author azizmma
 */
public class DPAlgorithms {

    public double epsilon_max, epsilon_min, selection_prob, current_epsilon;
    int id = 0;

    public void add_selection(double prob) {
        this.selection_prob = this.selection_prob + prob;
    }

    public void enforce_penalty2() {
        this.selection_prob = this.selection_prob * (epsilon_min / epsilon_max);
    }

    public void enforce_penalty() {
        this.selection_prob = this.selection_prob * (1 - (epsilon_max - epsilon_min) / epsilon_max);
    }

    public double getCurrent_epsilon() {
        return current_epsilon;
    }

    public int getId() {
        return id;
    }

    public void setCurrent_epsilon(double current_epsilon) {
        this.current_epsilon = current_epsilon;
    }

    public DPAlgorithms(int id, double epsilon_max, double epsilon_min, double selection_prob) {
        this.id = id;
        this.epsilon_max = epsilon_max;
        this.epsilon_min = epsilon_min;
        this.selection_prob = selection_prob;
        this.current_epsilon = epsilon_min + Math.random() * (epsilon_max - epsilon_min);
    }

    public void setSelection_prob(double selection_prob) {
        this.selection_prob = selection_prob;
    }

    public double getEpsilon_max() {
        return epsilon_max;
    }

    public double getEpsilon_min() {
        return epsilon_min;
    }

    public double getSelection_prob() {
        return selection_prob;
    }

}
