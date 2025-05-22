package com.example.denticaree;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.ai.client.generativeai.java.ChatFutures;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class Chat extends AppCompatActivity {
private TextInputEditText queryEditText;
private ImageView sendQuery,logo,appIcon;
FloatingActionButton btnShowDialog;
private ProgressBar progressBar;
private LinearLayout chatResponse;
private ChatFutures chatModel;
Dialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
       dialog=new Dialog(this);
       dialog.setContentView(R.layout.message_dialog);
       if(dialog.getWindow() !=null){
           dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
           dialog.setCancelable(false);
       }
       sendQuery=dialog.findViewById(R.id.sendMessage);
       queryEditText=dialog.findViewById(R.id.queryEditText);
       btnShowDialog=findViewById(R.id.showMessageDialog);
       progressBar=findViewById(R.id.progressBar);
       chatResponse=findViewById(R.id.chatResponse);
       appIcon=findViewById(R.id.appIcon);
       chatModel=getChatModel();

       btnShowDialog.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               dialog.show();
           }
       });
       sendQuery.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               dialog.dismiss();
               progressBar.setVisibility(View.VISIBLE);
               appIcon.setVisibility((View.GONE));
               String query=queryEditText.getText().toString();

               queryEditText.setText("");
               chatBody("You",query,getDrawable(R.drawable.ai));
               GeminiResp.getResponse(chatModel, query, new ResponseCallback() {
                   @Override
                   public void onResponse(String response) {
                       progressBar.setVisibility(View.GONE);
                       chatBody("AI",response,getDrawable(R.drawable.ai));

                   }

                   @Override
                   public void onError(Throwable throwable) {
chatBody("AI","please try again" ,getDrawable(R.drawable.ai));
                       progressBar.setVisibility(View.GONE);
                   }
               });

           }
       });

    }
    private ChatFutures getChatModel(){
        GeminiResp model = new GeminiResp();
        GenerativeModelFutures modelFutures = model.getModel();
        return modelFutures.startChat();
    }

    private void chatBody(String you, String query, Drawable drawable) {
        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.chat_message, null);
        TextView message=view.findViewById(R.id.agentMessage);
        ImageView imageView=view.findViewById(R.id.logo);
        name.setText(userName);



    }
}