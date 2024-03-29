package org.kin.ecosystem.history;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

class BaseViewHolder extends RecyclerView.ViewHolder {

	/**
	 * Views indexed with their IDs
	 */
	private final SparseArray<View> views;

	BaseViewHolder(View view) {
		super(view);
		views = new SparseArray<>();
	}

	/**
	 * @param viewId
	 * @param <E>
	 * @return
	 */
	protected <E extends View> E getView(@IdRes int viewId) {
		View view = views.get(viewId);
		if (view == null) {
			view = itemView.findViewById(viewId);
			views.put(viewId, view);
		}
		return (E) view;
	}

	/**
	 *
	 *
	 * @param viewId
	 * @param width
	 * @param height
	 */
	protected void setViewSize(@IdRes int viewId, int width, int height) {
		View view = getView(viewId);
		if (view != null) {
			ViewGroup.LayoutParams params = view.getLayoutParams();
			params.width = width;
			params.height = height;
		}
	}

	protected void setViewHeight(@IdRes int viewId, int height) {
		View view = getView(viewId);
		if (view != null) {
			ViewGroup.LayoutParams params = view.getLayoutParams();
			params.height = height;
		}
	}

	protected void setVisibility(@IdRes int viewId, int visibility) {
		View view = getView(viewId);
		if (view != null) {
			view.setVisibility(visibility);
		}
	}

	protected void setViewTopMargin(@IdRes int viewId, int topMargin) {
		View view = getView(viewId);
		if (view != null) {
			ViewGroup.MarginLayoutParams params = (MarginLayoutParams)view.getLayoutParams();
			params.topMargin = topMargin;
		}
	}

	protected void setViewLeftMargin(@IdRes int viewId, int leftMargin) {
		View view = getView(viewId);
		if (view != null) {
			ViewGroup.MarginLayoutParams params = (MarginLayoutParams)view.getLayoutParams();
			params.leftMargin = leftMargin;
		}
	}

	/**
	 * @param viewId The view id.
	 * @param value The text to put in the text view.
	 */
	protected void setText(@IdRes int viewId, CharSequence value) {
		TextView view = getView(viewId);
		view.setText(value);
	}

	/**
	 * @param viewId The view id.
	 * @param maxEms The maximum ems width of the text view.
	 */
	protected void setMaxEMs(@IdRes int viewId, int maxEms) {
		TextView view = getView(viewId);
		view.setMaxEms(maxEms);
	}

	/**
	 * Set spannable test for text font manipulation.
	 */
	protected void setSpannableText(@IdRes int viewId, Spannable spannable) {
		TextView view = getView(viewId);
		view.setText(spannable, TextView.BufferType.SPANNABLE);
	}

	/**
	 * Set the color of the test
	 */
	protected void setTextColor(@IdRes int viewId, @ColorInt int color) {
		TextView view = getView(viewId);
		view.setTextColor(color);
	}

	protected void setVectorDrawable(@IdRes int viewId, @DrawableRes int vectorResID) {
		if(!AppCompatDelegate.isCompatVectorFromResourcesEnabled()) {
			AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
		}
		ImageView view = getView(viewId);
		view.setImageResource(vectorResID);
	}

	/**
	 * Set a image resource.
	 */
	protected void setImageResource(@IdRes int viewId, @DrawableRes int imageResId) {
		ImageView view = getView(viewId);
		view.setImageResource(imageResId);
	}
}