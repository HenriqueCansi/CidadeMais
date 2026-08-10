package obj.cidademais.frm_Principal.Panel;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Locale;

import obj.cidademais.Core.CmData;
import obj.cidademais.Firebase.Ocorrencia.FirebaseOcorrencia;
import obj.cidademais.Firebase.Usuario.FirebaseUsuario;
import obj.cidademais.R;
import obj.cidademais.RvActivity;
import obj.cidademais.RvView;
import obj.cidademais.frm_Login.Classe.CallbackCadastro;
import obj.cidademais.frm_Login.Data.Sessao;
import obj.cidademais.frm_Login.Data.Usuario;
import obj.cidademais.frm_Login.Panel.frm_Login_pnlLogin;
import obj.cidademais.frm_Principal.Data.Ocorrencia;

public class frm_Principal_pnlFeedDetalhe extends RvView
{
	public static frm_Principal_pnlFeedDetalhe __obj = new frm_Principal_pnlFeedDetalhe();
	private LinearLayout layout;
	private Ocorrencia ocorrenciaAtual;

	private ImageView ivFechar;
	private ImageView imgAvatar;
	private ImageView ivCurtir;
	private TextView tvNomePoster;
	private TextView tvTempo;
	private TextView tvTitulo;
	private TextView tvCategoria;
	private TextView tvEnderecoBairro;
	private TextView tvQtdImagens;
	private TextView tvCurtidas;
	private LinearLayout llFotos;
	private LinearLayout llCurtir;
	private Button btnVerNoMapa;

	@Override
	public LinearLayout getLayout()
	{
		if (layout == null)
		{
			LayoutInflater inflater = LayoutInflater.from(RvActivity.__activity);
			layout = (LinearLayout) inflater.inflate(R.layout.frm_principal_pnlfeeddetalhe, null);
		}
		return layout;
	}

	public void exibir(Ocorrencia oc)
	{
		this.ocorrenciaAtual = oc;
	}

	@Override
	public void OnActivated()
	{
		ivFechar = layout.findViewById(R.id.ivFechar);
		imgAvatar = layout.findViewById(R.id.imgAvatar);
		tvNomePoster = layout.findViewById(R.id.tvNomePoster);
		tvTempo = layout.findViewById(R.id.tvTempo);
		tvTitulo = layout.findViewById(R.id.tvTitulo);
		tvCategoria = layout.findViewById(R.id.tvCategoria);
		tvEnderecoBairro = layout.findViewById(R.id.tvEnderecoBairro);
		tvQtdImagens = layout.findViewById(R.id.tvQtdImagens);
		llFotos = layout.findViewById(R.id.llFotos);
		btnVerNoMapa = layout.findViewById(R.id.btnVerNoMapa);
		llCurtir = layout.findViewById(R.id.llCurtir);
		ivCurtir = layout.findViewById(R.id.ivCurtir);
		tvCurtidas = layout.findViewById(R.id.tvCurtidas);

		carregarDados();

		ivFechar.setOnClickListener(v -> {
			frm_Principal_pnlFeed.__obj.Show();
			this.Hide();
		});

		btnVerNoMapa.setOnClickListener(v -> {
			frm_Principal_pnlPrincipal.focoOcorrencia =
					new LatLng(ocorrenciaAtual.latitude, ocorrenciaAtual.longitude);

			frm_Principal_pnlPrincipal.__obj.Show();
			this.Hide();
		});

		llCurtir.setOnClickListener(v -> alternarCurtida());
	}

