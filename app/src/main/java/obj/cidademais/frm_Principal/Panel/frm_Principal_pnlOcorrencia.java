package obj.cidademais.frm_Principal.Panel;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.core.content.ContextCompat;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import obj.cidademais.Core.CmCamera;
import obj.cidademais.Core.CmConstantes;
import obj.cidademais.Core.CmPermissao;
import obj.cidademais.Core.Localizacao.CmPosicao;
import obj.cidademais.Core.Localizacao.Localizacao;
import obj.cidademais.Core.Localizacao.LocalizacaoManager;
import obj.cidademais.Core.Localizacao.PermissaoManager;
import obj.cidademais.Firebase.Ocorrencia.FirebaseOcorrencia;
import obj.cidademais.R;
import obj.cidademais.RvActivity;
import obj.cidademais.RvView;
import obj.cidademais.frm_Login.Classe.CallbackCadastro;
import obj.cidademais.frm_Login.Data.Sessao;
import obj.cidademais.frm_Login.Panel.frm_Login_pnlLogin;
import obj.cidademais.frm_Principal.Data.Ocorrencia;

public class frm_Principal_pnlOcorrencia extends RvView
{
	public static frm_Principal_pnlOcorrencia __obj = new frm_Principal_pnlOcorrencia();
	private LinearLayout layout;
	private FirebaseAuth mAuth;
	EditText edtTitulo;
	EditText edtDescricao;


	LinearLayout llFotosPreview;

	TextView txtEndereco;
	TextView txtCidade;

	Button btnAdicionarImagem;

	Button btnCancelar;
	Button btnReportar;

	Button btnAlterarLocalizacao;
	private LinearLayout cardBuraco;
	private LinearLayout cardIluminacao;
	private LinearLayout cardLixo;
	private LinearLayout cardArvore;
	private LinearLayout cardObra;
	private LinearLayout cardOutros;
	private CmPosicao posicaoAtual;
	private String categoriaSelecionada = "";
	private static final int MAX_FOTOS = 5;
	private final List<Uri> fotosSelecionadas = new ArrayList<>();
	@Override
	public LinearLayout getLayout()
	{
		if (layout == null)
		{
			LayoutInflater inflater = LayoutInflater.from(RvActivity.__activity);
			layout = (LinearLayout) inflater.inflate(R.layout.frm_principal_pnlocorrencia, null);
		}
		return layout;
	}

	@Override
	public void OnActivated()
	{
		 edtTitulo = layout.findViewById(R.id.edtTitulo);
		 edtDescricao = layout.findViewById(R.id.edtDescricao);

		 llFotosPreview = layout.findViewById(R.id.llFotosPreview);

		 txtEndereco = layout.findViewById(R.id.txtEndereco);
		 txtCidade = layout.findViewById(R.id.txtCidade);

		 btnAdicionarImagem = layout.findViewById(R.id.btnAdicionarImagem);

		 btnCancelar = layout.findViewById(R.id.btnCancelar);
		 btnReportar = layout.findViewById(R.id.btnReportar);

		 btnAlterarLocalizacao = layout.findViewById(R.id.btnAlterarLocalizacao);
		cardBuraco = layout.findViewById(R.id.cardBuraco);
		cardIluminacao = layout.findViewById(R.id.cardIluminacao);
		cardLixo = layout.findViewById(R.id.cardLixo);
		cardArvore = layout.findViewById(R.id.cardArvore);
		cardObra = layout.findViewById(R.id.cardObra);
		cardOutros = layout.findViewById(R.id.cardOutros);

		limparFormulario();

		cardBuraco.setOnClickListener(v -> selecionarCategoria(cardBuraco, "BURACO"));
		cardIluminacao.setOnClickListener(v -> selecionarCategoria(cardIluminacao, "ILUMINACAO"));
		cardLixo.setOnClickListener(v -> selecionarCategoria(cardLixo, "LIXO"));
		cardArvore.setOnClickListener(v -> selecionarCategoria(cardArvore, "ARVORE"));
		cardObra.setOnClickListener(v -> selecionarCategoria(cardObra, "OBRA"));
		cardOutros.setOnClickListener(v -> selecionarCategoria(cardOutros, "OUTROS"));

		carregarLocalizacao();

		btnAdicionarImagem.setOnClickListener(v -> abrirCamera());

		btnAlterarLocalizacao.setOnClickListener(v -> {

			Toast.makeText(RvActivity.__activity,
					"Abrir mapa",
					Toast.LENGTH_SHORT).show();

			// Abrir mapa para selecionar outro local
		});

		btnCancelar.setOnClickListener(v -> {

			frm_Principal_pnlPrincipal.__obj.Show();
			Hide();

		});

		btnReportar.setOnClickListener(v -> {

			if (!Sessao.isLogado())
			{
				Toast.makeText(RvActivity.__activity,
						"Faça login para reportar uma ocorrência.",
						Toast.LENGTH_LONG).show();

				if (frm_Login_pnlLogin.__obj == null)
					frm_Login_pnlLogin.__obj = new frm_Login_pnlLogin();

				frm_Login_pnlLogin.__obj.Show();
				Hide();
				return;
			}

			String titulo = edtTitulo.getText().toString().trim();
			String descricao = edtDescricao.getText().toString().trim();

			if (titulo.isEmpty())
			{
				Toast.makeText(RvActivity.__activity,
						"Informe o título.",
						Toast.LENGTH_SHORT).show();
				return;
			}

			if (descricao.isEmpty())
			{
				Toast.makeText(RvActivity.__activity,
						"Informe a descrição.",
						Toast.LENGTH_SHORT).show();
				return;
			}

			if (categoriaSelecionada.isEmpty())
			{
				Toast.makeText(RvActivity.__activity,
						"Selecione uma categoria.",
						Toast.LENGTH_SHORT).show();
				return;
			}

			if (fotosSelecionadas.isEmpty())
			{
				Toast.makeText(RvActivity.__activity,
						"Tire uma foto.",
						Toast.LENGTH_SHORT).show();
				return;
			}

			cadastrarOcorrencia(
					titulo,
					descricao);

		});
	}

