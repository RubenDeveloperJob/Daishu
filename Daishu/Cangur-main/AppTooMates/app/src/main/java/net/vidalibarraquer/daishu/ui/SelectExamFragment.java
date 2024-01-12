package net.vidalibarraquer.daishu.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.vidalibarraquer.daishu.R;
import net.vidalibarraquer.daishu.objects.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelectExamFragment extends Fragment{
    //vars
    private RequestQueue queue = null;
    String[] items;
    String[] items_value;
    String exam;
    String url;

    //ui
    Spinner levelSpinner;
    ListView examsList;
    Button continueBtn;
    ImageButton back;

    public SelectExamFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //ui
        levelSpinner = view.findViewById(R.id.level_spinner);
        examsList = view.findViewById(R.id.exams_list);
        back = view.findViewById(R.id.goback_btn_select_exam);
        continueBtn = view.findViewById(R.id.continue_select_exam);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ProfileActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //vars
                ExamenActivity a = new ExamenActivity();

                a.exam = exam;

                if ( queue == null )
                    queue = Volley.newRequestQueue(getContext());

                //make the request
                JsonObjectRequest request = new JsonObjectRequest(url,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    //vars
                                    List<String> array = new ArrayList<>();

                                    //save the array
                                    JSONObject jsonObject = response.getJSONObject("data");
                                    JSONArray jsonArray = jsonObject.getJSONArray("exams");

                                    a.level = jsonObject.getString("reference");

                                    //save the questions
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        if (jsonArray.getJSONObject(i).keys().next().equals(exam)){
                                            jsonArray = jsonArray.getJSONObject(i).getJSONArray(exam);

                                            a.questions.clear();

                                            for (int x = 0; x < jsonArray.length(); x++){
                                                Question q = new Question(
                                                        jsonArray.getJSONObject(x).getString("file"),
                                                        jsonArray.getJSONObject(x).getString("title"),
                                                        jsonArray.getJSONObject(x).getString("exercise"),
                                                        jsonArray.getJSONObject(x).getString("exercise_image"),
                                                        jsonArray.getJSONObject(x).getString("options"),
                                                        jsonArray.getJSONObject(x).getInt("answer"),
                                                        jsonArray.getJSONObject(x).getString("answer_image")
                                                );

                                                a.questions.add(q);
                                            }
                                        }
                                    }

                                    //put the exams on the listview
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, array);
                                    examsList.setAdapter(adapter);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
                queue.add(request);

                Fragment newFragment = new StartExamFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.layout_examen, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        //set values
        items = getActivity().getResources().getStringArray(R.array.levels_name);
        items_value = getActivity().getResources().getStringArray(R.array.levels_links);

        //put values in the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        levelSpinner.setAdapter(adapter);

        //spinner listener
        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //get the exams
                onLevelSelected(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //listview listener
        examsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                exam = parent.getItemAtPosition(position).toString();
                continueBtn.setEnabled(true);
            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_exam, container, false);
    }

    private void onLevelSelected(int level){
        //vars
        url = null;

        //get the link
        for(int i = 0; i <= level; i++){
            url = items_value[i];
        }

        if ( queue == null )
            queue = Volley.newRequestQueue(getContext());

            //make the request
            JsonObjectRequest request = new JsonObjectRequest(url,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //vars
                                List<String> array = new ArrayList<>();

                                //save the array
                                JSONObject jsonObject = response.getJSONObject("data");
                                JSONArray jsonArray =  jsonObject.getJSONArray("exams");

                                //save the exams name
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    array.add(jsonArray.getJSONObject(i).keys().next());
                                }

                                //put the exams on the listview
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, array);
                                examsList.setAdapter(adapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
        queue.add(request);
    }
}