	private void carregarDados()
	{
		if (ocorrenciaAtual == null)
			return;

		tvTitulo.setText(ocorrenciaAtual.titulo);
		tvCategoria.setText(rotuloCategoria(ocorrenciaAtual.categoria));

		String bairro = ocorrenciaAtual.bairro != null ? ocorrenciaAtual.bairro : "";
		String endereco = ocorrenciaAtual.endereco != null ? ocorrenciaAtual.endereco : "";
		tvEnderecoBairro.setText(bairro.isEmpty() ? endereco : endereco + " - " + bairro);

		tvTempo.setText("há " + CmData.tempoRelativo(ocorrenciaAtual.criadoEm));

		int qtd = ocorrenciaAtual.fotos == null ? 0 : ocorrenciaAtual.fotos.size();
		tvQtdImagens.setText(String.format(Locale.getDefault(), "%02d imagens", qtd));

		llFotos.removeAllViews();

		if (ocorrenciaAtual.fotos != null)
			for (String url : ocorrenciaAtual.fotos)
			{
				ImageView iv = new ImageView(RvActivity.__activity);

				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						dpParaPx(220));
				params.bottomMargin = dpParaPx(8);

				iv.setLayoutParams(params);
				iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

				Glide.with(RvActivity.__activity).load(url).into(iv);

				llFotos.addView(iv);
			}

		atualizarBotaoCurtir();

		FirebaseUsuario.buscar(ocorrenciaAtual.uidUsuario, new FirebaseUsuario.CallbackBusca()
		{
			@Override
			public void onSucesso(Usuario usuario)
			{
				tvNomePoster.setText(usuario.nome);

				if (usuario.fotoPerfil != null && !usuario.fotoPerfil.isEmpty())
					Glide.with(RvActivity.__activity).load(usuario.fotoPerfil).into(imgAvatar);
			}

			@Override
			public void onErro(Exception e)
			{
				tvNomePoster.setText("Usuário");
			}
		});
	}

	private int dpParaPx(int dp)
	{
		float densidade = RvActivity.__activity.getResources().getDisplayMetrics().density;
		return Math.round(dp * densidade);
	}

	private void atualizarBotaoCurtir()
	{
		boolean jaCurtiu = Sessao.isLogado()
				&& ocorrenciaAtual.curtidoPor != null
				&& ocorrenciaAtual.curtidoPor.contains(Sessao.getUsuario().uid);

		ivCurtir.setColorFilter(Color.parseColor(jaCurtiu ? "#E53935" : "#CCCCCC"));
		tvCurtidas.setText(ocorrenciaAtual.curtidas + " Apoios");
	}

	private void alternarCurtida()
	{
		if (!Sessao.isLogado())
		{
			Toast.makeText(RvActivity.__activity,
					"Faça login para apoiar uma ocorrência.",
					Toast.LENGTH_LONG).show();

			if (frm_Login_pnlLogin.__obj == null)
				frm_Login_pnlLogin.__obj = new frm_Login_pnlLogin();

			frm_Login_pnlLogin.__obj.Show();
			this.Hide();
			return;
		}

		String uid = Sessao.getUsuario().uid;
		boolean jaCurtiu = ocorrenciaAtual.curtidoPor != null && ocorrenciaAtual.curtidoPor.contains(uid);
		boolean curtir = !jaCurtiu;

		llCurtir.setEnabled(false);

		FirebaseOcorrencia.alternarCurtida(ocorrenciaAtual.id, uid, curtir, new CallbackCadastro()
		{
			@Override
			public void onSucesso()
			{
				if (ocorrenciaAtual.curtidoPor == null)
					ocorrenciaAtual.curtidoPor = new ArrayList<>();

				if (curtir)
				{
					ocorrenciaAtual.curtidoPor.add(uid);
					ocorrenciaAtual.curtidas++;
				}
				else
				{
					ocorrenciaAtual.curtidoPor.remove(uid);
					ocorrenciaAtual.curtidas = Math.max(0, ocorrenciaAtual.curtidas - 1);
				}

				atualizarBotaoCurtir();
				llCurtir.setEnabled(true);
			}

			@Override
			public void onErro(Exception e)
			{
				Toast.makeText(RvActivity.__activity, e.getMessage(), Toast.LENGTH_SHORT).show();
				llCurtir.setEnabled(true);
			}
		});
	}

	private String rotuloCategoria(String categoria)
	{
		if (categoria == null)
			return "Outros";

		switch (categoria)
		{
			case "BURACO": return "Buraco";
			case "ILUMINACAO": return "Iluminação";
			case "LIXO": return "Lixo";
			case "ARVORE": return "Árvore";
			case "OBRA": return "Obra";
			default: return "Outros";
		}
	}
}
