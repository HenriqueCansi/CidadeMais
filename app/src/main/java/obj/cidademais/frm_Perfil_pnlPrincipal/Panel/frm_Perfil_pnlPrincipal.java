package obj.cidademais.frm_Perfil_pnlPrincipal.Panel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import obj.cidademais.Core.CmConstantes;
import obj.cidademais.Core.CmReputacao;
import obj.cidademais.Firebase.Ocorrencia.FirebaseOcorrencia;
import obj.cidademais.Firebase.Usuario.FirebaseUsuario;
import obj.cidademais.R;
import obj.cidademais.RvActivity;
import obj.cidademais.RvView;
import obj.cidademais.frm_Login.Data.Sessao;
import obj.cidademais.frm_Login.Data.Usuario;
import obj.cidademais.frm_Login.Panel.frm_Login_pnlLogin;
import obj.cidademais.frm_Principal.Data.Ocorrencia;

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

		Usuario usuario = Sessao.getUsuario();

		if (usuario != null)
		{
			tvNome.setText(usuario.nome);
			tvCidade.setText(usuario.cidade);
			carregarReputacao(usuario);
		}
		else
		{
			tvNome.setText("Visitante");
			tvCidade.setText("");
			tvPontos.setText("0");
			tvPremios.setText("0");
		}

		btnMeusPremios.setOnClickListener(v -> {
		});

		btnMeusProblemas.setOnClickListener(v -> {
			if (frm_Perfil_pnlMeusProblemas.__obj == null)
				frm_Perfil_pnlMeusProblemas.__obj = new frm_Perfil_pnlMeusProblemas();

			frm_Perfil_pnlMeusProblemas.__obj.Show();
			Hide();
		});

		btnSair.setOnClickListener(v -> {

			Sessao.logout();
			frm_Login_pnlLogin.__obj.Show();
			Hide();

		});
	}
	public void ShowCustom()
	{
	}

	private void carregarReputacao(Usuario usuario)
	{
		FirebaseOcorrencia.listarPorUsuario(usuario.uid, new FirebaseOcorrencia.CallbackListagem()
		{
			@Override
			public void onSucesso(List<Ocorrencia> lista)
			{
				int totalCurtidas = 0;
				int totalConfirmacoes = 0;
				int qtdResolvidas = 0;

				for (Ocorrencia oc : lista)
				{
					totalCurtidas += oc.curtidas;
					totalConfirmacoes += oc.confirmacoes;

					if (CmConstantes.STATUS_RESOLVIDA.equals(oc.status))
						qtdResolvidas++;
				}

				int reputacao = CmReputacao.calcularReputacao(lista.size(), totalCurtidas, totalConfirmacoes, qtdResolvidas);
				int nivel = CmReputacao.calcularNivel(reputacao);

				tvPontos.setText(String.valueOf(reputacao));
				tvPremios.setText(String.valueOf(lista.size()));

				usuario.reputacao = reputacao;
				usuario.nivel = nivel;
				FirebaseUsuario.salvar(usuario, null);
			}

			@Override
			public void onErro(Exception e)
			{
				tvPontos.setText("0");
				tvPremios.setText("0");
			}
		});
	}
}
