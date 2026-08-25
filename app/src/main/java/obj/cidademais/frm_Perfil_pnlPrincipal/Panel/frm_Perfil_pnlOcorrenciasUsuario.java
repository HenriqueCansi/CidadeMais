package obj.cidademais.frm_Perfil_pnlPrincipal.Panel;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import obj.cidademais.Core.CmConstantes;
import obj.cidademais.Core.CmData;
import obj.cidademais.Core.CmUi;
import obj.cidademais.Firebase.Ocorrencia.FirebaseOcorrencia;
import obj.cidademais.R;
import obj.cidademais.RvActivity;
import obj.cidademais.RvView;
import obj.cidademais.frm_Login.Data.Usuario;
import obj.cidademais.frm_Principal.Data.Ocorrencia;
import obj.cidademais.frm_Principal.Panel.frm_Principal_pnlFeedDetalhe;

public class frm_Perfil_pnlOcorrenciasUsuario extends RvView
{
	public static frm_Perfil_pnlOcorrenciasUsuario __obj = new frm_Perfil_pnlOcorrenciasUsuario();
	private LinearLayout layout;
	private LinearLayout llLista;
	private TextView tvTitulo;
	private TextView tvVazio;
	private ImageView ivFechar;
	private Usuario usuarioAlvo;

	@Override
	public LinearLayout getLayout()
	{
		if (layout == null)
		{
			LayoutInflater inflater = LayoutInflater.from(RvActivity.__activity);
			layout = (LinearLayout) inflater.inflate(R.layout.frm_perfil_pnlocorrenciasusuario, null);
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
		tvTitulo = layout.findViewById(R.id.tvTitulo);
		llLista = layout.findViewById(R.id.llLista);
		tvVazio = layout.findViewById(R.id.tvVazio);

		ivFechar.setOnClickListener(v -> {
			frm_Perfil_pnlPerfilPublico.__obj.Show();
			Hide();
		});

		if (usuarioAlvo != null)
			tvTitulo.setText("Ocorrências de " + usuarioAlvo.nome);

		carregarOcorrencias();
	}

	private void carregarOcorrencias()
	{
		if (usuarioAlvo == null)
			return;

		FirebaseOcorrencia.listarPorUsuario(usuarioAlvo.uid, new FirebaseOcorrencia.CallbackListagem()
		{
			@Override
			public void onSucesso(List<Ocorrencia> lista)
			{
				montarLista(lista);
			}

			@Override
			public void onErro(Exception e)
			{
				Toast.makeText(RvActivity.__activity, e.getMessage(), Toast.LENGTH_SHORT).show();
				tvVazio.setText("Não foi possível carregar as ocorrências.");
				tvVazio.setVisibility(View.VISIBLE);
			}
		});
	}

	private void montarLista(List<Ocorrencia> lista)
	{
		List<Ocorrencia> ordenadas = new ArrayList<>(lista);
		Collections.sort(ordenadas, (a, b) -> {
			if (a.criadoEm == null || b.criadoEm == null)
				return 0;
			return b.criadoEm.compareTo(a.criadoEm);
		});

		llLista.removeAllViews();
		tvVazio.setText("Nenhuma ocorrência publicada por " + usuarioAlvo.nome + " ainda.");
		tvVazio.setVisibility(ordenadas.isEmpty() ? View.VISIBLE : View.GONE);

		for (Ocorrencia oc : ordenadas)
			llLista.addView(criarCard(oc));
	}

	private View criarCard(Ocorrencia oc)
	{
		LayoutInflater inflater = LayoutInflater.from(RvActivity.__activity);
		View card = inflater.inflate(R.layout.item_feed_recente, llLista, false);
		CmUi.arredondar(card, 16);

		ImageView imgAvatar = card.findViewById(R.id.imgAvatar);
		TextView tvNome = card.findViewById(R.id.tvNome);
		TextView tvTempo = card.findViewById(R.id.tvTempo);
		TextView tvBairro = card.findViewById(R.id.tvBairro);
		TextView tvStatus = card.findViewById(R.id.tvStatus);
		TextView tvTituloCard = card.findViewById(R.id.tvTitulo);
		ImageView imgFoto = card.findViewById(R.id.imgFoto);

		tvNome.setText(usuarioAlvo.nome);

		if (usuarioAlvo.fotoPerfil != null && !usuarioAlvo.fotoPerfil.isEmpty())
			Glide.with(RvActivity.__activity).load(usuarioAlvo.fotoPerfil).into(imgAvatar);

		tvTempo.setText(CmData.tempoRelativo(oc.criadoEm));
		tvBairro.setText(oc.bairro != null ? oc.bairro : "");
		tvStatus.setText(CmConstantes.rotuloStatus(oc.status));
		tvStatus.getBackground().mutate().setTint(CmConstantes.corStatus(oc.status));
		tvTituloCard.setText(oc.titulo);

		CmUi.arredondar(imgFoto, 16);

		if (oc.fotos != null && !oc.fotos.isEmpty())
			Glide.with(RvActivity.__activity).load(oc.fotos.get(0)).into(imgFoto);

		card.setOnClickListener(v -> {
			if (frm_Principal_pnlFeedDetalhe.__obj == null)
				frm_Principal_pnlFeedDetalhe.__obj = new frm_Principal_pnlFeedDetalhe();

			frm_Principal_pnlFeedDetalhe.__obj.Show();
			frm_Principal_pnlFeedDetalhe.__obj.exibir(oc);
			Hide();
		});

		return card;
	}
}
