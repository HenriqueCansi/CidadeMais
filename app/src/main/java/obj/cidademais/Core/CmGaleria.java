package obj.cidademais.Core;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import obj.cidademais.RvActivity;

public class CmGaleria
{
	public interface Callback
	{
		void onSucesso(Uri fotoUri);

		void onErro(String erro);
	}

	public static final int REQUEST_GALERIA = 202;

	private static Callback callbackAtual;

	public static void abrirGaleria(Callback callback)
	{
		callbackAtual = callback;

		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");

		RvActivity.__activity.startActivityForResult(intent, REQUEST_GALERIA);
	}

	public static void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode != REQUEST_GALERIA)
			return;

		if (callbackAtual == null)
			return;

		if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null)
			callbackAtual.onSucesso(data.getData());
		else
			callbackAtual.onErro("Nenhuma imagem selecionada.");

		callbackAtual = null;
	}
}
