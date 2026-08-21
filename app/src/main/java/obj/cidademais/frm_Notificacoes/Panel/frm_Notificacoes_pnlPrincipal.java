package obj.cidademais.frm_Notificacoes.Panel;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import obj.cidademais.Core.CmData;
import obj.cidademais.Core.CmUi;
import obj.cidademais.Firebase.Notificacao.FirebaseNotificacao;
import obj.cidademais.Firebase.Ocorrencia.FirebaseOcorrencia;
import obj.cidademais.R;
import obj.cidademais.RvActivity;
import obj.cidademais.RvView;
import obj.cidademais.frm_Login.Data.Sessao;
import obj.cidademais.frm_Notificacoes.Data.Notificacao;
import obj.cidademais.frm_Perfil_pnlPrincipal.Panel.frm_Perfil_pnlMeusProblemas;
import obj.cidademais.frm_Principal.Data.Ocorrencia;
import obj.cidademais.frm_Principal.Panel.frm_Principal_pnlFeedDetalhe;
import obj.cidademais.frm_Principal.Panel.frm_Principal_pnlPrincipal;

public class frm_Notificacoes_pnlPrincipal extends RvView
{
	public static frm_Notificacoes_pnlPrincipal __obj = new frm_Notificacoes_pnlPrincipal();
	private LinearLayout layout;
	private LinearLayout llLista;
	private TextView tvVazio;
	private ImageView ivFechar;

	@Override
	public LinearLayout getLayout()
	{
		if (layout == null)
		{
			LayoutInflater inflater = LayoutInflater.from(RvActivity.__activity);
			layout = (LinearLayout) inflater.inflate(R.layout.frm_notificacoes_pnlprincipal, null);
		}
		return layout;
	}

	@Override
	public void OnActivated()
	{
		ivFechar = layout.findViewById(R.id.ivFechar);
		llLista = layout.findViewById(R.id.llLista);
		tvVazio = layout.findViewById(R.id.tvVazio);

		//ivFechar.setOnClickListener(v -> this.Hide());

		ivFechar.setOnClickListener(v -> {

			frm_Principal_pnlPrincipal.__obj.Show();
			Hide();
		});

		carregarNotificacoes();
	}

	private void carregarNotificacoes()
	{
		if (!Sessao.isLogado())
		{
			tvVazio.setText("Faça login para ver suas notificações.");
			tvVazio.setVisibility(View.VISIBLE);
			return;
		}

		FirebaseNotificacao.listarPorUsuario(Sessao.getUsuario().uid, new FirebaseNotificacao.CallbackListagem()
		{
			@Override
			public void onSucesso(List<Notificacao> lista)
			{
				montarLista(lista);
			}

			@Override
			public void onErro(Exception e)
			{
				Toast.makeText(RvActivity.__activity, e.getMessage(), Toast.LENGTH_SHORT).show();
				tvVazio.setText("Não foi possível carregar suas notificações.");
				tvVazio.setVisibility(View.VISIBLE);
			}
		});
	}

	private void montarLista(List<Notificacao> lista)
	{
		List<Notificacao> ordenadas = new ArrayList<>(lista);
		Collections.sort(ordenadas, (a, b) -> {
			if (a.criadoEm == null || b.criadoEm == null)
				return 0;
			return b.criadoEm.compareTo(a.criadoEm);
		});

		llLista.removeAllViews();
		tvVazio.setVisibility(ordenadas.isEmpty() ? View.VISIBLE : View.GONE);

		for (Notificacao notificacao : ordenadas)
			llLista.addView(criarCard(notificacao));
	}

	private View criarCard(Notificacao notificacao)
	{
		LayoutInflater inflater = LayoutInflater.from(RvActivity.__activity);
		View card = inflater.inflate(R.layout.item_notificacao, llLista, false);
		CmUi.arredondar(card, 16);

		TextView tvTitulo = card.findViewById(R.id.tvTitulo);
		TextView tvCorpo = card.findViewById(R.id.tvCorpo);
		TextView tvTempo = card.findViewById(R.id.tvTempo);

		tvTitulo.setText(notificacao.titulo);
		tvCorpo.setText(notificacao.corpo);
		tvTempo.setText(CmData.tempoRelativo(notificacao.criadoEm));

		card.setOnClickListener(v -> abrirOcorrencia(notificacao));

		return card;
	}

	private void abrirOcorrencia(Notificacao notificacao)
	{
		if (notificacao.ocorrenciaId == null)
			return;

		FirebaseOcorrencia.buscarPorId(notificacao.ocorrenciaId, new FirebaseOcorrencia.CallbackBusca()
		{
			@Override
			public void onSucesso(Ocorrencia ocorrencia)
			{
				if (frm_Principal_pnlFeedDetalhe.__obj == null)
					frm_Principal_pnlFeedDetalhe.__obj = new frm_Principal_pnlFeedDetalhe();

				frm_Principal_pnlFeedDetalhe.__obj.Show();
				frm_Principal_pnlFeedDetalhe.__obj.exibir(ocorrencia);
				Hide();
			}

			@Override
			public void onErro(Exception e)
			{
				Toast.makeText(RvActivity.__activity, "Essa ocorrência não existe mais.", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
