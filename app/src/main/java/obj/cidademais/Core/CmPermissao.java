package obj.cidademais.Core;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import obj.cidademais.RvActivity;

public class CmPermissao
{
	public interface Callback
	{
		void onPermitido();

		void onNegado();
	}

	public static final int REQUEST_LOCALIZACAO = 100;
	public static final int REQUEST_CAMERA = 101;
	public static final int REQUEST_GALERIA = 102;

	private static Callback callbackAtual;

	public static void solicitarLocalizacao(Callback callback)
	{
		callbackAtual = callback;

		if (ContextCompat.checkSelfPermission(
				RvActivity.__activity,
				Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED)
		{
			callback.onPermitido();
			return;
		}

		ActivityCompat.requestPermissions(
				RvActivity.__activity,
				new String[]{
						Manifest.permission.ACCESS_FINE_LOCATION,
						Manifest.permission.ACCESS_COARSE_LOCATION
				},
				REQUEST_LOCALIZACAO);
	}

	public static void onRequestPermissionsResult(
			int requestCode,
			int[] grantResults)
	{
		if (callbackAtual == null)
			return;

		if (requestCode == REQUEST_LOCALIZACAO)
		{
			if (grantResults.length > 0 &&
					grantResults[0] == PackageManager.PERMISSION_GRANTED)
			{
				callbackAtual.onPermitido();
			}
			else
			{
				callbackAtual.onNegado();
			}

			callbackAtual = null;
		}
	}
}