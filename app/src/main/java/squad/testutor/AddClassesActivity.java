package squad.testutor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddClassesActivity
        extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private DatabaseReference classesRef;

    private RelativeLayout mainLayout;

    private int classCount;
    private ArrayList<String> classList = new ArrayList<>();

    private final int MAX_CLASSES = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_classes);

        ImageButton plusButton = (ImageButton) findViewById(R.id.plusButton);

        Button done = (Button) findViewById(R.id.done);
        Button cancel = (Button) findViewById(R.id.cancel);

        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);

        classesRef = FirebaseDatabase.getInstance().getReference().child("classes");

        for (String str :  MainMapActivity.classList) {
            classList.add(str);
        }

        initializeClasses();

        //listeners
        plusButton.setOnClickListener(this);

        done.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.done:
                setClasses();
                break;
            case R.id.cancel:
                Intent i = new Intent(AddClassesActivity.this, MainMapActivity.class);
                AddClassesActivity.this.startActivity(i);
                break;
            case R.id.plusButton:
                if (classCount < 6) {
                    //updateClassList();
                    classCount++;
                    addClassLayoutGroup();
                } else {
                    Toast.makeText(this, "You may only add 6 classes", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                removeClass(view);
        }
    }

    //removes selected item from other spinners to prevent it being selected twice
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long param) {
        //updateSpinners(parent, view, pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }

    //called in onCreate to set initial spinners
    private void initializeClasses() {
        classCount = 0;
        if (MainMapActivity.userClasses == null) {
            classCount = 1;
            addClassLayoutGroup();
        } else {
            for (String str : MainMapActivity.userClasses) {
                classCount++;
                addClassLayoutGroup();
            }
        }

        //updateSpinners();
    }


    private void removeClass(View view) {
        Log.d(LoginActivity.TAG, "IN removeClass");
        classCount--;

        ImageButton minusButton = (ImageButton) view;
        LinearLayout classLayout = (LinearLayout) minusButton.getParent();

        mainLayout.removeView(classLayout);

        int newIdNum = 1;
        int childCount = mainLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = mainLayout.getChildAt(i);
            if (v instanceof LinearLayout) {
                String newStr = "class" + String.valueOf(newIdNum);
                int newId = getResources().getIdentifier(newStr, "id", getPackageName());

                v.setId(newId);

                newIdNum++;
            }
        }

        setContentView(mainLayout);
    }

    private void updateClassList() {
        for(int i = 1; i <= classCount; i++) {
            String str = "class" + String.valueOf(classCount);
            int id = getResources().getIdentifier(str, "id", getPackageName());

            LinearLayout layout = (LinearLayout) findViewById(id);
            Spinner spinner = (Spinner) layout.getChildAt(0);
            classList.remove(spinner.getSelectedItem());
        }
    }

    private void updateSpinners(AdapterView<?> parent, View view, int pos) {
        int spinnerId = view.getId();

        String selected = (String) parent.getItemAtPosition(pos);

        ArrayList<String> classList = MainMapActivity.classList;
        classList.remove(classList.indexOf(selected));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, classList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for(int i = 0; i < classCount; i++) {
            String id = "class" + String.valueOf(classCount);
            int resId = getResources().getIdentifier(id, "id", getPackageName());

            if (resId != spinnerId) {
                Spinner spinner = (Spinner) findViewById(resId);
                spinner.setAdapter(adapter);
            }
        }
    }

    private void addClassLayoutGroup() {
        Log.d(LoginActivity.TAG, String.valueOf(classCount));
        Spinner spinner = new Spinner(this);
        LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        spinnerParams.weight = 1;
        spinner.setLayoutParams(spinnerParams);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, classList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ImageButton minusButton = new ImageButton(this);
        minusButton.setImageResource(android.R.drawable.ic_delete);
        minusButton.setOnClickListener(this);

        String str = "class" + String.valueOf(classCount);
        int id = getResources().getIdentifier(str, "id", getPackageName());

        LinearLayout linearLayout = new LinearLayout(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setId(id);
        linearLayout.addView(spinner);
        linearLayout.addView(minusButton, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        if (classCount != 1) {
            str = "class" + String.valueOf(classCount - 1);
            int prevId = getResources().getIdentifier(str, "id", getPackageName());

            layoutParams.addRule(RelativeLayout.BELOW, prevId);
        }

        linearLayout.setLayoutParams(layoutParams);

        mainLayout.addView(linearLayout);

        /*ImageButton plusButton = (ImageButton) findViewById(R.id.plusButton);
        RelativeLayout.LayoutParams plusParams = (RelativeLayout.LayoutParams) plusButton.getLayoutParams();
        plusParams.addRule(RelativeLayout.BELOW, id);
        plusButton.setLayoutParams(plusParams);*/

        setContentView(mainLayout);
    }

    //adds classes to user in database and returns the user to MainMapActivity
    private void setClasses() {
        ArrayList<String> classArr = new ArrayList<>();

        for (int i = 1; i <= classCount; i++) {
            String str = "class" + String.valueOf(i);
            int id = getResources().getIdentifier(str, "id", getPackageName());
            LinearLayout layout = (LinearLayout) findViewById(id);
            Spinner spinner = (Spinner) layout.getChildAt(0);
            classArr.add(spinner.getSelectedItem().toString());
        }

        if (MainMapActivity.user != null) {
            String uid = MainMapActivity.user.getUid();
            /*if (classesRef.child(uid) != null) {
                classesRef.child(uid).removeValue();
            }*/
            DatabaseReference userRef = classesRef.child(uid);
            userRef.setValue(classArr);
            /*for (String str : classArr) {
                userRef.child(str).setValue(0);
            }*/

            Intent i = new Intent(AddClassesActivity.this, MainMapActivity.class);
            AddClassesActivity.this.startActivity(i);
        } else {
            Log.d(MainMapActivity.DATABASE, "Failed getting current user");
        }
    }



    /*private void trash() {

        RelativeLayout.LayoutParams plusParams = (RelativeLayout.LayoutParams) plusButton.getLayoutParams();
        plusParams.removeRule(RelativeLayout.BELOW);
        plusParams.addRule(RelativeLayout.BELOW, newId);
        plusButton.setLayoutParams(plusParams);

        RelativeLayout.LayoutParams minusParams = (RelativeLayout.LayoutParams) minusButton.getLayoutParams();
        minusParams.removeRule(RelativeLayout.BELOW);
        minusParams.addRule(RelativeLayout.BELOW, newId);
        minusButton.setLayoutParams(minusParams);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_layout);

        setContentView(layout);
    }*/
}