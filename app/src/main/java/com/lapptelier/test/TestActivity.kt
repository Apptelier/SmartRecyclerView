package com.lapptelier.test

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lapptelier.smartrecyclerview.*
import java.util.*

/**
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
internal class TestActivity : AppCompatActivity(), ViewHolderInteractionListener, OnMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private var mRecyclerView: SmartRecyclerView? = null

    private lateinit var adapter: MultiGenericAdapter

    private val elements = Arrays.asList("test 1", "test 2", "test 3", "test 4", "test 5", "test 6", "test 7", "test 8")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        mRecyclerView = findViewById(R.id.test_smart_recycler_view)

        //configuration de la liste
        mRecyclerView!!.setLayoutManager(LinearLayoutManager(this, RecyclerView.VERTICAL, false))
        mRecyclerView!!.setRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            Log.d("test", "FRESH-FRESH-FRESH !")
            mRecyclerView!!.enableSwipeToRefresh(false)
        })

        // Configuration de l'adapter
        adapter = MultiGenericAdapter(String::class.java, TestViewHolder::class.java, R.layout.cell_test, this)
        mRecyclerView!!.setAdapter(adapter)
        mRecyclerView!!.addItemDecoration(DrawableDividerItemDecoration(ContextCompat.getDrawable(baseContext, R.drawable.divider), null, true))

        mRecyclerView!!.emptyLayout = R.layout.empty
        mRecyclerView!!.loadingLayout = R.layout.loading
        mRecyclerView!!.loadMoreLayout = R.layout.loading
        mRecyclerView!!.setOnMoreListener(this, 5)
        mRecyclerView!!.setRefreshListener(this)

        mRecyclerView!!.enableEmptyView(true)
        mRecyclerView!!.enableLoadMore(true)
        mRecyclerView!!.enableSwipeToRefresh(true)

        Handler().postDelayed({ adapter.addAll(elements) }, 2000)


    }


    // OnMoreListener
    override fun onMoreAsked(overallItemsCount: Int, itemsBeforeMore: Int, maxLastVisiblePosition: Int) {
        adapter.addAll(elements)
    }

    override fun onRefresh() {
        adapter.clear()
        adapter.addAll(elements)
    }


    override fun onItemAction(item: Any, viewId: Int, action: ViewHolderInteraction) {
        adapter.removeAt(adapter.getObjectIndex(item))
    }
}
