package com.example.manageit.adapters;

import java.util.ArrayList;
import java.util.List;

/**
 * Placeholder adapter-level class.
 * Replace with RecyclerView.Adapter implementations when module list UIs are finalized.
 */
public class ModuleItemAdapter {

    private final List<String> items = new ArrayList<>();

    public void submitItems(List<String> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
    }

    public List<String> getItems() {
        return new ArrayList<>(items);
    }
}
