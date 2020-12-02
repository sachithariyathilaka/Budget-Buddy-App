package com.budget.buddy.moneytracking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class PredictionActivity extends AppCompatActivity {
    Button predict;
    EditText total;
    TextView prediction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);
        predict =  findViewById(R.id.button);
        total = findViewById(R.id.total);
        prediction = findViewById(R.id.prediction);
        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    predictBudjet();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void predictBudjet() throws JSONException {
        int Total = Integer.parseInt(total.getText().toString());
        int Food = Total/4;
        int Transport = Total/4;
        int Shopping = Total/4;
        int Debt = Total/4;
        // create json object
        JSONObject obj = new JSONObject();
        JSONObject Inputs = null;
        try {
            Inputs = obj.getJSONObject("Inputs");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject input1 = Inputs.getJSONObject("input1");
        JSONArray ColumnNames = input1.getJSONArray("ColumnNames");
        ColumnNames.put("Food");
        ColumnNames.put("Transport");
        ColumnNames.put("Shopping");
        ColumnNames.put("Debt");
        ColumnNames.put("Total Budjet");
        JSONArray Values = input1.getJSONArray("Values");
        JSONArray val = new JSONArray();
        Values.put(val);
        val.put(Food);
        val.put(Transport);
        val.put(Shopping);
        val.put(Debt);
        val.put(Total);
        sendRequest(String.valueOf(obj));
    }

    private void sendRequest(String data){
        String uri = "https://ussouthcentral.services.azureml.net/workspaces/f109c04a3b4a45cc8e03f355a55f52d8/services/a9cf95b70eae4379816e121d9db34c44/execute?api-version=2.0&details=true";
        String key = " 5BToVoVeo8p0q+Ot7kvgDTnPNzQHK3PMJpT4peIPBJVmzTI8l8HWl71ffbS5hj66dxDV/mrfkHumtPd1VNNhEA==";
        try {
            Content content = Request.Post(uri)
                    .addHeader("Content-Type", "application/json")
                    // Only needed if using authentication
                    .addHeader("Authorization", "Bearer " + key)
                    // Set the JSON data as the body
                    .bodyString(data, ContentType.APPLICATION_JSON)
                    // Make the request and display the response.
                    .execute().returnContent();
                    prediction.setText(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
