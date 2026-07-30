package obj.cidademais.frm_Perfil_pnlPrincipal.Panel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import obj.cidademais.R;
import obj.cidademais.RvActivity;
import obj.cidademais.RvView;
import obj.cidademais.frm_Login.Data.Sessao;
import obj.cidademais.frm_Login.Data.Usuario;
import obj.cidademais.frm_Login.Panel.frm_Login_pnlLogin;

public class frm_Perfil_pnlPrincipal extends RvView
{

	public static frm_Perfil_pnlPrincipal __obj = new frm_Perfil_pnlPrincipal();
	private LinearLayout layoutPerfil;
	private ImageView imgPerfil;
	private TextView tvTitulo, tvNome, tvCidade, tvPontos, tvPremios;
	private Button btnMeusPremios, btnMeusProblemas, btnSair;

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
	public void OnActivated()
	{
		super.OnActivated();
		if (layoutPerfil == null)
		{
			getLayout();
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
		btnSair = layoutPerfil.findViewById(R.id.btnSair);

		tvNome.setText("Henrique Cansi");
		tvCidade.setText("Caxias do Sul - RS");
		tvPontos.setText("190");
		tvPremios.setText("02");

		btnMeusPremios.setOnClickListener(v -> {
		});

		btnMeusProblemas.setOnClickListener(v -> {
		});

		btnSair.setOnClickListener(v -> {

			Sessao.logout();
			frm_Login_pnlLogin.__obj.Show();
			Hide();

		});
	}
	public void ShowCustom(){

		Usuario usuario = Sessao.getUsuario();

		tvNome.setText(usuario.nome);
		tvCidade.setText(usuario.cidade);
	}
}
