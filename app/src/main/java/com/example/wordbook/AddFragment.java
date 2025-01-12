package com.example.wordbook;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class AddFragment extends Fragment {
    private Button buttonSubmit;
    private EditText editTextEnglish, editTextChinese;
    private WordViewModel wordViewModel;

    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final FragmentActivity activity = requireActivity();
        wordViewModel = ViewModelProviders.of(activity).get(WordViewModel.class);
        buttonSubmit = activity.findViewById(R.id.buttonSubmit);
        editTextEnglish = activity.findViewById(R.id.editTextEnglish);
        editTextChinese = activity.findViewById(R.id.editTextChinese);

        // 获取传递的数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            String english = bundle.getString("english", "");
            String chinese = bundle.getString("chinese", "");
            final int id = bundle.getInt("id", -1);

            editTextEnglish.setText(english);
            editTextChinese.setText(chinese);
            buttonSubmit.setText("确认");
            buttonSubmit.setEnabled(true);

            buttonSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String english = editTextEnglish.getText().toString().trim();
                    String chinese = editTextChinese.getText().toString().trim();
                    Word word = new Word(english, chinese);
                    word.setId(id);
                    wordViewModel.updateWords(word);
                    NavController navController = Navigation.findNavController(v);
                    navController.navigateUp();
                    InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                }
            });
        } else {
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String english = editTextEnglish.getText().toString().trim();
                    String chinese = editTextChinese.getText().toString().trim();
                    buttonSubmit.setEnabled(!english.isEmpty() && !chinese.isEmpty());
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };

            editTextEnglish.addTextChangedListener(textWatcher);
            editTextChinese.addTextChangedListener(textWatcher);
            buttonSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String english = editTextEnglish.getText().toString().trim();
                    String chinese = editTextChinese.getText().toString().trim();
                    Word word = new Word(english, chinese);
                    wordViewModel.insertWords(word);
                    NavController navController = Navigation.findNavController(v);
                    navController.navigateUp();
                    InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                }
            });
        }
    }
}