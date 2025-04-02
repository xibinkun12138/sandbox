package com.hello.sandbox.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hello.sandbox.ui.home.ViewPager2Adapter.PagerViewHolder;
import com.hello.sandbox.common.util.NullChecker;
import com.hello.sandbox.common.util.ViewUtil;
import java.util.ArrayList;
import java.util.List;
import top.niunaijun.blackboxa.R;

public class ViewPager2Adapter extends RecyclerView.Adapter<PagerViewHolder> {
  private final List<HomeBannerInfo> mList = new ArrayList<>();

  private OnItemClickListener mClickListener;

  public ViewPager2Adapter(List<HomeBannerInfo> list) {
    mList.addAll(list);
  }

  @NonNull
  @Override
  public PagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.home_pager_item, parent, false);
    return new PagerViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull PagerViewHolder holder, int position) {
    holder.bind(mList.get(position), position);
  }

  @Override
  public int getItemCount() {
    return mList.size();
  }

  class PagerViewHolder extends RecyclerView.ViewHolder {
    public PagerViewHolder(@NonNull View itemView) {
      super(itemView);
    }

    public void bind(HomeBannerInfo info, int position) {
      ImageView imageView = itemView.findViewById(R.id.banner_image);
      imageView.setImageDrawable(info.getIcon());
      ViewUtil.singleClickListener(
          itemView,
          v -> {
            if (NullChecker.notNull(mClickListener)) {
              mClickListener.onItemClick(info);
            }
          });
    }
  }

  public interface OnItemClickListener {
    void onItemClick(HomeBannerInfo info);
  }

  public void setOnItemClickListener(OnItemClickListener listener) {
    this.mClickListener = listener;
  }
}
