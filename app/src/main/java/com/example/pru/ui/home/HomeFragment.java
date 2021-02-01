package com.example.pru.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.pru.AdaptadorNovelas;
import com.example.pru.ListaNovelas;
import com.example.pru.R;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    public static final String ID_NOVELA = "com.com.example.tarea_1.ID_NOVELA";

    private  String usuario = "";
    String titulo, id, imagen;

    MenuItem itemlt;
    MenuItem itemln;
    MenuItem itemr;
    MenuItem subir_novelas;

    ArrayList<ListaNovelas> listaNovelas = new ArrayList<>();
    RecyclerView recyclerNovelas;

    private HomeViewModel homeViewModel;
    View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        view = inflater.inflate(R.layout.fragment_home, container, false);
/*
        final TextView textView = view.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
*/

        Cargar("https://tnowebservice.000webhostapp.com/UltimasActualizaciones.php");
/*
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            SharedPreferences datos_usu = this.getActivity().getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
            usuario = datos_usu.getString("usuario", "");
            if (!usuario.equals("")) {
                //setTitle("Hola " + usuario);
            }
        }
*/
        return view;
    }

    private void Cargar(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++){
                    try {
                        jsonObject = response.getJSONObject(i);
                        titulo = new String(jsonObject.getString("titulo").getBytes("ISO-8859-1"), "UTF-8");
                        id = jsonObject.getString("id_novela");
                        imagen = jsonObject.getString("portada");
                        listaNovelas.add(new ListaNovelas(titulo, id, imagen));

                    } catch (JSONException | UnsupportedEncodingException e){
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                recyclerNovelas = view.findViewById(R.id.ReyclerId);
                recyclerNovelas.setLayoutManager(new LinearLayoutManager(getContext()));

                AdaptadorNovelas adapter = new AdaptadorNovelas(listaNovelas);
                adapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id_N = listaNovelas.get(recyclerNovelas.getChildAdapterPosition(v)).getId();
/*
                        Intent i = new Intent(getContext(), Novela.class);

                        i.putExtra(ID_NOVELA, id_N);

                        startActivity(i);*/
                    }
                });
                recyclerNovelas.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "ERROR DE CARGA DE NOVELA", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }
}