package com.example.wordbook;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

        setupEditText(editTextEnglish, editTextChinese);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.getInt("id", -1) != -1) {  // 编辑模式
            String english = bundle.getString("english", "");
            String chinese = bundle.getString("chinese", "");
            final int id = bundle.getInt("id", -1);

            editTextEnglish.setText(english);
            editTextChinese.setText(chinese);
            buttonSubmit.setText("确认修改");
            buttonSubmit.setEnabled(true);

            TextWatcher textWatcher = createTextWatcher();
            editTextEnglish.addTextChangedListener(textWatcher);
            editTextChinese.addTextChangedListener(textWatcher);

            buttonSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveAndExit(activity, v, id);
                }
            });

            editTextChinese.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, android.view.KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        saveAndExit(activity, v, id);
                        return true;
                    }
                    return false;
                }
            });

        } else {
            buttonSubmit.setText("确认添加");

            TextWatcher textWatcher = createTextWatcher();
            editTextEnglish.addTextChangedListener(textWatcher);
            editTextChinese.addTextChangedListener(textWatcher);
            buttonSubmit.setEnabled(false);

            buttonSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveAndExit(activity, v, -1);
                }
            });

            editTextChinese.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, android.view.KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE && buttonSubmit.isEnabled()) {
                        saveAndExit(activity, v, -1);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private void setupEditText(EditText english, EditText chinese) {
        english.setSingleLine(true);
        chinese.setSingleLine(true);

        english.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        chinese.setImeOptions(EditorInfo.IME_ACTION_DONE);


        english.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        chinese.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }


    private TextWatcher createTextWatcher() {
        return new TextWatcher() {
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
    }


    private void saveAndExit(FragmentActivity activity, View view, int id) {
        hideKeyboard(view);

        String english = editTextEnglish.getText().toString().trim();
        String chinese = editTextChinese.getText().toString().trim();

        Word word = new Word(english, chinese);
        if (id != -1) {
            word.setId(id);
            wordViewModel.updateWords(word);
        } else {
            wordViewModel.insertWords(word);
        }

        NavController navController = Navigation.findNavController(view);
        navController.navigateUp();
    }


    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}