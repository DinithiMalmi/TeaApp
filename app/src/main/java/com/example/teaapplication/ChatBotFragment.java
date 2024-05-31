package com.example.teaapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatBotFragment extends Fragment {

    private RecyclerView chatsRV;
    private EditText userMsgEdt;
    private FloatingActionButton sendMsgFAB;

    private final String BOT_KEY = "bot";
    private final String USER_KEY = "user";
    private ArrayList<ChatsModal> chatsModalArrayList;
    private ChatRVAdapter chatRVAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_bot, container, false);
        chatsRV = rootView.findViewById(R.id.idRVChats);
        userMsgEdt = rootView.findViewById(R.id.idEdtMessage);
        sendMsgFAB = rootView.findViewById(R.id.idFABSend);

        chatsModalArrayList = new ArrayList<>();
        chatRVAdapter = new ChatRVAdapter(chatsModalArrayList, getActivity());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        chatsRV.setLayoutManager(manager);
        chatsRV.setAdapter(chatRVAdapter);

        sendMsgFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userMsgEdt.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter your message", Toast.LENGTH_SHORT).show();
                    return;
                }
                getResponse(userMsgEdt.getText().toString());
                userMsgEdt.setText("");
            }
        });

        return rootView;
    }

    public void getResponse(String message) {
        chatsModalArrayList.add(new ChatsModal(message, USER_KEY));
        chatRVAdapter.notifyDataSetChanged();

        String BASE_URL = "https://a51a-35-245-34-138.ngrok-free.app";

        // Create OkHttpClient with custom timeout values
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES) // Set connection timeout
                .readTimeout(5, TimeUnit.MINUTES)    // Set read timeout
                .build();

        // Pass the custom OkHttpClient to Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient) // Set custom OkHttpClient
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        MsgModal msgModal = new MsgModal();
        msgModal.setPromt(message);

        Call<ResponseBody> call = retrofitAPI.getMessage(msgModal);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonResponse = response.body().string(); // Get the raw JSON string from the response body
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        String botMessage = jsonObject.optString("answer", "");

                        Log.d("ChatBotFragment", "Received message: " + botMessage); // Log the received message
                        chatsModalArrayList.add(new ChatsModal(botMessage, BOT_KEY)); // Add bot's message to list
                        chatRVAdapter.notifyDataSetChanged(); // Notify adapter of data change
                        chatsRV.smoothScrollToPosition(chatsRV.getAdapter().getItemCount() - 1); // Scroll to the last item
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        chatsModalArrayList.add(new ChatsModal("Error: Unable to parse response", BOT_KEY));
                        chatRVAdapter.notifyDataSetChanged();
                        chatsRV.smoothScrollToPosition(chatsRV.getAdapter().getItemCount() - 1); // Scroll to the last item
                    }
                } else {
                    chatsModalArrayList.add(new ChatsModal("Error: Unable to get response", BOT_KEY));
                    chatRVAdapter.notifyDataSetChanged();
                    chatsRV.smoothScrollToPosition(chatsRV.getAdapter().getItemCount() - 1); // Scroll to the last item
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                chatsModalArrayList.add(new ChatsModal("Error: " + t.getMessage(), BOT_KEY));
                chatRVAdapter.notifyDataSetChanged();
                chatsRV.smoothScrollToPosition(chatsRV.getAdapter().getItemCount() - 1); // Scroll to the last item
            }
        });
    }
}