package videira.ifc.edu.br.georent.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import videira.ifc.edu.br.georent.R;
import videira.ifc.edu.br.georent.activities.ResidenceShowActivity;
import videira.ifc.edu.br.georent.adapters.ResidenceImageAdapter;
import videira.ifc.edu.br.georent.interfaces.Bind;
import videira.ifc.edu.br.georent.interfaces.RecyclerViewOnClickListener;
import videira.ifc.edu.br.georent.listeners.RecyclerViewTouchListener;
import videira.ifc.edu.br.georent.models.Residence;
import videira.ifc.edu.br.georent.models.ResidenceImage;
import videira.ifc.edu.br.georent.repositories.ResidenceImageRepository;
import videira.ifc.edu.br.georent.repositories.ResidenceRepository;
import videira.ifc.edu.br.georent.utils.AuthUtil;
import videira.ifc.edu.br.georent.utils.FakeGenerator;
import videira.ifc.edu.br.georent.utils.NetworkUtil;

public class ResidenceIndexFragment extends Fragment implements RecyclerViewOnClickListener, Bind<Residence> {
    //Parâmetros constantes do fragment
    public static final String ARG_PAGE_RESIDENCE = "HOME";

    //Parâmetros variáveis do fragment
    private int page;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Residence> mResidenceList;
    private LinearLayoutManager mLinearLayoutManager;
    private ResidenceImageAdapter mResidenceAdapter;
    private ResidenceRepository mResidenceRepository;
    private View mView;
    protected ProgressBar mProgressBar;

    public static ResidenceIndexFragment newInstance(int page) {
        ResidenceIndexFragment fragment = new ResidenceIndexFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_RESIDENCE, page);
        fragment.setArguments(args);
        return fragment;
    }

    /*************************************************************************
     **                             VIEW                                    **
     *************************************************************************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.page = getArguments().getInt(ARG_PAGE_RESIDENCE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /**
         * Infla o layout do fragment e pega a view
         */
        mView = inflater.inflate(R.layout.fragment_index_residence, container, false);

        mResidenceRepository = new ResidenceRepository(this.getActivity(), this);
        mResidenceList = new ArrayList<>();
        mProgressBar = (ProgressBar) mView.findViewById(R.id.pb_residence_index);

        /**
         * Seta as propriedades do LayoutManager para a lista
         */
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        /**
         * Seta as propriedades do recyclerView (componente gráfico da lista)
         */
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.rv_users);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        /**
         * Cria o adapter para amarrar a view aos objetos e seta ele na view
         */
        mResidenceAdapter = new ResidenceImageAdapter(mResidenceList, getActivity());
        mRecyclerView.setAdapter(mResidenceAdapter);
        //Adiciona os eventos na lista
        mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), mRecyclerView, this));

        //Seta listeners de ações do componente
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            /**
             * Chamado ao carregar mais itens na tela
             * @param recyclerView
             * @param dx
             * @param dy
             */
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                /**
                 * Carrega mais itens se o último já foi exibido
                 */
                if (mResidenceList.size() == mLinearLayoutManager.findLastCompletelyVisibleItemPosition() + 1) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    doLoad();
                }
            }
        });

        /**
         * Seta o refresh do layout
         */
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.sr_users);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doLoad();
            }
        });


        /**
         * Retorna a view para preencher a tela
         */
        Log.i("LOG","onCreateView()");
        return mView;
    }

    @Override
    public void onStop() {
        mResidenceRepository.cancelRequests();
        mProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
        super.onStop();
    }

    @Override
    public void onStart() {
        if (mResidenceList.isEmpty()) {
            mProgressBar.setVisibility(View.VISIBLE);
            doLoad();
        }
        super.onStart();
    }

    /*************************************************************************
     **                             CLICK                                   **
     *************************************************************************/

    /**
     * Ação de click no item da lista a partir da interface
     *
     * @param view
     * @param position
     */
    @Override
    public void onClickListener(View view, int position) {
        //Joga uma mensagem curta com a posição na tela.
        int index = mResidenceAdapter.getListItem(position).getIdResidence();
        Intent i = new Intent(getActivity(), ResidenceShowActivity.class);
        i.putExtra("idResidence", index);
        startActivity(i);
        Log.i("LOG", "Clicou!");
    }

    /**
     * Ação de click longo no item da lista a partir da interface
     *
     * @param view
     * @param position
     */
    @Override
    public void onLongClickListener(View view, int position) {
        Toast.makeText(getActivity(), "Position: " + position, Toast.LENGTH_SHORT).show();
        //mResidenceImageAdapter.removeListItem(position);
    }

    /*************************************************************************
     **                            SERVIÇO                                  **
     *************************************************************************/

    @Override
    public void doLoad() {
        if (NetworkUtil.verifyConnection(getActivity())) {
            mResidenceRepository.getResidencesByUser(AuthUtil.getLoggedUserId(getActivity())); //Bind Correto
        } else {
            doError(new UnknownHostException());
        }
    }

    @Override
    public void doSingleBind(Residence result) {

    }

    @Override
    public void doMultipleBind(List<Residence> result) {
        boolean isNewer = mSwipeRefreshLayout.isRefreshing();

        mProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);

        for (Residence residence : result) {
            if (isNewer) {
                mResidenceAdapter.addListItem(residence, 0);
            } else {
                mResidenceAdapter.addListItem(residence, mResidenceAdapter.getItemCount());
            }
        }
    }

    @Override
    public void doError(Exception ex) {
        mProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
        Snackbar snackbar = null;

        Log.i("LOG","doError()");
        if (ex instanceof UnknownHostException) {
            snackbar = Snackbar
                    .make(mView.getRootView(), R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Conectar", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(it);
                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(getContext(), R.color.accent));
        } else {
            snackbar = Snackbar
                    .make(mView.getRootView(), R.string.unknown_error, Snackbar.LENGTH_SHORT)
                    .setActionTextColor(ContextCompat.getColor(getContext(), R.color.accent));
        }

        snackbar.show();
    }
}
