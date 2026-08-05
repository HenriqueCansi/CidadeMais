package obj.cidademais;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.LinearLayout;
import java.util.ArrayList;

import obj.cidademais.Core.Localizacao.CmPosicao;
import obj.cidademais.Core.Localizacao.LocalizacaoManager;
import obj.cidademais.frm_Login.Panel.frm_Login_pnlLogin;
import obj.cidademais.frm_Principal.Panel.frm_Principal_pnlPrincipal;
import com.google.firebase.FirebaseApp;
import androidx.annotation.NonNull;

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

		CmPermissao.solicitarPermissoes(new CmPermissao.Callback()
		{
			@Override
			public void onPermitido()
			{
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

				frm_Login_pnlLogin.__obj.Show();
			}

			@Override
			public void onNegado()
			{
				// Mesmo que negue algumas, vamos seguir para o login? 
				// Ou o usuário quer que peça tudo de novo?
				// Geralmente seguimos para o login, mas algumas funções podem falhar depois.
				frm_Login_pnlLogin.__obj.Show();
			}
		});
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
