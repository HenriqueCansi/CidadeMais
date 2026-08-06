package obj.cidademais.frm_Principal.Panel;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import obj.cidademais.Core.Localizacao.CmGPS;
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
import obj.cidademais.frm_Principal.Data.Ocorrencia;

public class frm_Principal_pnlOcorrencia extends RvView
{
	public static frm_Principal_pnlOcorrencia __obj = new frm_Principal_pnlOcorrencia();
	private LinearLayout layout;
	private FirebaseAuth mAuth;
	EditText edtTitulo;
	EditText edtDescricao;


	ImageView imgPreview;

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

		 imgPreview = layout.findViewById(R.id.imgPreview);

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

		cardBuraco.setOnClickListener(v -> selecionarCategoria(cardBuraco, "BURACO"));
		cardIluminacao.setOnClickListener(v -> selecionarCategoria(cardIluminacao, "ILUMINACAO"));
		cardLixo.setOnClickListener(v -> selecionarCategoria(cardLixo, "LIXO"));
		cardArvore.setOnClickListener(v -> selecionarCategoria(cardArvore, "ARVORE"));
		cardObra.setOnClickListener(v -> selecionarCategoria(cardObra, "OBRA"));
		cardOutros.setOnClickListener(v -> selecionarCategoria(cardOutros, "OUTROS"));

		carregarLocalizacao();

		btnAdicionarImagem.setOnClickListener(v -> {

			Toast.makeText(RvActivity.__activity,
					"Selecionar imagem",
					Toast.LENGTH_SHORT).show();

			// Abrir câmera/galeria
		});

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

			String categoria = categoriaSelecionada;

			cadastrarOcorrencia(
					titulo,
					descricao);

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

		CmGPS.buscar(new CmGPS.Callback()
		{
			@Override
			public void onSucesso(CmPosicao posicao)
			{
				posicaoAtual = posicao;

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

		ocorrencia.status = "ABERTA";

		ocorrencia.latitude = CmPosicao.posicaoAtual.latitude;

		ocorrencia.longitude = CmPosicao.posicaoAtual.longitude;

		ocorrencia.endereco = CmPosicao.posicaoAtual.localizacao.endereco;

		ocorrencia.cidade = CmPosicao.posicaoAtual.localizacao.cidade;

		ocorrencia.curtidas = 0;

		ocorrencia.confirmacoes = 0;

		ocorrencia.comentarios = 0;

		ocorrencia.criadoEm = Timestamp.now();

		ocorrencia.atualizadoEm = Timestamp.now();

		FirebaseOcorrencia.cadastrar(
				ocorrencia,
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
