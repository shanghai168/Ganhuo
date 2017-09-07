package ganhuo.ly.com.ganhuo.mvp.huaban.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.List;
import java.util.concurrent.TimeUnit;

import ganhuo.ly.com.ganhuo.R;
import ganhuo.ly.com.ganhuo.mvp.base.BaseFragment;
import ganhuo.ly.com.ganhuo.mvp.entity.HuaResults;
import ganhuo.ly.com.ganhuo.mvp.home.adapter.GirlyAdapter;
import ganhuo.ly.com.ganhuo.mvp.huaban.presenter.HuaPresenter;
import ganhuo.ly.com.ganhuo.mvp.huaban.view.HuaFragmentView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by liuyu1 on 2017/8/22.
 */

public class HuaFragment extends BaseFragment implements HuaFragmentView {
    private SwipyRefreshLayout swipyRefreshLayout;
    private RecyclerView recyclerview;
    private HuaPresenter huaPresenter;
    private String type;
    private GirlyAdapter girlyAdapter;
    private List<HuaResults.PinsBean> pins;
    private static int NOW_PAGE= 1;
    private boolean isTop;


    public static HuaFragment getInstance(String type) {
        HuaFragment fra = new HuaFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type",type);
        fra.setArguments(bundle);
        return fra;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            type = bundle.getString("type");
        }
        huaPresenter = new HuaPresenter(this);
        getData(false,type,0);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_zhihu, container, false);
    }

    @Override
    protected void initListener() {
        initRecyclerView();
        initSwipyRefreshLayout();
    }

    private void initSwipyRefreshLayout() {
        swipyRefreshLayout = (SwipyRefreshLayout) mRootView.findViewById(R.id.swipyrefreshlayout);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        swipyRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);

        swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                isTop = direction == SwipyRefreshLayoutDirection.TOP ? true : false;

                Log.d("direction",direction.name()+""+isTop);
                Observable.timer(2, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                swipyRefreshLayout.setRefreshing(false);
                            }
                        });
                 if(pins!=null){
                    int maxId = getMaxId(pins);
                    getData(false,type,maxId);
                }

            }
        });

    }

    /**
     * 从返回联网结果中保存max值 用于下次联网的关键
     *
     * @param
     * @return
     */
    private int getMaxId(List<HuaResults.PinsBean> pins) {
        return pins.get(pins.size() - 1).getPin_id();
    }


    private void initRecyclerView() {
        recyclerview = (RecyclerView) mRootView.findViewById(R.id.recyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(llm);
        recyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        girlyAdapter = new GirlyAdapter(getActivity(), 2);
        recyclerview.setAdapter(girlyAdapter);
    }


    private void getData(boolean isUseCache,String type,int max) {
        if(isTop){
           max=0;
        }
        huaPresenter.getDataResults(type,max);
    }


    @Override
    public void showLoadFailMsg() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void newDatas(HuaResults data) {
        if (isTop) {
            girlyAdapter.getResults().clear();
        }
        pins = data.getPins();
        girlyAdapter.getHuaResults().addAll(pins);
        girlyAdapter.notifyDataSetChanged();
//        NOW_PAGE_MZ++;
    }


}