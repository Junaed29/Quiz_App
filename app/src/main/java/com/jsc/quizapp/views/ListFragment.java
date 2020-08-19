package com.jsc.quizapp.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jsc.quizapp.R;
import com.jsc.quizapp.adapter.QuizListAdapter;
import com.jsc.quizapp.model.QuestionDetailsModel;
import com.jsc.quizapp.model.QuizListModel;
import com.jsc.quizapp.utils.NetworkHelper;
import com.jsc.quizapp.viewmodel.QuizViewModel;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class ListFragment extends Fragment implements QuizListAdapter.OnClickQuizButton {

    private static final String TAG = "ListFragment";

    private QuizViewModel viewModel;
    private RecyclerView recyclerView;
    private QuizListAdapter adapter;
    private ProgressBar listProgressBar;
    private Animation fade_in_Anim, fade_out_Anim;
    private NavController navController;
    private SwipeRefreshLayout refreshLayout;

    String dept = "", batch = "";

    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = getActivity().getSharedPreferences(
                "com.jsc.quizapp", Context.MODE_PRIVATE);
        batch = prefs.getString("userBatch","null");
        dept = prefs.getString("userDept","null");


        recyclerView = (RecyclerView) view.findViewById(R.id.list_view);
        listProgressBar = (ProgressBar) view.findViewById(R.id.list_progress);
        refreshLayout = view.findViewById(R.id.refreshId);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startViewModel();
            }
        });

        adapter = new QuizListAdapter(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        navController = Navigation.findNavController(view);

        fade_in_Anim = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_in);
        fade_out_Anim = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_out);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startViewModel();
    }

    public void startViewModel() {
        Log.d(TAG, "startViewModel: ");
        viewModel = new ViewModelProvider(getActivity()).get(QuizViewModel.class);
        viewModel.getQuizListLiveData(dept,batch).observe(getViewLifecycleOwner(), new Observer<List<QuestionDetailsModel>>() {
            @Override
            public void onChanged(List<QuestionDetailsModel> quizListModels) {
                // RecyclerView Update
                if (quizListModels.isEmpty()) {
                    Toast.makeText(getContext(), "List is empty\nAll are private", Toast.LENGTH_SHORT).show();
                } else {
                    recyclerView.setAnimation(fade_in_Anim);
                    listProgressBar.setAnimation(fade_out_Anim);

                    adapter.setQuizListModels(quizListModels);
                    refreshLayout.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {

        if(NetworkHelper.isNetworkAvailable(getContext())){
            ListFragmentDirections.ActionListFragmentToDetailsFragment action = ListFragmentDirections.actionListFragmentToDetailsFragment();
            action.setPosition(position);
            navController.navigate(action);
        }else {
            Toasty.error(getContext(), "Please Connect to The Internet", Toast.LENGTH_SHORT).show();
        }

    }
}