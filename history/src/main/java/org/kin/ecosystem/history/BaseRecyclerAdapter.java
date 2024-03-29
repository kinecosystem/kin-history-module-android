package org.kin.ecosystem.history;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

abstract class BaseRecyclerAdapter<T, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {

	private static final int EMPTY_VIEW = 0x00000001;

	private OnItemClickListener onItemClickListener;
	private LayoutInflater layoutInflater;
	private @LayoutRes
	int layoutResId;

	private FrameLayout emptyLayout;
	private RecyclerView recyclerView;

	protected List<T> data;

	public BaseRecyclerAdapter(@LayoutRes int layoutResId) {
		this.layoutResId = layoutResId;
	}

	protected abstract void convert(@NonNull VH holder, @Nullable T item);

	protected abstract VH createBaseViewHolder(@NonNull View view);

	public void setNewData(@Nullable List<T> data) {
		this.data = data == null ? new ArrayList<T>() : data;
		notifyDataSetChanged();
	}

	@Override
	public VH onCreateViewHolder(ViewGroup parent, int viewType) {
		VH baseViewHolder;
		Context context = parent.getContext();
		this.layoutInflater = LayoutInflater.from(context);
		switch (viewType) {
			case EMPTY_VIEW:
				baseViewHolder = createBaseViewHolder(emptyLayout);
				break;
			default:
				baseViewHolder = createBaseViewHolder(getItemView(layoutResId, parent));
				bindViewListener(baseViewHolder);
		}
		return baseViewHolder;
	}

	private void bindViewListener(final VH baseViewHolder) {
		if (baseViewHolder == null) {
			return;
		}
		final View view = baseViewHolder.itemView;
		if (view == null) {
			return;
		}
		if (getOnItemClickListener() != null) {
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					getOnItemClickListener()
						.onItemClick(BaseRecyclerAdapter.this, view, baseViewHolder.getLayoutPosition());
				}
			});
		}
	}


	private View getItemView(@LayoutRes int layoutResId, ViewGroup parent) {
		return layoutInflater.inflate(layoutResId, parent, false);
	}

	@Override
	public void onBindViewHolder(VH holder, int position) {
		int viewType = holder.getItemViewType();
		if (viewType != EMPTY_VIEW) {
			convert(holder, getItem(position));
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (isEmptyView()) {
			return EMPTY_VIEW;
		}
		return super.getItemViewType(position);
	}

	private boolean isEmptyView() {
		return emptyLayout != null && getDataCount() == 0;
	}

	@Override
	public int getItemCount() {
		return isEmptyView() ? 1 : getDataCount();
	}

	protected int getDataCount() {
		return data == null ? 0 : data.size();
	}

	public List<T> getData() {
		return data != null ? data : new ArrayList<T>();
	}

	@Nullable
	private T getItem(@IntRange(from = 0) int position) {
		if (position >= 0 && position < data.size()) {
			return data.get(position);
		} else {
			return null;
		}
	}

	public void bindToRecyclerView(RecyclerView recyclerView) {
		if (this.recyclerView != null) {
			throw new RuntimeException("Should not bind RecyclerView twice");
		}
		this.recyclerView = recyclerView;
		this.recyclerView.setAdapter(this);
	}

	public void setEmptyView(View emptyView) {
		boolean insert = false;
		if (this.emptyLayout == null) {
			this.emptyLayout = new FrameLayout(emptyView.getContext());
			final RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
				RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
			final ViewGroup.LayoutParams lp = emptyView.getLayoutParams();
			if (lp != null) {
				layoutParams.width = lp.width;
				layoutParams.height = lp.height;
			}
			this.emptyLayout.setLayoutParams(layoutParams);
			insert = true;
		}
		this.emptyLayout.removeAllViews();
		this.emptyLayout.addView(emptyView);
		if (insert) {
			if (isEmptyView()) {
				notifyItemInserted(0);
			}
		}
	}

	public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
		onItemClickListener = listener;
	}

	public final OnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	public interface OnItemClickListener {

		void onItemClick(BaseRecyclerAdapter adapter, View view, int position);

	}
}
