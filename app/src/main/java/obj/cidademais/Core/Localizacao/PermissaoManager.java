package obj.cidademais.Core.Localizacao;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import obj.cidademais.RvActivity;

public class PermissaoManager
{
	public static final int REQUEST_LOCALIZACAO = 100;

	public static boolean possuiPermissaoLocalizacao()
	{
		return ContextCompat.checkSelfPermission(
				RvActivity.__activity,
				Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED;
	}

	public static void solicitarPermissaoLocalizacao()
	{
		ActivityCompat.requestPermissions(
				RvActivity.__activity,
				new String[]{
						Manifest.permission.ACCESS_FINE_LOCATION,
						Manifest.permission.ACCESS_COARSE_LOCATION
				},
				REQUEST_LOCALIZACAO);
	}
}