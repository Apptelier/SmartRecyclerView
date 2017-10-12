package com.lapptelier.smartrecyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lapptelier.smartrecyclerview.swipe.SwipeLayout;
import com.lapptelier.smartrecyclerview.swipe.SwipeMode;
import com.lapptelier.smartrecyclerview.swipe.SwipeItemManagerImpl;
import com.lapptelier.smartrecyclerview.swipe.SwipeAdapterInterface;
import com.lapptelier.smartrecyclerview.swipe.SwipeItemManagerInterface;
import com.socks.library.KLog;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * com.lapptelier.smartrecyclerview.smart_recycler_view.MultiGenericAdapter
 * <p/>
 * Generic implementation of a {@see SmartAdapter} that can manager multiple item type and their corresponding ViewHolders.
 * <p/>
 * Sub class of {@link SmartAdapter}.
 *
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
public class SwipableMultiGenericAdapter extends SmartAdapter implements SwipeItemManagerInterface, SwipeAdapterInterface {

    private Map<Class<? extends ViewHolder>, Integer> fragmentResources; // List of all ViewHolders' layout ressource_id
    private Map<Class<?>, Class<? extends ViewHolder>> viewHolderForClass; // Map of all corresponding ViewHolders classes for a given item class
    private Map<Integer, Class<? extends ViewHolder>> viewHolders; // Map of all the ViewHolders classes, by their layour ressource id
    private Map<Class<?>, Integer> swipeLayoutIds; // Map of the swipe layout id for each viewholder type

    public SwipeItemManagerImpl mSwipeItemManager = new SwipeItemManagerImpl(this);


    /**
     * Empty constructor
     */
    public SwipableMultiGenericAdapter() {
        this.viewHolderForClass = new HashMap<>();
        this.fragmentResources = new HashMap<>();
        this.viewHolders = new HashMap<>();
        this.swipeLayoutIds = new HashMap<>();
    }

    /**
     * Full constructor
     *
     * @param itemClass         class of an item in the list
     * @param viewHolderClass   associated view holde class for the given class
     * @param fragment_resource ressources_id of the layout for the given ViewHolder class
     * @param swipeLayoutId     ressource_id of the swipe layout for the given class
     */
    public SwipableMultiGenericAdapter(Class<?> itemClass, Class<? extends ViewHolder> viewHolderClass, int fragment_resource, int swipeLayoutId) {
        this();
        this.addViewHolderType(itemClass, viewHolderClass, fragment_resource, swipeLayoutId);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int index) {

        if (index > -1) {
            //getting the ViewHolder class for a given item position
            Class viewHolderClass = this.viewHolders.get(index);

            View view = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(fragmentResources.get(viewHolderClass), viewGroup, false);

            //getting the constuctor of the ViewHolder by instropection
            try {
                Constructor constructor =
                        viewHolderClass.getConstructor(View.class);
                return (ViewHolder) constructor.newInstance(view);
            } catch (Exception e) {
                KLog.e("exception while creating viewHolder : " + e.getLocalizedMessage(), e);
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int index) {
        ((MyViewHolder) viewHolder).setItem(items.get(index));
        //binding the item with the swipe manager
        mSwipeItemManager.bind(viewHolder.itemView, index);
    }

    @Override
    public int getItemViewType(int position) {
        return this.fragmentResources.get(this.viewHolderForClass.get(this.items.get(position).getClass()));
    }


    /**
     * Add a new ViewHolder class for an item class
     *
     * @param itemClass         class of an item in the list
     * @param viewHolderClass   associated view holde class for the given class
     * @param fragment_resource ressources_id of the layout for the given ViewHolder class
     * @param swipeLayoutId     ressource_id of the swipe layout for the given class
     */
    public void addViewHolderType(Class<?> itemClass, Class<? extends ViewHolder> viewHolderClass, int fragment_resource, int swipeLayoutId) {
        this.viewHolderForClass.put(itemClass, viewHolderClass);
        this.fragmentResources.put(viewHolderClass, fragment_resource);
        this.viewHolders.put(fragment_resource, viewHolderClass);
        this.swipeLayoutIds.put(itemClass, swipeLayoutId);
    }

    /**
     * Remove a ViewHolder class
     *
     * @param viewHolderClass ViewHolder class to remove
     */
    public void removeViewHolderType(Class<? extends ViewHolder> viewHolderClass) {
        if (this.viewHolders.containsKey(viewHolderForClass.get(viewHolderClass)))
            this.viewHolders.remove(viewHolderForClass.get(viewHolderClass));
        if (viewHolderForClass.containsKey(viewHolderClass))
            this.viewHolderForClass.remove(viewHolderClass);
        if (fragmentResources.containsKey(viewHolderClass))
            this.fragmentResources.remove(viewHolderClass);
        if (swipeLayoutIds.containsKey(viewHolderForClass.get(viewHolderClass)))
            this.swipeLayoutIds.remove(viewHolderForClass.get(viewHolderClass));
    }

    /**
     * Methods for swipe
     */

    @Override
    public void openItem(int position) {
        mSwipeItemManager.openItem(position);
    }

    @Override
    public void closeItem(int position) {
        mSwipeItemManager.closeItem(position);
    }

    @Override
    public void closeAllExcept(SwipeLayout layout) {
        mSwipeItemManager.closeAllExcept(layout);
    }

    @Override
    public void closeAllItems() {
        mSwipeItemManager.closeAllItems();
    }

    @Override
    public List<Integer> getOpenItems() {
        return mSwipeItemManager.getOpenItems();
    }

    @Override
    public List<SwipeLayout> getOpenLayouts() {
        return mSwipeItemManager.getOpenLayouts();
    }

    @Override
    public void removeShownLayouts(SwipeLayout layout) {
        mSwipeItemManager.removeShownLayouts(layout);
    }

    @Override
    public boolean isOpen(int position) {
        return mSwipeItemManager.isOpen(position);
    }

    @Override
    public SwipeMode getMode() {
        return mSwipeItemManager.getMode();
    }

    @Override
    public void setMode(SwipeMode mode) {
        mSwipeItemManager.setMode(mode);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return this.swipeLayoutIds.get(this.items.get(position).getClass());
    }

    @Override
    public void notifyDatasetChanged() {
        super.notifyDataSetChanged();
    }
}