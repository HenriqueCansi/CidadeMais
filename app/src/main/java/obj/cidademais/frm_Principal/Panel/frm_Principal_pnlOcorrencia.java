package obj.cidademais.frm_Principal.Panel;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import obj.cidademais.Core.CmCamera;
import obj.cidademais.Core.CmConstantes;
import obj.cidademais.Core.CmGaleria;
import obj.cidademais.Core.CmPermissao;
import obj.cidademais.Core.CmUi;
import obj.cidademais.Core.Localizacao.CmGeo;
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
	Button btnEscolherGaleria;

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
	// true quando o admin escolheu o local no mapa — impede que o GPS sobrescreva a escolha
	private boolean localizacaoManual = false;
	private String categoriaSelecionada = "";
	private static final int MAX_FOTOS = 5;
	private static final double RAIO_PROXIMIDADE_KM = 0.05;
	private static final int LIMITE_OCORRENCIAS_PROXIMAS = 3;
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
		 btnEscolherGaleria = layout.findViewById(R.id.btnEscolherGaleria);

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

		posicaoAtual = null;
		localizacaoManual = false;

		cardBuraco.setOnClickListener(v -> selecionarCategoria(cardBuraco, "BURACO"));
		cardIluminacao.setOnClickListener(v -> selecionarCategoria(cardIluminacao, "ILUMINACAO"));
		cardLixo.setOnClickListener(v -> selecionarCategoria(cardLixo, "LIXO"));
		cardArvore.setOnClickListener(v -> selecionarCategoria(cardArvore, "ARVORE"));
		cardObra.setOnClickListener(v -> selecionarCategoria(cardObra, "OBRA"));
		cardOutros.setOnClickListener(v -> selecionarCategoria(cardOutros, "OUTROS"));

		carregarLocalizacao();

		btnAdicionarImagem.setOnClickListener(v -> abrirCamera());

		// Galeria somente para admin (usado em testes) — usuário comum só pode tirar foto na hora
		btnEscolherGaleria.setVisibility(isAdmin() ? View.VISIBLE : View.GONE);

		btnEscolherGaleria.setOnClickListener(v -> {

			if (!isAdmin())
				return;

			abrirGaleria();
		});

		btnAlterarLocalizacao.setVisibility(isAdmin() ? View.VISIBLE : View.GONE);

		btnAlterarLocalizacao.setOnClickListener(v -> {

			if (!isAdmin())
				return;

			abrirSeletorLocalizacao();
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

			if (obterPosicaoOcorrencia() == null)
			{
				Toast.makeText(RvActivity.__activity,
						"Aguarde a localização ser obtida.",
						Toast.LENGTH_SHORT).show();
				return;
			}

			RvActivity.mostrarCarregando("Enviando...");

			Ocorrencia novaOcorrencia = montarOcorrencia(titulo, descricao);
			verificarOcorrenciasProximas(novaOcorrencia);

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
			CmUi.arredondar(item, 12);

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
		btnEscolherGaleria.setEnabled(fotosSelecionadas.size() < MAX_FOTOS);
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

	private void abrirGaleria()
	{
		if (fotosSelecionadas.size() >= MAX_FOTOS)
		{
			Toast.makeText(RvActivity.__activity,
					"Máximo de " + MAX_FOTOS + " fotos.",
					Toast.LENGTH_SHORT).show();
			return;
		}

		CmGaleria.abrirGaleria(new CmGaleria.Callback()
		{
			@Override
			public void onSucesso(Uri fotoUri)
			{
				fotosSelecionadas.add(fotoUri);
				atualizarPreviewFotos();
			}

			@Override
			public void onErro(String erro)
			{
				Toast.makeText(RvActivity.__activity, erro, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private boolean isAdmin()
	{
		return Sessao.isLogado()
				&& CmConstantes.TIPO_USUARIO_ADMIN.equals(Sessao.getUsuario().tipoUsuario);
	}

	private void carregarLocalizacao()
	{
		if (!PermissaoManager.possuiPermissaoLocalizacao())
		{
			txtEndereco.setText("Aguardando permissão...");

			CmPermissao.solicitarLocalizacao(new CmPermissao.Callback()
			{
				@Override
				public void onPermitido()
				{
					carregarLocalizacao();
				}

				@Override
				public void onNegado()
				{
					txtEndereco.setText("Permissão de localização negada.");
				}
			});
			return;
		}

		txtEndereco.setText("Obtendo localização...");
		txtCidade.setText("");

		LocalizacaoManager.buscar(new LocalizacaoManager.Callback()
		{
			@Override
			public void onSucesso(CmPosicao posicao)
			{
				CmPosicao.posicaoAtual = posicao;

				if (localizacaoManual)
					return;

				posicaoAtual = posicao;
				exibirPosicao(posicao);
			}

			@Override
			public void onErro(String erro)
			{
				if (localizacaoManual)
					return;

				txtEndereco.setText("Não foi possível localizar.");
				txtCidade.setText(erro);
			}
		});
	}

	private void exibirPosicao(CmPosicao posicao)
	{
		txtEndereco.setText(posicao.localizacao.endereco != null
				? posicao.localizacao.endereco
				: String.format(Locale.US, "%.6f, %.6f", posicao.latitude, posicao.longitude));

		String bairro = posicao.localizacao.bairro != null ? posicao.localizacao.bairro : "";
		String cidade = posicao.localizacao.cidade != null ? posicao.localizacao.cidade : "";
		String estado = posicao.localizacao.estado != null ? posicao.localizacao.estado : "";

		txtCidade.setText(bairro + "\n" + cidade + " - " + estado);
	}

	// Posição gravada na ocorrência: a obtida/escolhida nesta tela, ou a última conhecida do app
	private CmPosicao obterPosicaoOcorrencia()
	{
		return posicaoAtual != null ? posicaoAtual : CmPosicao.posicaoAtual;
	}

	// Somente admin: abre um mapa em diálogo para escolher manualmente o local da ocorrência (usado em testes)
	private void abrirSeletorLocalizacao()
	{
		LinearLayout container = new LinearLayout(RvActivity.__activity);
		container.setOrientation(LinearLayout.VERTICAL);

		TextView txtDica = new TextView(RvActivity.__activity);
		txtDica.setText("Toque no mapa (ou arraste o marcador) para escolher o local.");
		txtDica.setPadding(dpParaPx(20), dpParaPx(4), dpParaPx(20), dpParaPx(8));
		container.addView(txtDica);

		MapView mapaSeletor = new MapView(RvActivity.__activity);
		mapaSeletor.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, dpParaPx(400)));
		container.addView(mapaSeletor);

		mapaSeletor.onCreate(new Bundle());
		mapaSeletor.onStart();
		mapaSeletor.onResume();

		final LatLng[] escolhido = new LatLng[1];

		CmPosicao inicial = obterPosicaoOcorrencia();
		if (inicial != null)
			escolhido[0] = new LatLng(inicial.latitude, inicial.longitude);

		mapaSeletor.getMapAsync(map -> {
			final Marker[] marcador = new Marker[1];

			if (escolhido[0] != null)
			{
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(escolhido[0], 16f));
				marcador[0] = map.addMarker(new MarkerOptions().position(escolhido[0]).draggable(true));
			}

			map.setOnMapClickListener(latLng -> {
				escolhido[0] = latLng;

				if (marcador[0] == null)
					marcador[0] = map.addMarker(new MarkerOptions().position(latLng).draggable(true));
				else
					marcador[0].setPosition(latLng);
			});

			map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener()
			{
				@Override
				public void onMarkerDragStart(Marker marker)
				{
				}

				@Override
				public void onMarkerDrag(Marker marker)
				{
				}

				@Override
				public void onMarkerDragEnd(Marker marker)
				{
					escolhido[0] = marker.getPosition();
				}
			});
		});

		AlertDialog dialog = new AlertDialog.Builder(RvActivity.__activity)
				.setTitle("Alterar localização")
				.setView(container)
				.setNegativeButton("Cancelar", null)
				.setPositiveButton("Confirmar", (d, which) -> {
					if (escolhido[0] == null)
					{
						Toast.makeText(RvActivity.__activity,
								"Nenhum local selecionado.",
								Toast.LENGTH_SHORT).show();
						return;
					}

					aplicarLocalizacaoManual(escolhido[0]);
				})
				.create();

		dialog.setOnDismissListener(d -> {
			mapaSeletor.onPause();
			mapaSeletor.onStop();
			mapaSeletor.onDestroy();
		});

		dialog.show();
	}

	private void aplicarLocalizacaoManual(LatLng latLng)
	{
		CmPosicao posicao;

		try
		{
			posicao = LocalizacaoManager.geocodificar(latLng.latitude, latLng.longitude);
		}
		catch (IOException e)
		{
			// Sem endereço (ex.: sem internet) — mantém só as coordenadas
			posicao = new CmPosicao();
			posicao.latitude = latLng.latitude;
			posicao.longitude = latLng.longitude;
		}

		localizacaoManual = true;
		posicaoAtual = posicao;
		exibirPosicao(posicao);
	}

	private Ocorrencia montarOcorrencia(String titulo, String descricao)
	{
		Ocorrencia ocorrencia = new Ocorrencia();
		CmPosicao posicao = obterPosicaoOcorrencia();

		ocorrencia.uidUsuario = Sessao.getUsuario().uid;

		ocorrencia.titulo = titulo;

		ocorrencia.descricao = descricao;

		ocorrencia.categoria = categoriaSelecionada;

		ocorrencia.status = CmConstantes.STATUS_ABERTA;

		ocorrencia.latitude = posicao.latitude;

		ocorrencia.longitude = posicao.longitude;

		ocorrencia.endereco = posicao.localizacao.endereco;

		ocorrencia.cidade = posicao.localizacao.cidade;

		ocorrencia.bairro = posicao.localizacao.bairro;

		ocorrencia.curtidas = 0;

		ocorrencia.curtidoPor = new ArrayList<>();

		ocorrencia.confirmacoes = 0;

		ocorrencia.confirmadoPor = new ArrayList<>();

		ocorrencia.comentarios = 0;

		ocorrencia.criadoEm = Timestamp.now();

		ocorrencia.atualizadoEm = Timestamp.now();

		return ocorrencia;
	}

	private void verificarOcorrenciasProximas(Ocorrencia novaOcorrencia)
	{
		FirebaseOcorrencia.listarTodas(new FirebaseOcorrencia.CallbackListagem()
		{
			@Override
			public void onSucesso(List<Ocorrencia> lista)
			{
				List<Ocorrencia> proximas = new ArrayList<>();

				for (Ocorrencia oc : lista)
				{
					if (!novaOcorrencia.categoria.equals(oc.categoria))
						continue;

					if (CmConstantes.STATUS_RESOLVIDA.equals(oc.status))
						continue;

					if (oc.latitude == 0 && oc.longitude == 0)
						continue;

					double distanciaKm = CmGeo.distanciaKm(
							novaOcorrencia.latitude, novaOcorrencia.longitude,
							oc.latitude, oc.longitude);

					if (distanciaKm <= RAIO_PROXIMIDADE_KM)
						proximas.add(oc);
				}

				Collections.sort(proximas, (a, b) -> Double.compare(
						CmGeo.distanciaKm(novaOcorrencia.latitude, novaOcorrencia.longitude, a.latitude, a.longitude),
						CmGeo.distanciaKm(novaOcorrencia.latitude, novaOcorrencia.longitude, b.latitude, b.longitude)));

				if (proximas.size() > LIMITE_OCORRENCIAS_PROXIMAS)
					proximas = proximas.subList(0, LIMITE_OCORRENCIAS_PROXIMAS);

				if (proximas.isEmpty())
				{
					cadastrarOcorrencia(novaOcorrencia);
					return;
				}

				RvActivity.esconderCarregando();

				if (frm_Principal_pnlOcorrenciaProxima.__obj == null)
					frm_Principal_pnlOcorrenciaProxima.__obj = new frm_Principal_pnlOcorrenciaProxima();

				frm_Principal_pnlOcorrenciaProxima.__obj.preparar(proximas, novaOcorrencia, new ArrayList<>(fotosSelecionadas));
				frm_Principal_pnlOcorrenciaProxima.__obj.Show();
				Hide();
			}

			@Override
			public void onErro(Exception e)
			{
				cadastrarOcorrencia(novaOcorrencia);
			}
		});
	}

	private void cadastrarOcorrencia(Ocorrencia ocorrencia)
	{
		FirebaseOcorrencia.cadastrarComFotos(
				ocorrencia,
				fotosSelecionadas,
				new CallbackCadastro()
				{
					@Override
					public void onSucesso()
					{
						RvActivity.esconderCarregando();

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
						RvActivity.esconderCarregando();

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
