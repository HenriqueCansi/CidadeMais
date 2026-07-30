package obj.cidademais.Core.Localizacao;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import obj.cidademais.RvActivity;

public class LocalizacaoManager
{

	public interface Callback
	{
		void onSucesso(CmPosicao posicao);

		void onErro(String erro);
	}

	@SuppressLint("MissingPermission")
	public static void buscar(Callback callback)
	{
		if (ActivityCompat.checkSelfPermission(
				RvActivity.__activity,
				Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
		{
			callback.onErro("Permissão de localização negada.");
			return;
		}

		FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(RvActivity.__activity);

		client.getCurrentLocation(
				Priority.PRIORITY_HIGH_ACCURACY,
				new CancellationToken()
				{
					@Override
					public CancellationToken onCanceledRequested(OnTokenCanceledListener listener)
					{
						return this;
					}

					@Override
					public boolean isCancellationRequested()
					{
						return false;
					}
				})
				.addOnSuccessListener(location -> {
					if (location == null)
					{
						callback.onErro("Não foi possível obter a localização.");
						return;
					}

					CmPosicao posicao = new CmPosicao();

					posicao.latitude = location.getLatitude();
					posicao.longitude = location.getLongitude();

					try
					{
						Geocoder geocoder = new Geocoder(RvActivity.__activity, new Locale("pt", "BR"));

						List<Address> lista = geocoder.getFromLocation(
								posicao.latitude,
								posicao.longitude,
								1);

						if (lista != null && !lista.isEmpty())
						{
							Address a = lista.get(0);

							posicao.localizacao.endereco = a.getThoroughfare();

							if (a.getSubThoroughfare() != null)
								posicao.localizacao.endereco += ", " + a.getSubThoroughfare();

							posicao.localizacao.bairro = a.getSubLocality();
							posicao.localizacao.cidade = a.getLocality();
							posicao.localizacao.estado = a.getAdminArea();
						}

						callback.onSucesso(posicao);

					}
					catch (IOException e)
					{
						callback.onErro(e.getMessage());
					}

				})
				.addOnFailureListener(e -> callback.onErro(e.getMessage()));
	}

}