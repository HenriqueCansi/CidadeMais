package obj.cidademais;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import java.util.ArrayList;

import obj.cidademais.Core.Localizacao.CmPosicao;
import obj.cidademais.Core.Localizacao.LocalizacaoManager;
import obj.cidademais.Core.Localizacao.PermissaoManager;
import obj.cidademais.frm_Login.Panel.frm_Login_pnlLogin;
import obj.cidademais.frm_Permissao.Panel.frm_Permissao_pnlBloqueio;
import obj.cidademais.frm_Principal.Panel.frm_Principal_pnlPrincipal;
import com.google.firebase.FirebaseApp;
import androidx.annotation.NonNull;

import obj.cidademais.Core.CmCamera;
import obj.cidademais.Core.CmGaleria;
import obj.cidademais.Core.CmGoogleAuth;
import obj.cidademais.Core.CmPermissao;

public class RvActivity extends Activity
{
	public static RvActivity __activity;
	public static ArrayList<RvView> __views = new ArrayList<>();
	private static ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		FirebaseApp.initializeApp(this);
		__activity = this;

		criarCanalNotificacao();

		CmPermissao.solicitarPermissoes(new CmPermissao.Callback()
		{
			@Override
			public void onPermitido()
			{
				avaliarPermissaoLocalizacao();
			}

			@Override
			public void onNegado()
			{
				// O lote de permissões inclui câmera/mídia/notificações além da
				// localização — só a localização deve bloquear a entrada no app.
				avaliarPermissaoLocalizacao();
			}
		});
	}

	private static void avaliarPermissaoLocalizacao()
	{
		if (PermissaoManager.possuiPermissaoLocalizacao())
			prosseguirParaApp();
		else
			frm_Permissao_pnlBloqueio.__obj.Show();
	}

	public static void prosseguirParaApp()
	{
		buscarLocalizacaoInicial();
		frm_Login_pnlLogin.__obj.Show();
	}

	private static void buscarLocalizacaoInicial()
	{
		if (!PermissaoManager.possuiPermissaoLocalizacao())
			return;

		LocalizacaoManager.buscar(new LocalizacaoManager.Callback()
		{
			@Override
			public void onSucesso(CmPosicao posicao)
			{
				CmPosicao.posicaoAtual = posicao;
			}

			@Override
			public void onErro(String erro)
			{
			}
		});
	}

	private void criarCanalNotificacao()
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
			return;

		NotificationChannel canal = new NotificationChannel(
				"ocorrencias_status",
				"Atualizações de Ocorrências",
				NotificationManager.IMPORTANCE_HIGH);

		getSystemService(NotificationManager.class).createNotificationChannel(canal);
	}

	public static LinearLayout criaTela(RvView view)
	{
		return view.getLayout();
	}

	public static void mostrarCarregando(String mensagem)
	{
		if (progressDialog == null)
		{
			progressDialog = new ProgressDialog(__activity);
			progressDialog.setCancelable(false);
		}
		progressDialog.setMessage(mensagem);
		progressDialog.show();
	}

	public static void esconderCarregando()
	{
		if (progressDialog != null && progressDialog.isShowing())
		{
			progressDialog.dismiss();
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if (frm_Principal_pnlPrincipal.__obj.mapView != null)
			frm_Principal_pnlPrincipal.__obj.mapView.onResume();

		if (!__views.isEmpty())
			__views.get(__views.size() - 1).onResume();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		if (frm_Principal_pnlPrincipal.__obj.mapView != null)
			frm_Principal_pnlPrincipal.__obj.mapView.onPause();
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		if (frm_Principal_pnlPrincipal.__obj.mapView != null)
			frm_Principal_pnlPrincipal.__obj.mapView.onStart();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		if (frm_Principal_pnlPrincipal.__obj.mapView != null)
			frm_Principal_pnlPrincipal.__obj.mapView.onStop();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (frm_Principal_pnlPrincipal.__obj.mapView != null)
			frm_Principal_pnlPrincipal.__obj.mapView.onDestroy();
	}

	@Override
	public void onLowMemory()
	{
		super.onLowMemory();
		if (frm_Principal_pnlPrincipal.__obj.mapView != null)
			frm_Principal_pnlPrincipal.__obj.mapView.onLowMemory();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		CmCamera.onActivityResult(requestCode, resultCode);
		CmGaleria.onActivityResult(requestCode, resultCode, data);
		CmGoogleAuth.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(
				requestCode,
				permissions,
				grantResults);

		CmPermissao.onRequestPermissionsResult(
				requestCode,
				grantResults);
	}
}