	private void limparFormulario()
	{
		edtTitulo.setText("");
		edtDescricao.setText("");

		fotosSelecionadas.clear();
		atualizarPreviewFotos();

		categoriaSelecionada = "";

		cardBuraco.setBackgroundResource(R.drawable.bg_categoria);
		cardIluminacao.setBackgroundResource(R.drawable.bg_categoria);
		cardLixo.setBackgroundResource(R.drawable.bg_categoria);
		cardArvore.setBackgroundResource(R.drawable.bg_categoria);
		cardObra.setBackgroundResource(R.drawable.bg_categoria);
		cardOutros.setBackgroundResource(R.drawable.bg_categoria);

		cardBuraco.setScaleX(1f);
		cardBuraco.setScaleY(1f);
		cardIluminacao.setScaleX(1f);
		cardIluminacao.setScaleY(1f);
		cardLixo.setScaleX(1f);
		cardLixo.setScaleY(1f);
		cardArvore.setScaleX(1f);
		cardArvore.setScaleY(1f);
		cardObra.setScaleX(1f);
		cardObra.setScaleY(1f);
		cardOutros.setScaleX(1f);
		cardOutros.setScaleY(1f);
	}

	private void atualizarPreviewFotos()
	{
		llFotosPreview.removeAllViews();

		for (Uri uri : fotosSelecionadas)
		{
			FrameLayout item = new FrameLayout(RvActivity.__activity);

			LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(dpParaPx(90), dpParaPx(90));
			itemParams.setMarginEnd(dpParaPx(8));
			item.setLayoutParams(itemParams);

			ImageView thumb = new ImageView(RvActivity.__activity);
			thumb.setLayoutParams(new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.MATCH_PARENT));
			thumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
			thumb.setImageURI(uri);
			item.addView(thumb);

			ImageView remover = new ImageView(RvActivity.__activity);
			FrameLayout.LayoutParams removerParams = new FrameLayout.LayoutParams(dpParaPx(22), dpParaPx(22));
			removerParams.gravity = Gravity.TOP | Gravity.END;
			remover.setLayoutParams(removerParams);
			remover.setImageResource(R.drawable.ic_fechar);
			remover.setBackgroundColor(0xAAFFFFFF);
			remover.setOnClickListener(v -> {
				fotosSelecionadas.remove(uri);
				atualizarPreviewFotos();
			});
			item.addView(remover);

			llFotosPreview.addView(item);
		}

