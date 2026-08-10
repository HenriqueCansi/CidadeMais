package obj.cidademais.Core;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import java.io.File;

import obj.cidademais.RvActivity;

public class CmCamera
{
	public interface Callback
	{
		void onSucesso(Uri fotoUri);

		void onErro(String erro);
	}

	public static final int REQUEST_CAMERA = 201;

	private static Uri fotoUriAtual;
	private static Callback callbackAtual;

	public static void tirarFoto(Callback callback)
	{
		callbackAtual = callback;

		File pastaImagens = new File(RvActivity.__activity.getCacheDir(), "images");

		if (!pastaImagens.exists())
			pastaImagens.mkdirs();

		File arquivoFoto = new File(pastaImagens, "IMG_" + System.currentTimeMillis() + ".jpg");

		fotoUriAtual = FileProvider.getUriForFile(
				RvActivity.__activity,
				RvActivity.__activity.getPackageName() + ".fileprovider",
				arquivoFoto);

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUriAtual);
		intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

		RvActivity.__activity.startActivityForResult(intent, REQUEST_CAMERA);
	}

	public static void onActivityResult(int requestCode, int resultCode)
	{
		if (requestCode != REQUEST_CAMERA)
			return;

		if (callbackAtual == null)
			return;

		if (resultCode == android.app.Activity.RESULT_OK)
			callbackAtual.onSucesso(fotoUriAtual);
		else
			callbackAtual.onErro("Nenhuma foto foi capturada.");

		callbackAtual = null;
	}
}
