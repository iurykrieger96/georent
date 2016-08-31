package videira.ifc.edu.br.georent.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import videira.ifc.edu.br.georent.R;
import videira.ifc.edu.br.georent.models.NetworkObject;
import videira.ifc.edu.br.georent.utils.NetworkUtil;

/**
 * Created by iuryk on 30/08/2016.
 */
public class NetworkConnection {

    /**
     * Atributos
     */
    private static NetworkConnection connection;
    private Context mContext;
    private RequestQueue mRequestQueue;

    /**
     * Construtor
     * @param mContext
     */
    public NetworkConnection(Context mContext) {
        this.mContext = mContext;
        this.mRequestQueue = getRequestQueue();
    }

    /**
     * Obtém a conexão baseado no contexto da aplicação
     * @param context
     * @return
     */
    public static NetworkConnection getConnection(Context context){
        if(connection == null){
            connection = new NetworkConnection(context.getApplicationContext());
        }
        return connection;
    }

    /**
     * Obtém a fila de requisições baseado no contexto da aplicação
     * @return
     */
    public RequestQueue getRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
        return mRequestQueue;
    }

    /**
     * Adiciona uma requisição na fila
     * @param request
     * @param <T>
     */
    public <T> void addRequest( Request<T> request){
        getRequestQueue().add(request);
    }

    /**
     * Executa a transação da tag
     * @param transaction
     * @param tag
     */
    public void execute(final Transaction transaction, String tag){
        NetworkObject object = transaction.doBefore();
        Gson gson = new Gson();

        if(object == null){
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("jsonObject", gson.toJson(object));

        CustomRequest request = new CustomRequest(CustomRequest.Method.POST,
                NetworkUtil.getStringUrl(mContext, R.string.user_service),
                params,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("JSON", response.toString());
                        transaction.doAfter(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("JSON", error.getMessage());
                        transaction.doAfter(null);
                    }
                });
        request.setTag(tag);
        addRequest(request);
    }
}
