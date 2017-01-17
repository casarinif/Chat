package com.example.eduardocasarini.chat;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.List;

public class ConversasFragment extends Fragment {

    ListView ltwChats = null;
    List<Chat> lista = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversas, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        ltwChats = (ListView) getView().findViewById(R.id.ltwChats);
        refreshListView();

        ltwChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences preferences = getActivity().getSharedPreferences("USER_INFORMATION", Context.MODE_PRIVATE);

                Chat chat = (Chat)parent.getAdapter().getItem(position);

                Contato contato = null;

                SQLiteHelper db = new SQLiteHelper(getContext());

                if (preferences.getInt("id_usuario", 0) == chat.getId_user()){
                    contato = db.getContato(chat.getContact_user());
                } else {
                    contato = db.getContato(chat.getId_user());
                }

                Intent it = new Intent(getActivity(), ChatActivity.class);
                it.putExtra("contact_user", contato.getContact_user());
                it.putExtra("name_contact", contato.getName_contact());
                it.putExtra("photo_contact", contato.getPhoto_contact());
                startActivity(it);
            }
        });

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, new IntentFilter("REFRESH"));
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshListView();
        }
    };


    private void refreshListView(){

        SharedPreferences preferences = getActivity().getSharedPreferences("USER_INFORMATION", Context.MODE_PRIVATE);

        SQLiteHelper db = new SQLiteHelper(getContext());
        this.lista = db.getAllChats(preferences.getInt("id_usuario", 0));

        if (ltwChats.getAdapter() != null){
            ((ChatsAdapter)ltwChats.getAdapter()).refresh(this.lista);
        } else {
            ltwChats.setAdapter(new ChatsAdapter(getContext(), this.lista));
        }
    }
}

