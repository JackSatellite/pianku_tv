package com.hyena.pianku.tv;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    private ListAdapter mListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.main_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListAdapter = new ListAdapter();
        List<String> items = new ArrayList<>();
        items.add("测试1");
        items.add("测试2");
        items.add("测试3");
        items.add("测试4");
        items.add("测试5");
        mListAdapter.setItems(items);
        recyclerView.setAdapter(mListAdapter);
    }

    class ListAdapter extends RecyclerView.Adapter<ListHolder> {

        private List<String> items;

        public void setItems(List<String> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(getContext(), android.R.layout.simple_list_item_1, null);
            return new ListHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ListHolder holder, int position) {
            TextView textView = holder.itemView.findViewById(android.R.id.text1);
            textView.setText(items.get(position));
        }

        @Override
        public int getItemCount() {
            if (items == null)
                return 0;
            return items.size();
        }
    }

    class ListHolder extends RecyclerView.ViewHolder {

        public ListHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
