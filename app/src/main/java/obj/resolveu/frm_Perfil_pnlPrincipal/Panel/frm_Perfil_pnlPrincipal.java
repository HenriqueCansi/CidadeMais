package obj.resolveu.frm_Perfil_pnlPrincipal.Panel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import obj.resolveu.R;
import obj.resolveu.RvActivity;
import obj.resolveu.RvView;

public class frm_Perfil_pnlPrincipal extends RvView {

    public static frm_Perfil_pnlPrincipal __obj = new frm_Perfil_pnlPrincipal();
    private LinearLayout layoutPerfil;
    private ImageView imgPerfil;
    private TextView tvTitulo, tvNome, tvCidade, tvPontos, tvPremios;
    private Button btnMeusPremios, btnMeusProblemas, btnMeusMutiroes;

    private LinearLayout layout;

    @Override
    public LinearLayout getLayout()
    {
        if (layout == null)
        {
            LayoutInflater inflater = LayoutInflater.from(RvActivity.__activity);
            layout = (LinearLayout) inflater.inflate(R.layout.frm_perfil_pnlprincipal, null);
            layoutPerfil = layout;
        }
        return layout;
    }


    @Override
    public void OnActivated() {
        super.OnActivated();
        if (layoutPerfil == null) {
            getLayout(); // força o layout a ser inflado
        }
        // Inicializa os componentes
        imgPerfil = layoutPerfil.findViewById(R.id.imgPerfil);
        tvTitulo = layoutPerfil.findViewById(R.id.tvTitulo);
        tvNome = layoutPerfil.findViewById(R.id.tvNome);
        tvCidade = layoutPerfil.findViewById(R.id.tvCidade);
        tvPontos = layoutPerfil.findViewById(R.id.tvPontos);
        tvPremios = layoutPerfil.findViewById(R.id.tvPremios);
        btnMeusPremios = layoutPerfil.findViewById(R.id.btnMeusPremios);
        btnMeusProblemas = layoutPerfil.findViewById(R.id.btnMeusProblemas);
        btnMeusMutiroes = layoutPerfil.findViewById(R.id.btnMeusMutiroes);

        // Exemplo: Preenche os dados (depois você pode puxar do Firebase ou SQLite)
        tvNome.setText("Henrique Cansi");
        tvCidade.setText("Caxias do Sul - RS");
        tvPontos.setText("190");
        tvPremios.setText("02");

        // Clique nos botões
        btnMeusPremios.setOnClickListener(v -> {
            // Ex: abrir tela de prêmios
        });

        btnMeusProblemas.setOnClickListener(v -> {
            // Ex: abrir tela de problemas
        });

        btnMeusMutiroes.setOnClickListener(v -> {
            // Ex: abrir tela de mutirões
        });
    }
}
