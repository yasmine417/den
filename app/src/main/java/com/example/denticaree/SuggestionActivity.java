package com.example.denticaree;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class SuggestionActivity extends AppCompatActivity {

    private TextView resultText;
    private Button suggestButton;
    private Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        resultText = findViewById(R.id.resultText);
        suggestButton = findViewById(R.id.suggestButton);

        try {
            tflite = new Interpreter(loadModelFile());
        } catch (IOException e) {
            Log.e("TFLite", "Erreur chargement modèle : " + e.getMessage(), e);

            resultText.setText("Modèle non chargé");
            return;
        }

        suggestButton.setOnClickListener(v -> runPrediction());
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        Log.d("TFLite", "Chargement du modèle .tflite...");
        AssetFileDescriptor fileDescriptor = getAssets().openFd("XqBa2G6VmvQSjKvyv3kbyypaX803.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        Log.d("TFLite", "Modèle chargé avec succès");
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void runPrediction() {
        float[][] input = new float[1][3];
        float[][] output = new float[1][1];

        boolean suggestionTrouvee = false;

        for (int jour = 1; jour <= 7; jour++) { // jours 1 à 7
            for (int heure = 8; heure <= 17; heure++) { // heures 8h à 17h
                input[0][0] = (float) jour;
                input[0][1] = (float) heure;
                input[0][2] = 0f; // Type patient (ex : normal). Tu peux rendre ça dynamique aussi

                tflite.run(input, output);
                float score = output[0][0];

                Log.d("TFLite", "Test jour: " + jour + " heure: " + heure + " → score: " + score);

                if (score > 0.5f) {
                    resultText.setText("Créneau suggéré : Jour " + jour + " à " + heure + "h ✅ (score : " + score + ")");
                    suggestionTrouvee = true;
                    return;
                }
            }
        }

        if (!suggestionTrouvee) {
            resultText.setText("Aucun créneau recommandé trouvé ❌");
        }
    }

}

