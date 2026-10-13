package obj.cidademais.frm_Permissao.Panel;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import obj.cidademais.Core.CmPermissao;
import obj.cidademais.Core.Localizacao.PermissaoManager;
import obj.cidademais.R;
import obj.cidademais.RvActivity;
import obj.cidademais.RvView;

public class frm_Permissao_pnlBloqueio extends RvView
{
	public static frm_Permissao_pnlBloqueio __obj = new frm_Permissao_pnlBloqueio();
	private LinearLayout layout;

	@Override
	public LinearLayout getLayout()
	{
		if (layout == null)
		{
			LayoutInflater inflater = LayoutInflater.from(RvActivity.__activity);
			layout = (LinearLayout) inflater.inflate(R.layout.frm_permissao_pnlbloqueio, null);
		}
		return layout;
	}

	@Override
	public void OnActivated()
	{
		Button btnConceder = getLayout().findViewById(R.id.btnConcederPermissao);
		Button btnConfiguracoes = getLayout().findViewById(R.id.btnAbrirConfiguracoes);

		btnConceder.setOnClickListener(v -> solicitarPermissao());
		btnConfiguracoes.setOnClickListener(v -> abrirConfiguracoes());

		atualizarUi();
	}

	@Override
	public void onResume()
	{
		if (PermissaoManager.possuiPermissaoLocalizacao())
		{
			RvActivity.prosseguirParaApp();
			this.Hide();
			return;
		}

		atualizarUi();
	}

	private void solicitarPermissao()
	{
		CmPermissao.solicitarLocalizacao(new CmPermissao.Callback()
		{
			@Override
			public void onPermitido()
			{
				RvActivity.prosseguirParaApp();
				Hide();
			}

			@Override
			public void onNegado()
			{
				atualizarUi();
			}
		});
	}

	private void abrirConfiguracoes()
	{
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.setData(Uri.fromParts("package", RvActivity.__activity.getPackageName(), null));
		RvActivity.__activity.startActivity(intent);
	}

	private void atualizarUi()
	{
		TextView txtMensagem = getLayout().findViewById(R.id.txtMensagem);
		Button btnConfiguracoes = getLayout().findViewById(R.id.btnAbrirConfiguracoes);

		if (CmPermissao.isLocalizacaoNegadaPermanentemente())
		{
			txtMensagem.setText("Você negou a permissão de localização permanentemente. Abra as configurações do app e conceda a permissão de localização manualmente.");
			btnConfiguracoes.setVisibility(View.VISIBLE);
		}
		else
		{
			txtMensagem.setText("O CidadeMais precisa da sua localização para mostrar e registrar ocorrências perto de você. Conceda a permissão para continuar.");
			btnConfiguracoes.setVisibility(View.GONE);
		}
	}
}
