package obj.cidademais.frm_Principal.Panel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import obj.cidademais.R;
import obj.cidademais.RvActivity;
import obj.cidademais.RvView;
import obj.cidademais.frm_Perfil_pnlPrincipal.Panel.frm_Perfil_pnlPrincipal;

public class frm_Principal_pnlPrincipal extends RvView implements OnMapReadyCallback
{
	public static frm_Principal_pnlPrincipal __obj = new frm_Principal_pnlPrincipal();
	private LinearLayout layout;
	private GoogleMap googleMap;
	public MapView mapView;
	private Bundle mapViewBundle;

	@Override
	public LinearLayout getLayout()
	{
		if (layout == null)
		{
			LayoutInflater inflater = LayoutInflater.from(RvActivity.__activity);
			layout = (LinearLayout) inflater.inflate(R.layout.frm_principal_pnlprincipal, null);
		}
		return layout;
	}

	@Override
	public void OnActivated()
	{
		// Criar novo Bundle se necessário
		if (mapViewBundle == null)
		{
			mapViewBundle = new Bundle();
		}

		mapView = layout.findViewById(R.id.mapView);
		mapView.onCreate(new Bundle());
		mapView.getMapAsync(this);

		EditText etSearch = layout.findViewById(R.id.etSearch);
		ImageView ivNotification = layout.findViewById(R.id.ivNotification);
		ImageView ivMap = layout.findViewById(R.id.ivMap);
		ImageView ivAdd = layout.findViewById(R.id.ivAdd);
		ImageView ivProfile = layout.findViewById(R.id.ivProfile);

		RadioGroup rgStatus = layout.findViewById(R.id.rgStatus);
		rgStatus.setOnCheckedChangeListener((group, checkedId) -> {
			// Alternar entre "Em discussão" e "Solucionados"
		});

		ivNotification.setOnClickListener(v -> {
			// Ação da notificação
		});

		ivMap.setOnClickListener(v -> {
			// Ação do mapa
		});

		ivAdd.setOnClickListener(v -> {
			// Ação do adicionar
		});

		ivProfile.setOnClickListener(v -> {
			if(frm_Perfil_pnlPrincipal.__obj == null)
				frm_Perfil_pnlPrincipal.__obj = new frm_Perfil_pnlPrincipal();

			frm_Perfil_pnlPrincipal.__obj.Show();
			this.Hide();

		});
	}

	// Método de retorno do mapa pronto
	@Override
	public void onMapReady(GoogleMap map)
	{
		this.googleMap = map;
		// Aqui você pode configurar o mapa (zoom, marcadores etc.)
	}
}