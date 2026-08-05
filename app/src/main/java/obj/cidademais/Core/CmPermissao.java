package obj.cidademais.Core;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

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
		if (requestCode != REQUEST_PERMISSOES)
			return;

		if (callbackAtual == null)
			return;

		for (int resultado : grantResults)
		{
			if (resultado != PackageManager.PERMISSION_GRANTED)
			{
				callbackAtual.onNegado();
				callbackAtual = null;
				return;
			}
		}

		callbackAtual.onPermitido();
		callbackAtual = null;
	}

	public static final int REQUEST_PERMISSOES = 103;

	public static void solicitarPermissoes(Callback callback)
	{
		callbackAtual = callback;

		ArrayList<String> permissoes = new ArrayList<>();

		// Localização
		if (ContextCompat.checkSelfPermission(RvActivity.__activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
			permissoes.add(Manifest.permission.ACCESS_FINE_LOCATION);
		
		if (ContextCompat.checkSelfPermission(RvActivity.__activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
			permissoes.add(Manifest.permission.ACCESS_COARSE_LOCATION);

		// Câmera
		if (ContextCompat.checkSelfPermission(RvActivity.__activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
			permissoes.add(Manifest.permission.CAMERA);

		// Galeria (Imagens e Vídeos)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
		{
			if (ContextCompat.checkSelfPermission(RvActivity.__activity, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)
				permissoes.add(Manifest.permission.READ_MEDIA_IMAGES);
			
			if (ContextCompat.checkSelfPermission(RvActivity.__activity, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED)
				permissoes.add(Manifest.permission.READ_MEDIA_VIDEO);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
			{
				if (ContextCompat.checkSelfPermission(RvActivity.__activity, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) != PackageManager.PERMISSION_GRANTED)
					permissoes.add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
			}
		}
		else
		{
			if (ContextCompat.checkSelfPermission(RvActivity.__activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
				permissoes.add(Manifest.permission.READ_EXTERNAL_STORAGE);
		}

		if (permissoes.isEmpty())
		{
			callback.onPermitido();
			callbackAtual = null;
		}
		else
		{
			ActivityCompat.requestPermissions(
					RvActivity.__activity,
					permissoes.toArray(new String[0]),
					REQUEST_PERMISSOES);
		}
	}
}