package obj.cidademais.frm_Principal.Panel;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import obj.cidademais.Core.CmData;
import obj.cidademais.Core.Localizacao.CmGeo;
import obj.cidademais.Core.Localizacao.CmPosicao;
import obj.cidademais.Firebase.Ocorrencia.FirebaseOcorrencia;
import obj.cidademais.Firebase.Usuario.FirebaseUsuario;
import obj.cidademais.R;
import obj.cidademais.RvActivity;
import obj.cidademais.RvView;
import obj.cidademais.frm_Login.Data.Usuario;
import obj.cidademais.frm_Perfil_pnlPrincipal.Panel.frm_Perfil_pnlPrincipal;
import obj.cidademais.frm_Principal.Data.Ocorrencia;

public class frm_Principal_pnlFeed extends RvView
{
	public static frm_Principal_pnlFeed __obj = new frm_Principal_pnlFeed();
	private LinearLayout layout;

	private EditText etSearch;
	private ImageView ivNotification;
	private SeekBar sbRaio;
	private TextView tvRaioValor;
	private LinearLayout llDestaques;
	private LinearLayout llRecentes;

	private List<Ocorrencia> todasOcorrencias = new ArrayList<>();
	private Map<String, Usuario> cacheUsuarios = new HashMap<>();
	private int raioKm = 1;

	@Override
	public LinearLayout getLayout()
	{
		if (layout == null)
		{
			LayoutInflater inflater = LayoutInflater.from(RvActivity.__activity);
			layout = (LinearLayout) inflater.inflate(R.layout.frm_principal_pnlfeed, null);
		}
		return layout;
	}

