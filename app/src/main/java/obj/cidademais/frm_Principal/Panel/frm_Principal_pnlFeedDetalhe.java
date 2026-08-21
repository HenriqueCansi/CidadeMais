package obj.cidademais.frm_Principal.Panel;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Locale;

import obj.cidademais.Core.CmConstantes;
import obj.cidademais.Core.CmData;
import obj.cidademais.Core.CmUi;
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
	private ImageView ivConfirmar;
	private TextView tvNomePoster;
	private TextView tvTempo;
	private TextView tvTitulo;
	private TextView tvStatus;
	private TextView tvCategoria;
	private TextView tvEnderecoBairro;
	private TextView tvQtdImagens;
	private TextView tvCurtidas;
	private TextView tvConfirmacoes;
	private LinearLayout llFotos;
	private LinearLayout llCurtir;
	private LinearLayout llConfirmar;
	private LinearLayout llStatusAdmin;
	private LinearLayout chipAberta;
	private LinearLayout chipEmAnalise;
	private LinearLayout chipEncaminhada;
	private LinearLayout chipResolvida;
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
		tvStatus = layout.findViewById(R.id.tvStatus);
		tvCategoria = layout.findViewById(R.id.tvCategoria);
		tvEnderecoBairro = layout.findViewById(R.id.tvEnderecoBairro);
		tvQtdImagens = layout.findViewById(R.id.tvQtdImagens);
		llFotos = layout.findViewById(R.id.llFotos);
		btnVerNoMapa = layout.findViewById(R.id.btnVerNoMapa);
		llCurtir = layout.findViewById(R.id.llCurtir);
		ivCurtir = layout.findViewById(R.id.ivCurtir);
		tvCurtidas = layout.findViewById(R.id.tvCurtidas);
		llConfirmar = layout.findViewById(R.id.llConfirmar);
		ivConfirmar = layout.findViewById(R.id.ivConfirmar);
		tvConfirmacoes = layout.findViewById(R.id.tvConfirmacoes);
		llStatusAdmin = layout.findViewById(R.id.llStatusAdmin);
		chipAberta = layout.findViewById(R.id.chipAberta);
		chipEmAnalise = layout.findViewById(R.id.chipEmAnalise);
		chipEncaminhada = layout.findViewById(R.id.chipEncaminhada);
		chipResolvida = layout.findViewById(R.id.chipResolvida);

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
		llConfirmar.setOnClickListener(v -> alternarConfirmacao());

		chipAberta.setOnClickListener(v -> alterarStatus(CmConstantes.STATUS_ABERTA));
		chipEmAnalise.setOnClickListener(v -> alterarStatus(CmConstantes.STATUS_EM_ANALISE));
		chipEncaminhada.setOnClickListener(v -> alterarStatus(CmConstantes.STATUS_ENCAMINHADA));
		chipResolvida.setOnClickListener(v -> alterarStatus(CmConstantes.STATUS_RESOLVIDA));
	}

	private void carregarDados()
	{
		if (ocorrenciaAtual == null)
			return;

		tvTitulo.setText(ocorrenciaAtual.titulo);
		tvStatus.setText(CmConstantes.rotuloStatus(ocorrenciaAtual.status));
		tvStatus.getBackground().mutate().setTint(CmConstantes.corStatus(ocorrenciaAtual.status));
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
				CmUi.arredondar(iv, 16);

				Glide.with(RvActivity.__activity).load(url).into(iv);

				llFotos.addView(iv);
			}

		atualizarBotaoCurtir();
		atualizarBotaoConfirmar();

		boolean admin = isAdmin();
		llStatusAdmin.setVisibility(admin ? View.VISIBLE : View.GONE);

		if (admin)
			atualizarChipsStatus();

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
		RvActivity.mostrarCarregando("Enviando...");

		FirebaseOcorrencia.alternarCurtida(ocorrenciaAtual.id, uid, curtir, new CallbackCadastro()
		{
			@Override
			public void onSucesso()
			{
				RvActivity.esconderCarregando();

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
				RvActivity.esconderCarregando();

				Toast.makeText(RvActivity.__activity, e.getMessage(), Toast.LENGTH_SHORT).show();
				llCurtir.setEnabled(true);
			}
		});
	}

	private boolean isAdmin()
	{
		return Sessao.isLogado()
				&& CmConstantes.TIPO_USUARIO_ADMIN.equals(Sessao.getUsuario().tipoUsuario);
	}

	private void atualizarBotaoConfirmar()
	{
		boolean jaConfirmou = Sessao.isLogado()
				&& ocorrenciaAtual.confirmadoPor != null
				&& ocorrenciaAtual.confirmadoPor.contains(Sessao.getUsuario().uid);

		ivConfirmar.setColorFilter(Color.parseColor(jaConfirmou ? "#2E7D32" : "#CCCCCC"));
		tvConfirmacoes.setText(ocorrenciaAtual.confirmacoes + " Confirmações");
	}

	private void alternarConfirmacao()
	{
		if (!Sessao.isLogado())
		{
			Toast.makeText(RvActivity.__activity,
					"Faça login para confirmar uma ocorrência.",
					Toast.LENGTH_LONG).show();

			if (frm_Login_pnlLogin.__obj == null)
				frm_Login_pnlLogin.__obj = new frm_Login_pnlLogin();

			frm_Login_pnlLogin.__obj.Show();
			this.Hide();
			return;
		}

		String uid = Sessao.getUsuario().uid;
		boolean jaConfirmou = ocorrenciaAtual.confirmadoPor != null && ocorrenciaAtual.confirmadoPor.contains(uid);
		boolean confirmar = !jaConfirmou;

		llConfirmar.setEnabled(false);
		RvActivity.mostrarCarregando("Enviando...");

		FirebaseOcorrencia.alternarConfirmacao(ocorrenciaAtual.id, uid, confirmar, new CallbackCadastro()
		{
			@Override
			public void onSucesso()
			{
				RvActivity.esconderCarregando();

				if (ocorrenciaAtual.confirmadoPor == null)
					ocorrenciaAtual.confirmadoPor = new ArrayList<>();

				if (confirmar)
				{
					ocorrenciaAtual.confirmadoPor.add(uid);
					ocorrenciaAtual.confirmacoes++;
				}
				else
				{
					ocorrenciaAtual.confirmadoPor.remove(uid);
					ocorrenciaAtual.confirmacoes = Math.max(0, ocorrenciaAtual.confirmacoes - 1);
				}

				atualizarBotaoConfirmar();
				llConfirmar.setEnabled(true);
			}

			@Override
			public void onErro(Exception e)
			{
				RvActivity.esconderCarregando();

				Toast.makeText(RvActivity.__activity, e.getMessage(), Toast.LENGTH_SHORT).show();
				llConfirmar.setEnabled(true);
			}
		});
	}

	private void alterarStatus(String novoStatus)
	{
		if (!isAdmin())
			return;

		llStatusAdmin.setEnabled(false);
		RvActivity.mostrarCarregando("Atualizando...");

		FirebaseOcorrencia.atualizarStatus(ocorrenciaAtual.id, novoStatus, new CallbackCadastro()
		{
			@Override
			public void onSucesso()
			{
				RvActivity.esconderCarregando();

				ocorrenciaAtual.status = novoStatus;
				tvStatus.setText(CmConstantes.rotuloStatus(novoStatus));
				tvStatus.getBackground().mutate().setTint(CmConstantes.corStatus(novoStatus));
				atualizarChipsStatus();
				llStatusAdmin.setEnabled(true);

				Toast.makeText(RvActivity.__activity, "Status atualizado.", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onErro(Exception e)
			{
				RvActivity.esconderCarregando();

				Toast.makeText(RvActivity.__activity, e.getMessage(), Toast.LENGTH_SHORT).show();
				llStatusAdmin.setEnabled(true);
			}
		});
	}

	private void atualizarChipsStatus()
	{
		chipAberta.setBackgroundResource(R.drawable.bg_categoria);
		chipEmAnalise.setBackgroundResource(R.drawable.bg_categoria);
		chipEncaminhada.setBackgroundResource(R.drawable.bg_categoria);
		chipResolvida.setBackgroundResource(R.drawable.bg_categoria);

		String status = ocorrenciaAtual.status;

		if (CmConstantes.STATUS_EM_ANALISE.equals(status))
			chipEmAnalise.setBackgroundResource(R.drawable.bg_categoria_selecionada);
		else if (CmConstantes.STATUS_ENCAMINHADA.equals(status))
			chipEncaminhada.setBackgroundResource(R.drawable.bg_categoria_selecionada);
		else if (CmConstantes.STATUS_RESOLVIDA.equals(status))
			chipResolvida.setBackgroundResource(R.drawable.bg_categoria_selecionada);
		else
			chipAberta.setBackgroundResource(R.drawable.bg_categoria_selecionada);
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