		btnAdicionarImagem.setEnabled(fotosSelecionadas.size() < MAX_FOTOS);
		btnAdicionarImagem.setText(fotosSelecionadas.isEmpty()
				? "Adicionar Foto"
				: "Adicionar Foto (" + fotosSelecionadas.size() + "/" + MAX_FOTOS + ")");
	}

	private int dpParaPx(int dp)
	{
		float densidade = RvActivity.__activity.getResources().getDisplayMetrics().density;
		return Math.round(dp * densidade);
	}

	private void abrirCamera()
	{
		if (fotosSelecionadas.size() >= MAX_FOTOS)
		{
			Toast.makeText(RvActivity.__activity,
					"Máximo de " + MAX_FOTOS + " fotos.",
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (ContextCompat.checkSelfPermission(RvActivity.__activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
		{
			CmPermissao.solicitarPermissoes(new CmPermissao.Callback()
			{
				@Override
				public void onPermitido()
				{
					abrirCamera();
				}

				@Override
				public void onNegado()
				{
					Toast.makeText(RvActivity.__activity,
							"Permissão de câmera negada.",
							Toast.LENGTH_SHORT).show();
				}
			});
			return;
		}

		CmCamera.tirarFoto(new CmCamera.Callback()
		{
			@Override
			public void onSucesso(Uri uri)
			{
				fotosSelecionadas.add(uri);
				atualizarPreviewFotos();
			}

			@Override
			public void onErro(String erro)
			{
				Toast.makeText(RvActivity.__activity, erro, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void carregarLocalizacao()
	{
		if (!PermissaoManager.possuiPermissaoLocalizacao())
		{
			PermissaoManager.solicitarPermissaoLocalizacao();

			txtEndereco.setText("Aguardando permissão...");
			return;
		}

		txtEndereco.setText("Obtendo localização...");
		txtCidade.setText("");

		LocalizacaoManager.buscar(new LocalizacaoManager.Callback()
		{
			@Override
			public void onSucesso(CmPosicao posicao)
			{
				posicaoAtual = posicao;
				CmPosicao.posicaoAtual = posicao;

				txtEndereco.setText(posicao.localizacao.endereco);

				txtCidade.setText(
						posicao.localizacao.bairro + "\n" +
								posicao.localizacao.cidade + " - " +
								posicao.localizacao.estado);
			}

			@Override
			public void onErro(String erro)
			{
				txtEndereco.setText("Não foi possível localizar.");
				txtCidade.setText(erro);
			}
		});
	}
	private void cadastrarOcorrencia(String titulo, String descricao)
	{
		Ocorrencia ocorrencia = new Ocorrencia();

		ocorrencia.uidUsuario = Sessao.getUsuario().uid;

		ocorrencia.titulo = titulo;

		ocorrencia.descricao = descricao;

		ocorrencia.categoria = categoriaSelecionada;

		ocorrencia.status = CmConstantes.STATUS_ABERTA;

		ocorrencia.latitude = CmPosicao.posicaoAtual.latitude;

		ocorrencia.longitude = CmPosicao.posicaoAtual.longitude;

		ocorrencia.endereco = CmPosicao.posicaoAtual.localizacao.endereco;

		ocorrencia.cidade = CmPosicao.posicaoAtual.localizacao.cidade;

		ocorrencia.bairro = CmPosicao.posicaoAtual.localizacao.bairro;

		ocorrencia.curtidas = 0;

		ocorrencia.curtidoPor = new ArrayList<>();

		ocorrencia.confirmacoes = 0;

		ocorrencia.confirmadoPor = new ArrayList<>();

		ocorrencia.comentarios = 0;

		ocorrencia.criadoEm = Timestamp.now();

		ocorrencia.atualizadoEm = Timestamp.now();

		FirebaseOcorrencia.cadastrarComFotos(
				ocorrencia,
				fotosSelecionadas,
				new CallbackCadastro()
				{
					@Override
					public void onSucesso()
					{
						Toast.makeText(
								RvActivity.__activity,
								"Ocorrência cadastrada!",
								Toast.LENGTH_SHORT).show();

						frm_Principal_pnlPrincipal.__obj.Show();
						Hide();
					}

					@Override
					public void onErro(Exception e)
					{
						Toast.makeText(
								RvActivity.__activity,
								e.getMessage(),
								Toast.LENGTH_LONG).show();
					}
				});
	}

	public void ShowCustom()
	{

	}

	private void selecionarCategoria(LinearLayout selecionado, String categoria)
	{
		categoriaSelecionada = categoria;

		cardBuraco.setBackgroundResource(R.drawable.bg_categoria);
		cardIluminacao.setBackgroundResource(R.drawable.bg_categoria);
		cardLixo.setBackgroundResource(R.drawable.bg_categoria);
		cardArvore.setBackgroundResource(R.drawable.bg_categoria);
		cardObra.setBackgroundResource(R.drawable.bg_categoria);
		cardOutros.setBackgroundResource(R.drawable.bg_categoria);

		cardBuraco.animate().scaleX(1f).scaleY(1f).setDuration(120);
		cardIluminacao.animate().scaleX(1f).scaleY(1f).setDuration(120);
		cardLixo.animate().scaleX(1f).scaleY(1f).setDuration(120);
		cardArvore.animate().scaleX(1f).scaleY(1f).setDuration(120);
		cardObra.animate().scaleX(1f).scaleY(1f).setDuration(120);
		cardOutros.animate().scaleX(1f).scaleY(1f).setDuration(120);

		selecionado.setBackgroundResource(R.drawable.bg_categoria_selecionada);

		selecionado.animate()
				.scaleX(1.06f)
				.scaleY(1.06f)
				.setDuration(120);
	}

}
