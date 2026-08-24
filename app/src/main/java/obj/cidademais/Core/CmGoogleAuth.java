package obj.cidademais.Core;

import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;

import obj.cidademais.R;
import obj.cidademais.RvActivity;

public class CmGoogleAuth
{
	public interface Callback
	{
		void onSucesso(String idToken);

		void onErro(String erro);
	}

	public static final int REQUEST_GOOGLE_SIGN_IN = 203;

	private static GoogleSignInClient client;
	private static Callback callbackAtual;

	private static GoogleSignInClient getClient()
	{
		if (client == null)
		{
			GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
					.requestIdToken(RvActivity.__activity.getString(R.string.default_web_client_id))
					.requestEmail()
					.build();

			client = GoogleSignIn.getClient(RvActivity.__activity, gso);
		}
		return client;
	}

	public static void entrar(Callback callback)
	{
		callbackAtual = callback;

		Intent intent = getClient().getSignInIntent();
		RvActivity.__activity.startActivityForResult(intent, REQUEST_GOOGLE_SIGN_IN);
	}

	public static void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode != REQUEST_GOOGLE_SIGN_IN)
			return;

		if (callbackAtual == null)
			return;

		try
		{
			GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
			callbackAtual.onSucesso(account.getIdToken());
		}
		catch (ApiException e)
		{
			callbackAtual.onErro("Não foi possível entrar com Google (código " + e.getStatusCode() + ").");
		}

		callbackAtual = null;
	}

	public static void signOut()
	{
		if (client != null)
			client.signOut();
	}
}
