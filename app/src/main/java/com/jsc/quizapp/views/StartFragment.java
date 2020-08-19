package com.jsc.quizapp.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.jsc.quizapp.R;
import com.jsc.quizapp.model.StudentInfoModel;
import com.tuyenmonkey.mkloader.MKLoader;

import es.dmoral.toasty.Toasty;


public class StartFragment extends Fragment {

    String courseId = "";
    String batch = "";
    String dept = "";
    String quizTitle = "";

    private ProgressBar startProgressBar;
    private TextView startTextView;
    private FirebaseAuth mAuth;

    private NavController navController;
    private static final String TAG = "StartFragment";
    private int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient;

    public StartFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        navController = Navigation.findNavController(view);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        startTextView = view.findViewById(R.id.start_feedback);
        startProgressBar = view.findViewById(R.id.start_progress);
    }

    private void signIn() {
        startTextView.setText("Checking Account...");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        startTextView.setText("Creating Account...");
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getContext(), "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                            setStudentInfoDialog();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        SharedPreferences prefs = getActivity().getSharedPreferences(
                "com.jsc.quizapp", Context.MODE_PRIVATE);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String prefsUserId = prefs.getString("userId","null");
        if (prefsUserId.equals("null")) {
            if (currentUser == null) {
                signIn();
            }else {
                setStudentInfoDialog();
            }
        } else {
            startTextView.setText("Welcome back\n" + currentUser.getDisplayName());
            //Toast.makeText(getContext(), "Welcome back\n"+currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    navController.navigate(R.id.action_startFragment_to_listFragment);

                }
            }, 1500);

        }
    }

    void setStudentInfoDialog() {
        final Dialog dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(R.layout.details_dialog);
        dialog.setCanceledOnTouchOutside(false);


        final TextInputEditText courseIdEditText = dialog.findViewById(R.id.edit_username);
        final TextInputEditText quizTitleEditText = dialog.findViewById(R.id.edit_title);
        final MaterialSpinner deptSpinner = (MaterialSpinner) dialog.findViewById(R.id.spinnerDepiId);
        final MaterialSpinner batchSpinner = (MaterialSpinner) dialog.findViewById(R.id.spinnerBatchId);
        final Button goButton = (Button) dialog.findViewById(R.id.goButtonId);
        final MKLoader loader =  dialog.findViewById(R.id.loader);
        loader.setVisibility(View.GONE);

        deptSpinner.setItems(getResources().getStringArray(R.array.allDepartment));
        batchSpinner.setItems(getResources().getStringArray(R.array.allBatches));


        deptSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                dept = item.toString();
            }
        });

        batchSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                batch = item.toString();
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loader.setVisibility(View.VISIBLE);
                courseId = courseIdEditText.getText().toString().toUpperCase().trim();
                quizTitle = quizTitleEditText.getText().toString().trim();
                if (courseId == null | batch == null | dept == null | quizTitle == null | batch.isEmpty() | courseId.isEmpty() | dept.isEmpty() | quizTitle.isEmpty()) {
                    Toasty.error(getContext(), "Please Fill All Value", Toast.LENGTH_SHORT).show();
                } else {

                    StudentInfoModel model = new StudentInfoModel(quizTitle,courseId,batch,dept);


                    FirebaseFirestore.getInstance()
                            .collection("StudentInfo")
                            .document(mAuth.getCurrentUser().getUid())
                            .set(model)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toasty.success(getContext(), "Success", Toast.LENGTH_SHORT).show();

                                        SharedPreferences prefs = getActivity().getSharedPreferences(
                                                "com.jsc.quizapp", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString("userName", quizTitle);
                                        editor.putString("userId", mAuth.getCurrentUser().getUid());
                                        editor.putString("userBatch", batch);
                                        editor.putString("userDept", dept);
                                        editor.putString("userID", courseId);
                                        editor.apply();
                                        loader.setVisibility(View.GONE);

                                        navController.navigate(R.id.action_startFragment_to_listFragment);
                                    }else {
                                        Toasty.error(getContext(), "Something Wrong", Toast.LENGTH_SHORT).show();
                                    }

                                    dialog.dismiss();
                                }
                            });
                }
            }
        });


        dialog.show();
        Window window = dialog.getWindow();
        window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }


}