	@Override
	public void OnActivated()
	{
		etSearch = layout.findViewById(R.id.etSearch);
		ivNotification = layout.findViewById(R.id.ivNotification);
		sbRaio = layout.findViewById(R.id.sbRaio);
		tvRaioValor = layout.findViewById(R.id.tvRaioValor);
		llDestaques = layout.findViewById(R.id.llDestaques);
		llRecentes = layout.findViewById(R.id.llRecentes);

		ImageView ivMap = layout.findViewById(R.id.ivMap);
		ImageView ivAdd = layout.findViewById(R.id.ivAdd);
		ImageView ivProfile = layout.findViewById(R.id.ivProfile);

		ivNotification.setOnClickListener(v -> {
			// Ação da notificação
		});

		ivMap.setOnClickListener(v -> {
			frm_Principal_pnlPrincipal.__obj.Show();
			this.Hide();
		});

		ivAdd.setOnClickListener(v -> {
			if (frm_Principal_pnlOcorrencia.__obj == null)
				frm_Principal_pnlOcorrencia.__obj = new frm_Principal_pnlOcorrencia();

			frm_Principal_pnlOcorrencia.__obj.Show();
			frm_Principal_pnlOcorrencia.__obj.ShowCustom();
			this.Hide();
		});

		ivProfile.setOnClickListener(v -> {
			if (frm_Perfil_pnlPrincipal.__obj == null)
				frm_Perfil_pnlPrincipal.__obj = new frm_Perfil_pnlPrincipal();

			frm_Perfil_pnlPrincipal.__obj.Show();
			frm_Perfil_pnlPrincipal.__obj.ShowCustom();
			this.Hide();
		});

		raioKm = sbRaio.getProgress() + 1;
		tvRaioValor.setText(String.format(Locale.getDefault(), "%02dkm", raioKm));

		sbRaio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				raioKm = progress + 1;
				tvRaioValor.setText(String.format(Locale.getDefault(), "%02dkm", raioKm));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				reconstruirFeed();
			}
		});

		carregarOcorrencias();
	}

	private void carregarOcorrencias()
	{
		FirebaseOcorrencia.listarTodas(new FirebaseOcorrencia.CallbackListagem()
		{
			@Override
			public void onSucesso(List<Ocorrencia> lista)
			{
				todasOcorrencias = lista;
				reconstruirFeed();
			}

			@Override
			public void onErro(Exception e)
			{
				Toast.makeText(RvActivity.__activity, e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void reconstruirFeed()
	{
		List<Ocorrencia> filtradas = filtrarPorRaio(todasOcorrencias);
		montarDestaques(filtradas);
		montarRecentes(filtradas);
	}

	private List<Ocorrencia> filtrarPorRaio(List<Ocorrencia> lista)
	{
		if (CmPosicao.posicaoAtual == null)
			return lista;

		List<Ocorrencia> filtradas = new ArrayList<>();

		for (Ocorrencia oc : lista)
		{
			if (oc.latitude == 0 && oc.longitude == 0)
				continue;

			double distancia = CmGeo.distanciaKm(
					CmPosicao.posicaoAtual.latitude,
					CmPosicao.posicaoAtual.longitude,
					oc.latitude,
					oc.longitude);

			if (distancia <= raioKm)
				filtradas.add(oc);
		}

		return filtradas;
	}

	private void montarDestaques(List<Ocorrencia> filtradas)
	{
		List<Ocorrencia> ordenadas = new ArrayList<>(filtradas);
		Collections.sort(ordenadas, (a, b) -> b.curtidas - a.curtidas);

		llDestaques.removeAllViews();

		int limite = Math.min(5, ordenadas.size());

		for (int i = 0; i < limite; i++)
			llDestaques.addView(criarCardDestaque(ordenadas.get(i), i + 1));
	}

	private void montarRecentes(List<Ocorrencia> filtradas)
	{
		List<Ocorrencia> ordenadas = new ArrayList<>(filtradas);
		Collections.sort(ordenadas, (a, b) -> {
			if (a.criadoEm == null || b.criadoEm == null)
				return 0;
			return b.criadoEm.compareTo(a.criadoEm);
		});

		llRecentes.removeAllViews();

		for (Ocorrencia oc : ordenadas)
			llRecentes.addView(criarCardRecente(oc));
	}

	private View criarCardDestaque(Ocorrencia oc, int rank)
	{
		LayoutInflater inflater = LayoutInflater.from(RvActivity.__activity);
		View card = inflater.inflate(R.layout.item_feed_destaque, llDestaques, false);

		TextView tvRank = card.findViewById(R.id.tvRank);
		ImageView imgFoto = card.findViewById(R.id.imgFoto);
		TextView tvCurtidas = card.findViewById(R.id.tvCurtidas);
		TextView tvCategoria = card.findViewById(R.id.tvCategoria);
		TextView tvBairro = card.findViewById(R.id.tvBairro);

		tvRank.setText("#" + rank);
		tvCurtidas.setText(String.valueOf(oc.curtidas));
		tvCategoria.setText(rotuloCategoria(oc.categoria));
		tvBairro.setText(oc.bairro != null ? oc.bairro : "");

		if (oc.fotos != null && !oc.fotos.isEmpty())
			Glide.with(RvActivity.__activity).load(oc.fotos.get(0)).into(imgFoto);

		card.setOnClickListener(v -> abrirDetalhe(oc));

		return card;
	}

	private View criarCardRecente(Ocorrencia oc)
	{
		LayoutInflater inflater = LayoutInflater.from(RvActivity.__activity);
		View card = inflater.inflate(R.layout.item_feed_recente, llRecentes, false);

		ImageView imgAvatar = card.findViewById(R.id.imgAvatar);
		TextView tvNome = card.findViewById(R.id.tvNome);
		TextView tvTempo = card.findViewById(R.id.tvTempo);
		TextView tvBairro = card.findViewById(R.id.tvBairro);
		TextView tvTitulo = card.findViewById(R.id.tvTitulo);
		ImageView imgFoto = card.findViewById(R.id.imgFoto);

		tvNome.setText("...");
		tvTempo.setText(CmData.tempoRelativo(oc.criadoEm));
		tvBairro.setText(oc.bairro != null ? oc.bairro : "");
		tvTitulo.setText(oc.titulo);

		if (oc.fotos != null && !oc.fotos.isEmpty())
			Glide.with(RvActivity.__activity).load(oc.fotos.get(0)).into(imgFoto);

		resolverUsuario(oc.uidUsuario, usuario -> {
			if (usuario != null)
			{
				tvNome.setText(usuario.nome);

				if (usuario.fotoPerfil != null && !usuario.fotoPerfil.isEmpty())
					Glide.with(RvActivity.__activity).load(usuario.fotoPerfil).into(imgAvatar);
			}
			else
				tvNome.setText("Usuário");
		});

		card.setOnClickListener(v -> abrirDetalhe(oc));

		return card;
	}

	private interface UsuarioCallback
	{
		void onUsuario(Usuario usuario);
	}

	private void resolverUsuario(String uid, UsuarioCallback callback)
	{
		if (cacheUsuarios.containsKey(uid))
		{
			callback.onUsuario(cacheUsuarios.get(uid));
			return;
		}

		FirebaseUsuario.buscar(uid, new FirebaseUsuario.CallbackBusca()
		{
			@Override
			public void onSucesso(Usuario usuario)
			{
				cacheUsuarios.put(uid, usuario);
				callback.onUsuario(usuario);
			}

			@Override
			public void onErro(Exception e)
			{
				callback.onUsuario(null);
			}
		});
	}

	private void abrirDetalhe(Ocorrencia oc)
	{
		if (frm_Principal_pnlFeedDetalhe.__obj == null)
			frm_Principal_pnlFeedDetalhe.__obj = new frm_Principal_pnlFeedDetalhe();

		frm_Principal_pnlFeedDetalhe.__obj.Show();
		frm_Principal_pnlFeedDetalhe.__obj.exibir(oc);
		this.Hide();
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
