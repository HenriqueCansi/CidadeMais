package obj.cidademais.frm_Perfil_pnlPrincipal.Panel;

import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import obj.cidademais.Core.CmConstantes;
import obj.cidademais.Core.CmReputacao;
import obj.cidademais.Core.CmUi;
import obj.cidademais.Firebase.Ocorrencia.FirebaseOcorrencia;
import obj.cidademais.R;
import obj.cidademais.RvActivity;
import obj.cidademais.RvView;
import obj.cidademais.frm_Login.Data.Usuario;
import obj.cidademais.frm_Principal.Data.Ocorrencia;
import obj.cidademais.frm_Principal.Panel.frm_Principal_pnlFeedDetalhe;

public class frm_Perfil_pnlPerfilPublico extends RvView
{
	public static frm_Perfil_pnlPerfilPublico __obj = new frm_Perfil_pnlPerfilPublico();
	private LinearLayout layout;
	private Usuario usuarioAlvo;

	private ImageView ivFechar;
	private ImageView imgPerfil;
	private TextView tvNome;
	private TextView tvCidade;
	private TextView tvPontos;
	private TextView tvPremios;
	private Button btnVerOcorrencias;

	@Override
	public LinearLayout getLayout()
	{
		if (layout == null)
		{
			LayoutInflater inflater = LayoutInflater.from(RvActivity.__activity);
			layout = (LinearLayout) inflater.inflate(R.layout.frm_perfil_pnlperfilpublico, null);
		}
		return layout;
	}

	public void exibir(Usuario usuario)
	{
		this.usuarioAlvo = usuario;
	}

	@Override
	public void OnActivated()
	{
		ivFechar = layout.findViewById(R.id.ivFechar);
		imgPerfil = layout.findViewById(R.id.imgPerfil);
		tvNome = layout.findViewById(R.id.tvNome);
		tvCidade = layout.findViewById(R.id.tvCidade);
		tvPontos = layout.findViewById(R.id.tvPontos);
		tvPremios = layout.findViewById(R.id.tvPremios);
		btnVerOcorrencias = layout.findViewById(R.id.btnVerOcorrencias);

		CmUi.arredondar(imgPerfil, 40);

		if (usuarioAlvo == null)
			return;

		tvNome.setText(usuarioAlvo.nome);
		tvCidade.setText(usuarioAlvo.cidade);

		if (usuarioAlvo.fotoPerfil != null && !usuarioAlvo.fotoPerfil.isEmpty())
			Glide.with(RvActivity.__activity).load(usuarioAlvo.fotoPerfil).into(imgPerfil);
		else
			imgPerfil.setImageResource(R.drawable.ic_perfil);

		carregarReputacao();

		ivFechar.setOnClickListener(v -> {
			frm_Principal_pnlFeedDetalhe.__obj.Show();
			Hide();
		});

		btnVerOcorrencias.setOnClickListener(v -> {
			if (frm_Perfil_pnlOcorrenciasUsuario.__obj == null)
				frm_Perfil_pnlOcorrenciasUsuario.__obj = new frm_Perfil_pnlOcorrenciasUsuario();

			frm_Perfil_pnlOcorrenciasUsuario.__obj.Show();
			frm_Perfil_pnlOcorrenciasUsuario.__obj.exibir(usuarioAlvo);
			Hide();
		});
	}

	private void carregarReputacao()
	{
		FirebaseOcorrencia.listarPorUsuario(usuarioAlvo.uid, new FirebaseOcorrencia.CallbackListagem()
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

				tvPontos.setText(String.valueOf(reputacao));
				tvPremios.setText(String.valueOf(lista.size()));
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
