package obj.cidademais.frm_Principal.Panel;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import obj.cidademais.Core.CmConstantes;
import obj.cidademais.Core.CmUi;
import obj.cidademais.Core.Localizacao.CmGeo;
import obj.cidademais.Firebase.Ocorrencia.FirebaseOcorrencia;
import obj.cidademais.R;
import obj.cidademais.RvActivity;
import obj.cidademais.RvView;
import obj.cidademais.frm_Login.Classe.CallbackCadastro;
import obj.cidademais.frm_Login.Data.Sessao;
import obj.cidademais.frm_Principal.Data.Ocorrencia;

public class frm_Principal_pnlOcorrenciaProxima extends RvView
{
	public static frm_Principal_pnlOcorrenciaProxima __obj = new frm_Principal_pnlOcorrenciaProxima();
	private LinearLayout layout;

	private List<Ocorrencia> proximas;
	private Ocorrencia novaOcorrencia;
	private List<Uri> fotosNovaOcorrencia;

	private ImageView ivFechar;
	private LinearLayout llProximas;
	private Button btnCadastrarMesmoAssim;

	@Override
	public LinearLayout getLayout()
	{
		if (layout == null)
		{
			LayoutInflater inflater = LayoutInflater.from(RvActivity.__activity);
			layout = (LinearLayout) inflater.inflate(R.layout.frm_principal_pnlocorrenciaproxima, null);
		}
		return layout;
	}

	public void preparar(List<Ocorrencia> proximas, Ocorrencia novaOcorrencia, List<Uri> fotosNovaOcorrencia)
	{
		this.proximas = proximas;
		this.novaOcorrencia = novaOcorrencia;
		this.fotosNovaOcorrencia = fotosNovaOcorrencia;
	}

	@Override
	public void OnActivated()
	{
		ivFechar = layout.findViewById(R.id.ivFechar);
		llProximas = layout.findViewById(R.id.llProximas);
		btnCadastrarMesmoAssim = layout.findViewById(R.id.btnCadastrarMesmoAssim);

		ivFechar.setOnClickListener(v -> {
			frm_Principal_pnlOcorrencia.__obj.Show();
			this.Hide();
		});

		btnCadastrarMesmoAssim.setEnabled(true);
		btnCadastrarMesmoAssim.setOnClickListener(v -> cadastrarNovaOcorrencia());

		montarLista();
	}

	private void montarLista()
	{
		llProximas.removeAllViews();

		for (Ocorrencia oc : proximas)
			llProximas.addView(criarCard(oc));
	}

	private View criarCard(Ocorrencia oc)
	{
		LayoutInflater inflater = LayoutInflater.from(RvActivity.__activity);
		View card = inflater.inflate(R.layout.item_ocorrencia_proxima, llProximas, false);
		CmUi.arredondar(card, 16);

		ImageView imgFoto = card.findViewById(R.id.imgFoto);
		TextView tvTitulo = card.findViewById(R.id.tvTitulo);
		TextView tvStatus = card.findViewById(R.id.tvStatus);
		TextView tvDistancia = card.findViewById(R.id.tvDistancia);
		Button btnConfirmar = card.findViewById(R.id.btnConfirmar);

		tvTitulo.setText(oc.titulo);
		tvStatus.setText(CmConstantes.rotuloStatus(oc.status));
		tvStatus.getBackground().mutate().setTint(CmConstantes.corStatus(oc.status));

		double distanciaKm = CmGeo.distanciaKm(
				novaOcorrencia.latitude, novaOcorrencia.longitude,
				oc.latitude, oc.longitude);
		int metros = (int) Math.round(distanciaKm * 1000);

		String bairro = oc.bairro != null ? oc.bairro + " • " : "";
		tvDistancia.setText(bairro + "a " + metros + "m daqui");

		CmUi.arredondar(imgFoto, 12);

		if (oc.fotos != null && !oc.fotos.isEmpty())
			Glide.with(RvActivity.__activity).load(oc.fotos.get(0)).into(imgFoto);

		btnConfirmar.setOnClickListener(v -> confirmarExistente(oc, btnConfirmar));

		return card;
	}

	private void confirmarExistente(Ocorrencia existente, Button botao)
	{
		if (!Sessao.isLogado())
			return;

		botao.setEnabled(false);
		String uid = Sessao.getUsuario().uid;

		RvActivity.mostrarCarregando("Confirmando...");

		FirebaseOcorrencia.alternarConfirmacao(existente.id, uid, true, new CallbackCadastro()
		{
			@Override
			public void onSucesso()
			{
				RvActivity.esconderCarregando();

				Toast.makeText(RvActivity.__activity,
						"Confirmação registrada nesse problema.",
						Toast.LENGTH_SHORT).show();

				abrirDetalhe(existente);
			}

			@Override
			public void onErro(Exception e)
			{
				RvActivity.esconderCarregando();

				Toast.makeText(RvActivity.__activity, e.getMessage(), Toast.LENGTH_SHORT).show();
				botao.setEnabled(true);
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

	private void cadastrarNovaOcorrencia()
	{
		btnCadastrarMesmoAssim.setEnabled(false);

		RvActivity.mostrarCarregando("Enviando...");

		FirebaseOcorrencia.cadastrarComFotos(novaOcorrencia, fotosNovaOcorrencia, new CallbackCadastro()
		{
			@Override
			public void onSucesso()
			{
				RvActivity.esconderCarregando();

				Toast.makeText(RvActivity.__activity, "Ocorrência cadastrada!", Toast.LENGTH_SHORT).show();

				frm_Principal_pnlPrincipal.__obj.Show();
				Hide();
			}

			@Override
			public void onErro(Exception e)
			{
				RvActivity.esconderCarregando();

				Toast.makeText(RvActivity.__activity, e.getMessage(), Toast.LENGTH_LONG).show();
				btnCadastrarMesmoAssim.setEnabled(true);
			}
		});
	}
}
