package v;

import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public abstract class RAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

  View adaptingView = null;

  @SuppressWarnings("NullableProblems")
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflate(parent, viewType);
    return new ItemViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    adaptingView = holder.itemView;
    adapt(holder.itemView, getItem(position), getItemViewType(position), position);
    onViewRequested(position);
  }

  public abstract View inflate(ViewGroup parent, int itemViewType);

  public abstract void adapt(View convertView, T item, int itemViewType, int position);

  public void onViewRequested(int i) {}

  public abstract T getItem(int position);

  public abstract int getCount();

  @Override
  public void onViewRecycled(ViewHolder holder) {
    super.onViewRecycled(holder);
  }

  @Override
  public int getItemCount() {
    return getCount();
  }

  static class ItemViewHolder extends RecyclerView.ViewHolder {

    public ItemViewHolder(View itemView) {
      super(itemView);
    }
  }
}
